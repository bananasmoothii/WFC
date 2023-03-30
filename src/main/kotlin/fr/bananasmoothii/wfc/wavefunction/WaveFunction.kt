package fr.bananasmoothii.wfc.wavefunction

import fr.bananasmoothii.wfc.space.Bounds
import fr.bananasmoothii.wfc.space.Coords
import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Direction
import fr.bananasmoothii.wfc.tile.Rotatable
import fr.bananasmoothii.wfc.tile.Tile
import fr.bananasmoothii.wfc.tile.TileSet
import fr.bananasmoothii.wfc.util.*
import fr.bananasmoothii.wfc.wavefunction.WaveFunction.PropagationTask.Companion.addPropagationTasksInBoundsToList
import kotlin.random.Random

open class WaveFunction<C : Rotatable<D>, D : Dimension<D>>(
    val tileSet: TileSet<C, D>,
    protected val random: Random = Random.Default,
) : Iterable<Map.Entry<Coords<D>, List<Tile<C, D>>>>, Versionable {

    init {
        require(!tileSet.canCreateNewPieces) { "You have to finish creating the TileSet before using it in generation" }
    }

    protected var map = mutableMapOf<Coords<D>, LongArray>()

    protected var lastCoordsWherePicked: Coords<D>? = null
    protected var lastTileIdPicked: Int? = null

    protected val maxId = tileSet.maxId
    protected val maxEntropyArray = tileSet.maxEntropyArray
    protected val arraySize = tileSet.arraySize

    //private val dispatcher = newSingleThreadContext("Propagation thread")

    fun collapse(bounds: Bounds<D>) {
        for (coords in bounds) {
            val tilesAtCoordsArray = map[coords]
            if (tilesAtCoordsArray?.hasSingle1Bit() == true) continue
            commit()
            // there are multiple possibilities, so we pick one
            lastCoordsWherePicked = coords
            val tileId = chooseTile(tilesAtCoordsArray)
            lastTileIdPicked = tileId
            collapse(coords, tileId)
            try {
                propagateFrom(coords, bounds)
            } catch (e: PropagationException) {
                rollback()
                // remove that bad choice
                if (tilesAtCoordsArray == null) map[coords] = maxEntropyArray.copyOf().also { it.set0BitAt(tileId) }
                else tilesAtCoordsArray.set0BitAt(tileId)
            }
        }
    }

    fun chooseTile(options: LongArray?): Int {
        if (options == null) return random.nextInt(maxId + 1)
        return tileSet.pickTile(options, random)
    }

    fun collapse(coords: Coords<D>, chosenTile: Int = chooseTile(map[coords])) {
        val tilesAtCoordsArray = map[coords]
        if (tilesAtCoordsArray != null)
            tilesAtCoordsArray.setAll0AndSet1BitAt(chosenTile)
        else
            map[coords] = LongArray(arraySize).also { it.set1BitAt(chosenTile) }
    }

    /**
     * @throws PropagationException if an empty state is found
     */
    fun propagateFrom(latestCollapse: Coords<D>, bounds: Bounds<D>) {
        val needPropagationCoords: MutableList<PropagationTask<C, D>> = mutableListOf()
        latestCollapse.addPropagationTasksInBoundsToList(needPropagationCoords, bounds)

        while (needPropagationCoords.isNotEmpty()) {
            val needPropagationCoordsCopy = needPropagationCoords.toMutableList()
            needPropagationCoords.clear()

            for ((center, direction) in needPropagationCoordsCopy) {
                // TODO: fork (multithreading) (is it good to do it here ?)

                val taskPointsTo = center.move(direction)
                val tilesAtCoordsArray = map[taskPointsTo] ?: maxEntropyArray.copyOf()
                val currentLocationMaskForDirection = LongArray(arraySize)
                tileSet.getTileList(map[center]!!).forEach {
                    it.addAllowedNeighborsToArray(currentLocationMaskForDirection, direction)
                }

                if (tilesAtCoordsArray bitAndEquals currentLocationMaskForDirection) { // removing impossible neighbors
                    // tilesAtCoordsArray has changed
                    if (tilesAtCoordsArray.hasOnly0Bits()) throw PropagationException(taskPointsTo)

                    taskPointsTo.addPropagationTasksInBoundsToList(needPropagationCoords, bounds)
                }
            }
        }
    }

    protected data class PropagationTask<C : Rotatable<D>, D : Dimension<D>>(
        val center: Coords<D>,
        val direction: Direction<D>
    ) {
        fun pointsTo() = center.move(direction)

        companion object {
            fun <C : Rotatable<D>, D : Dimension<D>> Coords<D>.addPropagationTasksInBoundsToList(
                list: MutableList<PropagationTask<C, D>>,
                bounds: Bounds<D>
            ) {
                dimension.directionsAt(this).forEach { direction ->
                    if (this.move(direction) in bounds)
                        list += PropagationTask(this, direction)
                }
            }
        }
    }

    class PropagationException(message: String) : Exception(message) {
        constructor(coords: Coords<*>) : this("Found an empty node at $coords")
    }


    protected open inner class State(
        val map: MutableMap<Coords<D>, LongArray>,
        val lastCoordsWherePicked: Coords<D>?,
        val lastTileIdPicked: Int?
    )

    protected val states = mutableListOf<State>()

    override fun commit() {
        states += State(map.toMutableMap(), lastCoordsWherePicked, lastTileIdPicked)
    }

    override fun canRollback(): Boolean = states.isNotEmpty()

    override fun rollback() {
        val state = states.removeLast()
        map = state.map
        lastCoordsWherePicked = state.lastCoordsWherePicked
        lastTileIdPicked = state.lastTileIdPicked
    }

    override fun iterator(): Iterator<Map.Entry<Coords<D>, List<Tile<C, D>>>> =
        map.asSequence().map { (coords, tilesAtCoordsArray) ->
            object : Map.Entry<Coords<D>, List<Tile<C, D>>> {
                override val key: Coords<D> = coords
                override val value: List<Tile<C, D>> = tileSet.getTileList(tilesAtCoordsArray)
            }
        }.iterator()

    fun getTileList(coords: Coords<D>): List<Tile<C, D>>? = map[coords]?.let { tileSet.getTileList(it) }

    fun setTileList(coords: Coords<D>, tiles: List<Tile<C, D>>) {
        val array = LongArray(arraySize)
        for (tile in tiles) {
            array.set1BitAt(tile.id)
        }
        map[coords] = array
    }
}

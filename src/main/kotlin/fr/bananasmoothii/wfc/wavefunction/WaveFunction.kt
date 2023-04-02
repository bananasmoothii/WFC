package fr.bananasmoothii.wfc.wavefunction

import fr.bananasmoothii.wfc.space.Bounds
import fr.bananasmoothii.wfc.space.Coords
import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Direction
import fr.bananasmoothii.wfc.tile.AbstractTileSet
import fr.bananasmoothii.wfc.tile.Rotatable
import fr.bananasmoothii.wfc.tile.Tile
import fr.bananasmoothii.wfc.util.*
import kotlin.random.Random

open class WaveFunction<C : Rotatable<D>, D : Dimension<D>>(
    val tileSet: AbstractTileSet<C, D>,
    protected val random: Random = Random.Default,
    var onCollapse: ((Coords<D>, Tile<C, D>) -> Unit)? = null,
) : Iterable<Map.Entry<Coords<D>, List<Tile<C, D>>>>, Versionable {

    init {
        require(!tileSet.canCreateNewPieces) { "You have to finish creating the TileSet before using it in generation" }
    }

    protected var map = mutableMapOf<Coords<D>, LongArray>()

    protected var lastCoordsWherePicked: Coords<D>? = null
    protected var lastTileIdPicked: Int? = null

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
            val tileId = tileSet.pickTile(tilesAtCoordsArray, random)
            lastTileIdPicked = tileId
            collapse(coords, tileId)
            try {
                propagateFrom(coords, bounds)
                // collapse and propagation successful
                onCollapse?.invoke(coords, tileSet.fromId(tileId))
            } catch (e: PropagationException) {
                rollback()
                // remove that bad choice
                if (tilesAtCoordsArray == null) map[coords] = maxEntropyArray.copyOf().also { it.set0BitAt(tileId) }
                else tilesAtCoordsArray.set0BitAt(tileId)
            }
        }
    }

    fun collapse(coords: Coords<D>, chosenTile: Int = tileSet.pickTile(map[coords], random)) {
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
        val needPropagationCoords: MutableList<PropagationTask<D>> = mutableListOf()
        addPropagationTasksInBoundsToList(latestCollapse, needPropagationCoords, bounds)

        while (needPropagationCoords.isNotEmpty()) {
            val needPropagationCoordsCopy = needPropagationCoords.toMutableList()
            needPropagationCoords.clear()

            for ((center, direction) in needPropagationCoordsCopy) {
                // TODO: fork (multithreading) (is it good to do it here ?)

                val taskPointsTo = center.move(direction)
                val tilesAtNewCoordsArray = map[taskPointsTo] ?: maxEntropyArray.copyOf()
                val currentLocationMaskForDirection = LongArray(arraySize)
                for (tileAtCenter in tileSet.getTileList(map[center] ?: maxEntropyArray)) {
                    tileAtCenter.addAllowedNeighborsToArray(currentLocationMaskForDirection, direction)
                }

                if (tilesAtNewCoordsArray bitAndEquals currentLocationMaskForDirection) { // removing impossible neighbors
                    // tilesAtCoordsArray has changed
                    if (tilesAtNewCoordsArray.hasOnly0Bits()) throw PropagationException(taskPointsTo)
                    map[taskPointsTo] = tilesAtNewCoordsArray

                    addPropagationTasksInBoundsToList(taskPointsTo, needPropagationCoords, bounds)
                }
            }
        }
    }

    protected data class PropagationTask<D : Dimension<D>>(
        val center: Coords<D>,
        val direction: Direction<D>
    ) {
        fun pointsTo() = center.move(direction)
    }

    private fun addPropagationTasksInBoundsToList(
        coords: Coords<D>,
        list: MutableList<PropagationTask<D>>,
        bounds: Bounds<D>
    ) {
        for (direction in coords.dimension.directionsAt(coords)) {
            val newCoords: Coords<D> = coords.move(direction)
            if ((newCoords in bounds) && (map[newCoords]?.hasSingle1Bit() != true))
                list += PropagationTask(coords, direction)
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

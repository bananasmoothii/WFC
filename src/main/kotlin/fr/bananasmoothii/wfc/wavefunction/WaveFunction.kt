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

class WaveFunction<C : Rotatable<D>, D : Dimension<D>> private constructor(
    val tileSet: TileSet<C, D>,
    protected var map: MutableMap<Coords<D>, LongArray>,
    private val random: Random = Random.Default,
) : Map<Coords<D>, Tile<C, D>>, Versionable {

    init {
        require(!tileSet.canCreateNewPieces) { "You have to finish creating the TileSet before using it in generation" }
    }

    private var lastCoordsWherePicked: Coords<D>? = null
    private var lastTileIdPicked: Int? = null

    private val allTiles = tileSet.tiles
    private val maxId = tileSet.maxId
    private val arraySize = tileSet.arraySize

    //private val dispatcher = newSingleThreadContext("Propagation thread")

    constructor(tileSet: TileSet<C, D>) : this(
        tileSet,
        mutableMapOf<Coords<D>, LongArray>(),
    )

    fun collapse(bounds: Bounds<D>) {
        for (coords in bounds) {
            if (map[coords])
            commit()
            val chosenPieceId = collapse(coords)
        }
    }

    fun collapse(coords: Coords<D>): Int {
        val tilesAtCoordsArray = map[coords]
        if (tilesAtCoordsArray != null)
            tilesAtCoordsArray.setAll0AndSet1BitAt(tileSet.pickTile(tilesAtCoordsArray, random))
        else
            map[coords] = LongArray(arraySize).also { it.set1BitAt(random.nextInt(maxId + 1)) }
    }

    /**
     * @throws PropagationException if an empty state is found
     */
    fun propagateFrom(latestCollapse: Coords<D>, latestCollapseTile: Tile<C, D>, bounds: Bounds<D>) {
        val needPropagationCoords: MutableList<PropagationTask<C, D>> = mutableListOf()
        latestCollapse.addPropagationTasksInBoundsToList(needPropagationCoords, bounds)

        while (needPropagationCoords.isNotEmpty()) {
            val needPropagationCoordsCopy = needPropagationCoords.toMutableList()
            needPropagationCoords.clear()

            for (task in needPropagationCoordsCopy) {
                // TODO: fork (multithreading) (is it good to do it here ?)

                val taskPointsTo = task.pointsTo()
                val tilesAtCoordsArray = map[taskPointsTo] ?: continue
                val currentLocationMaskForDirection = LongArray(arraySize)
                for (currentLocationTile in tileSet.getTileList(map[task.center]!!)) {
                    currentLocationMaskForDirection bitOrEquals currentLocationTile.getNeighborMask(task.direction)
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
                dimension.directions.forEach { direction ->
                    if (this.move(direction) in bounds)
                        list += PropagationTask(this, direction)
                }
            }
        }
    }

    class PropagationException(message: String) : Exception(message) {
        constructor(coords: Coords<*>) : this("Found an empty node at $coords")
    }



    private inner class State(
        val map: MutableMap<Coords<D>, LongArray>,
        val lastCoordsWherePicked: Coords<D>?,
        val lastTileIdPicked: Int?
    )

    private val states = mutableListOf<State>()

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

}

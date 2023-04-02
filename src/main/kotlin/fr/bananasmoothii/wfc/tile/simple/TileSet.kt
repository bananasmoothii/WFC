package fr.bananasmoothii.wfc.tile.simple

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Dimensioned
import fr.bananasmoothii.wfc.space.Direction
import fr.bananasmoothii.wfc.tile.*
import fr.bananasmoothii.wfc.util.getBitAt
import fr.bananasmoothii.wfc.util.set1BitAt

sealed class TileSet<C : Rotatable<D>, D : Dimension<D>, T : Tile<C, D>>(override val dimension: D) :
    AbstractTileSet<C, D>(), Dimensioned<D> {

    protected val _tiles: MutableList<T> = mutableListOf()

    override val tiles: List<T>
        get() {
            return _tiles.toList()
        }

    override fun fromId(id: Int): T = _tiles[id]

    override fun generateRotations() {
        if (!canCreateNewPieces) throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
        for (tile in _tiles.toList()) {
            for (rotation in tile.content.generateRotations()) {
                createOrGetTile(rotation as C)
            }
        }
    }

    override fun getTileList(longArray: LongArray): List<Tile<C, D>> {
        val list = ArrayList<Tile<C, D>>(longArray.size * 16) // this is a very rough estimation
        for (i in 0 .. maxId) {
            if (longArray.getBitAt(i)) list.add(tiles[i])
        }
        return list
    }
}

class NeighborBasedTileSet<C : Rotatable<D>, D : Dimension<D>>(dimension: D) :
    TileSet<C, D, NeighborBasedTileSet<C, D>.Tile>(dimension) {
    override fun createOrGetTile(content: C): Tile {
        if (!canCreateNewPieces) throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
        return _tiles.find { it.content == content } ?: Tile(_tiles.size, content).also { _tiles.add(it) }
    }

    inner class Tile internal constructor(override val id: Int, override val content: C) :
        NeighborBasedTile<C, D>, Dimensioned<D> by this@NeighborBasedTileSet {
        init {
            if (id > maxId) maxId = id
        }

        private val acceptedNeighbors = mutableMapOf<Direction<D>, LongArray>()

        override val belongingSet: AbstractTileSet<C, D>
            get() = this@NeighborBasedTileSet

        override fun addAllowedNeighborsToArray(bitMask: LongArray, direction: Direction<D>) {
            val array = acceptedNeighbors.computeIfAbsent(direction) { LongArray(arraySize) }
            for (i in bitMask.indices) {
                bitMask[i] = bitMask[i] or array[i]
            }
        }

        override fun accepts(direction: Direction<D>, neighborId: Int): Boolean {
            if (this@NeighborBasedTileSet.canCreateNewPieces)
                throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
            return acceptedNeighbors[direction]?.getBitAt(neighborId) ?: false
        }

        override fun accept(direction: Direction<D>, neighborId: Int) {
            if (this@NeighborBasedTileSet.canCreateNewPieces) {
                this@NeighborBasedTileSet.addActionAfterFinishCreation { accept(direction, neighborId) }
                return
            }
            acceptedNeighbors.computeIfAbsent(direction) { LongArray(arraySize) }.set1BitAt(neighborId)
        }
    }

    companion object {
        /**
         * Creates a new [NeighborBasedTileSet] with the [dimension] of [D].
         *
         * **Warning**: [D] must be a singleton, otherwise an [IllegalArgumentException] will be thrown.
         */
        inline operator fun <C : Rotatable<D>, reified D : Dimension<D>> invoke() =
            NeighborBasedTileSet<C, D>(
                dimension = D::class.objectInstance
                    ?: throw IllegalArgumentException("Dimension must be a singleton to use this constructor")
            )
    }
}

class ConnectionBasedTileSet<C : Rotatable<D>, D : Dimension<D>>(dimension: D) :
    TileSet<C, D, ConnectionBasedTileSet<C, D>.Tile>(dimension) {
    override fun createOrGetTile(content: C): Tile {
        if (!canCreateNewPieces) throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
        return _tiles.find { it.content == content } ?: Tile(_tiles.size, content).also { _tiles.add(it) }
    }

    inner class Tile internal constructor(override val id: Int, override val content: C) :
        ConnectionBasedTile<C, D>, Dimensioned<D> by this@ConnectionBasedTileSet {
        init {
            if (id > maxId) maxId = id
        }

        override val belongingSet: AbstractTileSet<C, D>
            get() = this@ConnectionBasedTileSet

        // TODO: optimize, MutableList is probably slow, but maybe it's fine
        private val connections = mutableMapOf<Direction<D>, MutableList<Int>>()

        override fun addConnection(connection: Int, direction: Direction<D>) {
            connections.computeIfAbsent(direction) { mutableListOf() }.add(connection)
        }

        override fun addAllowedNeighborsToArray(bitMask: LongArray, direction: Direction<D>) {
            for (connection in connections[direction] ?: return) {
                for (possibleNeighbor in _tiles) {
                    if (possibleNeighbor.connections[direction.opposite]?.contains(connection) == true) {
                        bitMask.set1BitAt(possibleNeighbor.id)
                    }
                }
            }
        }

        override fun accepts(direction: Direction<D>, neighborId: Int): Boolean {
            val neighbor: Tile = _tiles[neighborId]
            for (connection in connections[direction] ?: return false) {
                if (neighbor.connections[direction.opposite]?.contains(connection) == true) return true
            }
            return false
        }

    }

    companion object {
        /**
         * Creates a new [ConnectionBasedTileSet] with the [dimension] of [D].
         *
         * **Warning**: [D] must be a singleton, otherwise an [IllegalArgumentException] will be thrown.
         */
        inline operator fun <C : Rotatable<D>, reified D : Dimension<D>> invoke() =
            ConnectionBasedTileSet<C, D>(
                dimension = D::class.objectInstance
                    ?: throw IllegalArgumentException("Dimension must be a singleton to use this constructor")
            )
    }
}

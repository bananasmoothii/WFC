package fr.bananasmoothii.wfc.tile.simple

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Dimensioned
import fr.bananasmoothii.wfc.space.Direction
import fr.bananasmoothii.wfc.tile.AbstractTileSet
import fr.bananasmoothii.wfc.tile.Rotatable
import fr.bananasmoothii.wfc.util.getBitAt
import fr.bananasmoothii.wfc.util.set1BitAt

class TileSet<C : Rotatable<D>, D : Dimension<D>>(override val dimension: D) : AbstractTileSet<C, D>(), Dimensioned<D> {

    private val _tiles: MutableList<Tile> = mutableListOf()

    override val tiles: List<Tile>
        get() = _tiles.toList()

    override fun fromId(id: Int): Tile = _tiles[id]

    override fun createOrGetTile(content: C): Tile {
        if (!canCreateNewPieces) throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
        val tileWithSameContent = _tiles.find { it.content == content }
        return tileWithSameContent ?: Tile(maxId++, content).also { _tiles.add(it) }
    }

    override fun generateRotations() {
        if (!canCreateNewPieces) throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
        for (tile in _tiles.toList()) {
            for (rotation in tile.content.generateRotations()) {
                val tileWithSameContent = _tiles.find { it.content == rotation }
                if (tileWithSameContent == null) {
                    _tiles += Tile(maxId++, rotation as C)
                }
            }
        }
    }

    inner class Tile internal constructor(override val id: Int, override val content: C) :
        fr.bananasmoothii.wfc.tile.Tile<C, D>, Dimensioned<D> by this@TileSet {
        init {
            if (id > maxId) maxId = id
        }

        private val acceptedNeighbors = mutableMapOf<Direction<D>, LongArray>()

        override val belongingSet: AbstractTileSet<C, D>
            get() = this@TileSet

        override fun addAllowedNeighborsToArray(bitMask: LongArray, direction: Direction<D>) {
            val array = acceptedNeighbors.computeIfAbsent(direction) { LongArray(arraySize) }
            for (i in bitMask.indices) {
                bitMask[i] = bitMask[i] or array[i]
            }
        }

        override fun accepts(direction: Direction<D>, neighborId: Int): Boolean {
            if (this@TileSet.canCreateNewPieces)
                throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
            return acceptedNeighbors[direction]?.getBitAt(neighborId) ?: false
        }

        override fun accept(direction: Direction<D>, neighborId: Int) {
            if (this@TileSet.canCreateNewPieces) {
                this@TileSet.addActionAfterFinishCreation { accept(direction, neighborId) }
                return
            }
            acceptedNeighbors.computeIfAbsent(direction) { LongArray(arraySize) }.set1BitAt(neighborId)
        }
    }

    companion object {
        inline operator fun <C : Rotatable<D>, reified D : Dimension<D>> invoke() =
            TileSet<C, D>(dimension = D::class.objectInstance ?: throw IllegalArgumentException("Dimension must be a singleton"))
    }
}
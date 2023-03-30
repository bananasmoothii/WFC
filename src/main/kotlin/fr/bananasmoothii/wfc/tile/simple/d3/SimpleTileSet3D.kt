package fr.bananasmoothii.wfc.tile.simple.d3

import fr.bananasmoothii.wfc.space.d3.D3
import fr.bananasmoothii.wfc.space.d3.Dimension3D
import fr.bananasmoothii.wfc.tile.Tile
import fr.bananasmoothii.wfc.tile.d3.Rotatable3D
import fr.bananasmoothii.wfc.tile.simple.SimpleTileSet

class SimpleTileSet3D<C : Rotatable3D> : SimpleTileSet<C, Dimension3D>(), D3 {

    private val _tiles: MutableList<SimpleTile3D<C>> = mutableListOf()

    override val tiles: List<SimpleTile3D<C>>
        get() = _tiles

    override fun fromId(id: Int): Tile<C, Dimension3D> = _tiles[id]

    override fun createOrGetTile(content: C): SimpleTile3D<C> {
        if (!canCreateNewPieces) throw IllegalStateException("Can't create new pieces after finishPieceCreation() has been called")
        val tileWithSameContent = _tiles.find { it.content == content }
        return tileWithSameContent ?: SimpleTile3D(++maxId, content, this).also { _tiles.add(it) }
    }
}
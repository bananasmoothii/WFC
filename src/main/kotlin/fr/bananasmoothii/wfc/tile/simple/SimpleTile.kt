package fr.bananasmoothii.wfc.tile.simple

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.tile.Rotatable
import fr.bananasmoothii.wfc.tile.Tile

/**
 * A piece with no weights.
 * Instantiate with [SimpleTileSet.createOrGetTile].
 * @param C the type of the content of the piece
 */
abstract class SimpleTile<C : Rotatable<D>, D: Dimension<D>>(
    override val id: Int,
    override val content: C,
    belongingSet: SimpleTileSet<C, D>? = null
) : Tile<C, D> {
    override var belongingSet: SimpleTileSet<C, D>? = belongingSet
        internal set
}

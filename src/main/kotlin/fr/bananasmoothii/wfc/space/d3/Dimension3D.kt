package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Dimension

object Dimension3D: Dimension<Dimension3D> {
    override val dimension: Int = 3

    override val directions: Array<Direction3D> = Direction3D.values
}
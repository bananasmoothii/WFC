package fr.bananasmoothii.wfc.space.d2

import fr.bananasmoothii.wfc.space.Coords
import fr.bananasmoothii.wfc.space.Dimension

object Dimension2D: Dimension<Dimension2D> {
    override fun directionsAt(coords: Coords<Dimension2D>) = Direction2D.values
}

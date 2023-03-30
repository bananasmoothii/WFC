package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Coords
import fr.bananasmoothii.wfc.space.Dimension

object Dimension3D: Dimension<Dimension3D> {
    override fun directionsAt(coords: Coords<Dimension3D>) = Direction3D.values
}

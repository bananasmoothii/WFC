package fr.bananasmoothii.wfc.space

import fr.bananasmoothii.wfc.space.d3.Dimension3D

/**
 * Typically 3D: [Dimension3D], or 2D, but can also be anything you want, even in non-euclidean spaces.
 * @param D the type of the implementing class
 */
interface Dimension<D: Dimension<D>> {
    fun directionsAt(coords: Coords<D>): Array<out Direction<D>>
}
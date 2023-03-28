package fr.bananasmoothii.wfc.space

/**
 * Typically 3D: [Dimension3D], or 2D.
 * @param D the type of the implementing class
 */
interface Dimension<D: Dimension<D>> {
    val dimension: Int

    val directions: Array<out Direction<D>>
}
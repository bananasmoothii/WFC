package fr.bananasmoothii.wfc.space

/**
 * Indicates that the implementing class has a certain dimension.
 */
interface Dimensioned<D: Dimension<D>> {
    val dimension: D
}
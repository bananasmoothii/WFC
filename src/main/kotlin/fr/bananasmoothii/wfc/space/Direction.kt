package fr.bananasmoothii.wfc.space

/**
 * A direction in a [Dimension].
 * All [Direction]s should have an unique [hashCode] for a given [Dimension].
 */
interface Direction<D: Dimension<D>>: Dimensioned<D> {
    val opposite: Direction<D>
}
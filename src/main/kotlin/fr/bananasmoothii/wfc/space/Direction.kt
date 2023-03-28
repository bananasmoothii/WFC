package fr.bananasmoothii.wfc.space

interface Direction<D: Dimension<D>>: Dimensioned<D> {
    val opposite: Direction<D>
}
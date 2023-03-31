package fr.bananasmoothii.wfc.space

interface Bounds<D: Dimension<D>>: Dimensioned<D>, Iterable<Coords<D>> {
    val min: Coords<D>
    val max: Coords<D>
}
package fr.bananasmoothii.wfc.space

interface Bounds<D: Dimension<D>>: Dimensioned<D>, Iterable<Coords<D>> {
    val start: Coords<D>
    val end: Coords<D>
}
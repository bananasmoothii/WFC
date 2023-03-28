package fr.bananasmoothii.wfc.space

interface Bounds<D: Dimension>: Dimensioned<D>, Iterable<Coords<D>> {
    val start: Coords<D>
    val end: Coords<D>
}
package fr.bananasmoothii.wfc.tile

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Dimensioned

/**
 * A dimensioned object that can be rotated.
 * Also, please implement [equals] and [hashCode] properly to avoid duplicates.
 */
interface Rotatable<D: Dimension<D>>: Dimensioned<D> {
    fun generateRotations(): Iterable<Rotatable<D>>
}
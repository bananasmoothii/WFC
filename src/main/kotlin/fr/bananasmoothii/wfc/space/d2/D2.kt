package fr.bananasmoothii.wfc.space.d2

import fr.bananasmoothii.wfc.space.Dimensioned

/**
 * Indicates that the implementing class has a dimension of 2.
 */
interface D2: Dimensioned<Dimension2D> {
    /**
     * Do not override.
     */
    override val dimension: Dimension2D
        get() = Dimension2D
}
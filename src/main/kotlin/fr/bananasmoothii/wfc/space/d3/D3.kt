package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Dimensioned

/**
 * Indicates that the implementing class has a dimension of 3.
 */
interface D3: Dimensioned<Dimension3D> {
    /**
     * Do not override.
     */
    override val dimension: Dimension3D
        get() = Dimension3D
}
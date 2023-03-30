package fr.bananasmoothii.wfc.space

import fr.bananasmoothii.wfc.wavefunction.WaveFunction

/**
 * N-dimensional coordinated. When implementing, please pay attention to how you implement [equals] and [hashCode] as
 * this class is the key for the [Map] stored in [WaveFunction]
 */
interface Coords<D : Dimension<D>> : Dimensioned<D> {
    fun move(direction: Direction<D>): Coords<D>

    /**
     * @return the coordinates of the neighbours (adjacent tiles) of this coordinate
     */
    fun getNeighbours(): List<Coords<D>> = dimension.directionsAt(this).map { move(it) }
}
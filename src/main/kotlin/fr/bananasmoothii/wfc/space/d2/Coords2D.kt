package fr.bananasmoothii.wfc.space.d2

import fr.bananasmoothii.wfc.space.Coords
import fr.bananasmoothii.wfc.space.Direction
import fr.bananasmoothii.wfc.space.d2.Direction2D.*

data class Coords2D(val x: Int, val y: Int) : Coords<Dimension2D>, D2 {

    /**
     * Moves to a new position one unit in the given direction.
     * **Warning**: [UP] means towards negative y
     */
    override fun move(direction: Direction<Dimension2D>): Coords2D {
        return when (direction) {
            UP -> copy(y = y - 1)
            DOWN -> copy(y = y + 1)
            LEFT -> copy(x = x - 1)
            RIGHT -> copy(x = x + 1)
            else -> throw IllegalArgumentException("Direction $direction is not a Direction2D")
        }
    }

    override fun toString(): String = "($x, $y)"
}
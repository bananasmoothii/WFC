package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Direction

@Suppress("EqualsOrHashCode")
sealed class Direction3D: Direction<Dimension3D>, D3 {
    object NORTH: Direction3D() {
        override val opposite: SOUTH = SOUTH

        override fun hashCode(): Int = 1
    }

    object SOUTH: Direction3D() {
        override val opposite: NORTH = NORTH

        override fun hashCode(): Int = 2
    }

    object EAST: Direction3D() {
        override val opposite: WEST = WEST

        override fun hashCode(): Int = 3
    }

    object WEST: Direction3D() {
        override val opposite: EAST = EAST

        override fun hashCode(): Int = 4
    }

    object UP: Direction3D() {
        override val opposite: DOWN = DOWN

        override fun hashCode(): Int = 5
    }

    object DOWN: Direction3D() {
        override val opposite: UP = UP

        override fun hashCode(): Int = 6
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return hashCode() == other.hashCode()
    }

    companion object {
        val values: Array<Direction3D> by lazy { arrayOf(NORTH, SOUTH, EAST, WEST, UP, DOWN) }
    }
}
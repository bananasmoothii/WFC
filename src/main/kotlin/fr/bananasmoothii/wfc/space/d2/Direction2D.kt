package fr.bananasmoothii.wfc.space.d2

import fr.bananasmoothii.wfc.space.Direction

@Suppress("EqualsOrHashCode")
sealed class Direction2D: Direction<Dimension2D>, D2 {

    object UP: Direction2D() {
        override val opposite: DOWN = DOWN

        override fun hashCode(): Int = 1
    }

    object DOWN: Direction2D() {
        override val opposite: UP = UP

        override fun hashCode(): Int = 2
    }

    object LEFT: Direction2D() {
        override val opposite: RIGHT = RIGHT

        override fun hashCode(): Int = 3
    }

    object RIGHT: Direction2D() {
        override val opposite: LEFT = LEFT

        override fun hashCode(): Int = 4
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        return hashCode() == other.hashCode()
    }

    companion object {
        val values: Array<Direction2D> by lazy { arrayOf(UP, DOWN, LEFT, RIGHT) }
    }
}
package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Direction

sealed class Direction3D: Direction<Dimension3D>, D3 {
    object NORTH: Direction3D() {
        override val opposite: SOUTH = SOUTH
    }

    object SOUTH: Direction3D() {
        override val opposite: NORTH = NORTH
    }

    object EAST: Direction3D() {
        override val opposite: WEST = WEST
    }

    object WEST: Direction3D() {
        override val opposite: EAST = EAST
    }

    object UP: Direction3D() {
        override val opposite: DOWN = DOWN
    }

    object DOWN: Direction3D() {
        override val opposite: UP = UP
    }

    companion object {
        val values: Array<Direction3D> = arrayOf(NORTH, SOUTH, EAST, WEST, UP, DOWN)
    }
}
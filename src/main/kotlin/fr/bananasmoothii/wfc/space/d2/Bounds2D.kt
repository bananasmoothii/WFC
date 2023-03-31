package fr.bananasmoothii.wfc.space.d2

import fr.bananasmoothii.wfc.space.Bounds
import fr.bananasmoothii.wfc.space.d2.Bounds2D.Companion.rangeTo

/**
 * A 3D volume between two inclusive coordinates.
 * Instantiate with [Coords2D.rangeTo]: `Coords(x1, y1, z1) .. Coords(x2, y2, z2)`
 */
class Bounds2D private constructor(override val min: Coords2D, override val max: Coords2D) : Bounds<Dimension2D>, D2 {

    val width = max.x - min.x + 1
    val height = max.y - min.y + 1

    operator fun contains(coords: Coords2D): Boolean =
        coords.x in min.x..max.x && coords.y in min.y..max.y


    override operator fun iterator(): Iterator<Coords2D> = object : Iterator<Coords2D> {
        var currentX = min.x
        var currentY = min.y


        override fun hasNext(): Boolean = currentX <= max.x && currentY <= max.y

        override fun next(): Coords2D {
            if (currentY > max.y) throw NoSuchElementException()
            val result = Coords2D(currentX, currentY)
            currentX++
            if (currentX > max.x) {
                currentX = min.x
                currentY++
            }
            return result
        }
    }

    companion object {
        operator fun Coords2D.rangeTo(other: Coords2D) = Bounds2D(
            Coords2D(
                minOf(this.x, other.x),
                minOf(this.y, other.y),
            ),
            Coords2D(
                maxOf(this.x, other.x),
                maxOf(this.y, other.y),
            )
        )
    }
}
package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Bounds
import fr.bananasmoothii.wfc.space.d3.Bounds3D.Companion.rangeTo

/**
 * A 3D volume between two inclusive coordinates.
 * Instantiate with [Coords3D.rangeTo]: `Coords(x1, y1, z1) .. Coords(x2, y2, z2)`
 */
class Bounds3D private constructor(override val start: Coords3D, override val end: Coords3D) : Bounds<Dimension3D>, D3 {

    operator fun contains(coords: Coords3D): Boolean =
        coords.x in start.x..end.x && coords.y in start.y..end.y && coords.z in start.z..end.z


    override operator fun iterator(): Iterator<Coords3D> = object : Iterator<Coords3D> {
        var currentX = start.x
        var currentY = start.y
        var currentZ = start.z

        override fun hasNext(): Boolean = currentX <= end.x && currentY <= end.y && currentZ <= end.z

        override fun next(): Coords3D {
            val result = Coords3D(currentX, currentY, currentZ)
            currentX++
            if (currentX > end.x) {
                currentX = start.x
                currentY++
                if (currentY > end.y) {
                    currentY = start.y
                    currentZ++
                    if (currentZ > end.z) throw NoSuchElementException()
                }
            }
            return result
        }
    }

    companion object {
        operator fun Coords3D.rangeTo(other: Coords3D) = Bounds3D(
            Coords3D(
                minOf(this.x, other.x),
                minOf(this.y, other.y),
                minOf(this.z, other.z)
            ),
            Coords3D(
                maxOf(this.x, other.x),
                maxOf(this.y, other.y),
                maxOf(this.z, other.z)
            )
        )
    }
}
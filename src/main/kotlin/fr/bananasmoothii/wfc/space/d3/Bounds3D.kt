package fr.bananasmoothii.wfc.space.d3

import fr.bananasmoothii.wfc.space.Bounds
import fr.bananasmoothii.wfc.space.Coords
import fr.bananasmoothii.wfc.space.d3.Bounds3D.Companion.rangeTo

/**
 * A 3D volume between two inclusive coordinates.
 * Instantiate with [Coords3D.rangeTo]: `Coords(x1, y1, z1) .. Coords(x2, y2, z2)`
 */
class Bounds3D private constructor(override val min: Coords3D, override val max: Coords3D) : Bounds<Dimension3D>, D3 {

    override operator fun contains(coords: Coords<Dimension3D>): Boolean =
        (coords as Coords3D).x in min.x..max.x && coords.y in min.y..max.y && coords.z in min.z..max.z


    override operator fun iterator(): Iterator<Coords3D> = object : Iterator<Coords3D> {
        var currentX = min.x
        var currentY = min.y
        var currentZ = min.z

        override fun hasNext(): Boolean = currentX <= max.x && currentY <= max.y && currentZ <= max.z

        override fun next(): Coords3D {
            if (currentZ > max.z) throw NoSuchElementException()
            val result = Coords3D(currentX, currentY, currentZ)
            currentX++
            if (currentX > max.x) {
                currentX = min.x
                currentY++
                if (currentY > max.y) {
                    currentY = min.y
                    currentZ++
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
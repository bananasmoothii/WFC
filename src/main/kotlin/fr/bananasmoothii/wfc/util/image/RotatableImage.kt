package fr.bananasmoothii.wfc.util.image

import fr.bananasmoothii.wfc.space.d2.D2
import fr.bananasmoothii.wfc.space.d2.Dimension2D
import fr.bananasmoothii.wfc.tile.Rotatable
import java.awt.image.BufferedImage
import java.util.*

class RotatableImage(val image: BufferedImage): Rotatable<Dimension2D>, D2 {

    fun rotateClockwise(): RotatableImage {
        val newImage = BufferedImage(image.height, image.width, image.type)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                newImage.setRGB(y, image.width - x - 1, image.getRGB(x, y))
            }
        }
        return RotatableImage(newImage)
    }

    fun rotateCounterClockwise(): RotatableImage {
        val newImage = BufferedImage(image.height, image.width, image.type)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                newImage.setRGB(image.height - y - 1, x, image.getRGB(x, y))
            }
        }
        return RotatableImage(newImage)
    }

    override fun generateRotations(): Iterable<Rotatable<Dimension2D>> {
        val rotations = mutableListOf<RotatableImage>()
        var current = this
        for (i in 0 until 4) {
            rotations.add(current)
            current = current.rotateClockwise()
        }
        return rotations
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RotatableImage) return false
        if (image.width != other.image.width || image.height != other.image.height) return false

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                if (image.getRGB(x, y) != other.image.getRGB(x, y)) return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(image.getRGB(0, 0, image.width, image.height, null, 0, image.width))
    }
}
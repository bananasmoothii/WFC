package fr.bananasmoothii.wfc.util.image

import fr.bananasmoothii.wfc.space.d2.Bounds2D
import fr.bananasmoothii.wfc.space.d2.Dimension2D
import fr.bananasmoothii.wfc.wavefunction.WaveFunction
import java.awt.image.BufferedImage

fun WaveFunction<RotatableImage, Dimension2D>.toImage(
    bounds: Bounds2D,
    tileImageWidth: Int,
    tileImageHeight: Int
): BufferedImage {
    val image =
        BufferedImage(bounds.width * tileImageWidth, bounds.height * tileImageHeight, BufferedImage.TYPE_INT_ARGB)
    for (coords in bounds) {
        val tiles = (getTileList(coords) ?: tileSet.tiles)
        val tilesSize = tiles.size
        for (tileX in 0 until tileImageWidth) {
            for (tileY in 0 until tileImageHeight) {
                val x = (coords.x - bounds.min.x) * tileImageWidth + tileX
                val y = (coords.y - bounds.min.y) * tileImageHeight + tileY
                var colorAlpha = 0
                var colorRed = 0
                var colorGreen = 0
                var colorBlue = 0
                for (tile in tiles) {
                    val tileImage = tile.content.image
                    val tileColor = tileImage.getRGB(tileX, tileY)
                    colorAlpha += (tileColor shr 24) and 0xFF
                    colorRed += (tileColor shr 16) and 0xFF
                    colorGreen += (tileColor shr 8) and 0xFF
                    colorBlue += tileColor and 0xFF
                }
                colorRed /= tilesSize
                colorGreen /= tilesSize
                colorBlue /= tilesSize
                image.setRGB(x, y, (colorAlpha shl 24) or (colorRed shl 16) or (colorGreen shl 8) or colorBlue)
            }
            /*
            val x = (coords.x - bounds.min.x) * tileImageWidth
            val y = (coords.y - bounds.min.y) * tileImageHeight
            image.graphics.drawImage(tileImage, x, y, tileImageWidth, tileImageHeight, null)

             */
        }
    }
    return image
}
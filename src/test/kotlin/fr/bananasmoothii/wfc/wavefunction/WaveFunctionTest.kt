package fr.bananasmoothii.wfc.wavefunction

import fr.bananasmoothii.wfc.space.d2.Bounds2D
import fr.bananasmoothii.wfc.space.d2.Bounds2D.Companion.rangeTo
import fr.bananasmoothii.wfc.space.d2.Coords2D
import fr.bananasmoothii.wfc.space.d2.Dimension2D
import fr.bananasmoothii.wfc.space.d2.Direction2D
import fr.bananasmoothii.wfc.tile.Tile
import fr.bananasmoothii.wfc.tile.simple.NeighborBasedTileSet
import fr.bananasmoothii.wfc.tile.simple.TileSet
import fr.bananasmoothii.wfc.util.image.RotatableImage
import fr.bananasmoothii.wfc.util.image.toImage
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Suppress("ClassName")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class WaveFunctionTest {
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation::class)
    inner class `Real case Knots neighbor tileset` {
        private var tileSet: TileSet<RotatableImage, Dimension2D, out Tile<RotatableImage, Dimension2D>> =
            NeighborBasedTileSet()

        private val imageNames = listOf("corner", "cross", "empty", "line", "t")
        private val images: List<RotatableImage> = imageNames.map {
            RotatableImage(ImageIO.read(this::class.java.getResourceAsStream("/tilesets/Knots/$it.png")))
        }

        @Test
        @Order(1)
        fun `generating tiles neighbor based tileset`() {
            images.forEach { tileSet.createOrGetTile(it) }
            tileSet.generateRotations()
            for (tile in tileSet.tiles) {
                if (tile !is NeighborBasedTileSet.Tile) throw IllegalArgumentException()
                for (potentialNeighbor in tileSet.tiles) {
                    // checking to add it at the top
                    if (tile.content.image.getRGB(5, 0) == potentialNeighbor.content.image.getRGB(5, 9)) {
                        tile.accept(Direction2D.UP, potentialNeighbor)
                    }
                    // checking to add it at the bottom
                    if (tile.content.image.getRGB(5, 9) == potentialNeighbor.content.image.getRGB(5, 0)) {
                        tile.accept(Direction2D.DOWN, potentialNeighbor)
                    }
                    // checking to add it at the left
                    if (tile.content.image.getRGB(0, 5) == potentialNeighbor.content.image.getRGB(9, 5)) {
                        tile.accept(Direction2D.LEFT, potentialNeighbor)
                    }
                    // checking to add it at the right
                    if (tile.content.image.getRGB(9, 5) == potentialNeighbor.content.image.getRGB(0, 5)) {
                        tile.accept(Direction2D.RIGHT, potentialNeighbor)
                    }
                }
            }
            tileSet.finishTileCreation()
        }

        private fun BufferedImage.save(name: String) {
            ImageIO.write(this, "png", File("./images/$name.png").also { it.createNewFile() })
        }

        private fun wfc(dir: String = "images") {
            val fileDir = File("./$dir")
            fileDir.delete()
            fileDir.mkdirs()

            val waveFunction = WaveFunction(tileSet, commitStates = false)
            val bounds: Bounds2D = Coords2D(0, 0)..Coords2D(1280, 720)
            /*
            waveFunction.onCollapse = { _, _ ->
                ImageIO.write(
                    waveFunction.toImage(bounds, 10, 10),
                    "png",
                    File(fileDir, "out-${System.nanoTime()}.png").also { it.createNewFile() }
                )
            }

             */

            waveFunction.collapse(bounds)
            val image = waveFunction.toImage(bounds, 10, 10)
            ImageIO.write(image, "png", File(fileDir, "out-final.png").also { it.createNewFile() })
        }

        @Test
        @Order(2)
        fun `WFC with neighbor based tileset`() {
            wfc("images/neighbor")
        }

        @Test
        @Order(3)
        fun `generating tiles with connection based tileset`() {
            // TODO
        }

        @Test
        @Order(4)
        fun `WFC with connection based tileset`() {
            // TODO
        }
    }
}
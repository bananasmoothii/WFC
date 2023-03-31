package fr.bananasmoothii.wfc.tile

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.space.Dimensioned
import fr.bananasmoothii.wfc.tile.simple.UnfinishedTileSetException
import fr.bananasmoothii.wfc.util.arraySizeForMaxIndex
import fr.bananasmoothii.wfc.util.getBitAt

/**
 * A set of pieces, with a unique id for each piece.
 * @param C the type of the content of the pieces
 */
abstract class AbstractTileSet<C : Rotatable<D>, D : Dimension<D>> : Dimensioned<D> {

    var canCreateNewPieces = true
        private set

    /**
     * The greatest tile id in this set
     */
    var maxId = 0
        protected set

    abstract val tiles: List<Tile<C, D>>

    abstract fun fromId(id: Int): Tile<C, D>

    abstract fun createOrGetTile(content: C): Tile<C, D>

    abstract fun generateRotations()

    private var actionsAfterFinishCreation: MutableList<() -> Unit>? = mutableListOf()

    /**
     * Call this when you're done creating pieces. You can still add neighbors to them.
     */
    fun finishTileCreation() {
        if (!canCreateNewPieces) throw IllegalStateException("You can't call finishPieceCreation() twice")
        canCreateNewPieces = false
        actionsAfterFinishCreation!!.forEach { it() }
        actionsAfterFinishCreation = null
        _arraySize = arraySizeForMaxIndex(maxId)
        _maxEntropyArray =
            LongArray(arraySize) { i -> if (i < arraySize - 1) -1L else -1L shl (64 - (maxId % 64)) } // -1L is 0b11111111...
        _minEntropyArray = LongArray(arraySize)
    }

    protected fun addActionAfterFinishCreation(action: () -> Unit) {
        if (canCreateNewPieces) actionsAfterFinishCreation!!.add(action)
        else action()
    }

    fun getTileList(longArray: LongArray): List<Tile<C, D>> {
        val list = ArrayList<Tile<C, D>>(longArray.size * 64)
        for (i in 0 until maxId) {
            if (longArray.getBitAt(i)) list.add(tiles[i])
        }
        return list
    }

    fun pickTile(tilesAtCoordsArray: LongArray, random: kotlin.random.Random): Int {
        return getTileList(tilesAtCoordsArray)[random.nextInt(tilesAtCoordsArray.size)].id
    }

    private lateinit var _maxEntropyArray: LongArray
    private lateinit var _minEntropyArray: LongArray
    private var _arraySize: Int = 0

    val maxEntropyArray: LongArray
        get() {
            if (canCreateNewPieces) throw UnfinishedTileSetException()
            return _maxEntropyArray
        }

    val minEntropyArray: LongArray
        get() {
            if (canCreateNewPieces) throw UnfinishedTileSetException()
            return _minEntropyArray
        }

    val arraySize: Int
        get() {
            if (canCreateNewPieces) throw UnfinishedTileSetException()
            return _arraySize
        }
}


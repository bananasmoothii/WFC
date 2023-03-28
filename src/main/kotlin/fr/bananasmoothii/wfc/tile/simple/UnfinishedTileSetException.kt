package fr.bananasmoothii.wfc.tile.simple

class UnfinishedTileSetException(message: String) : RuntimeException(message) {
    constructor() : this("You can't call this method before the piece set is finished. Remember to call PieceSet.finishPieceCreation()")
}

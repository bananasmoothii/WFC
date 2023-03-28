package fr.bananasmoothii.wfc.tile.simple

import fr.bananasmoothii.wfc.space.Dimension
import fr.bananasmoothii.wfc.tile.Rotatable
import fr.bananasmoothii.wfc.tile.TileSet

abstract class SimpleTileSet<C: Rotatable<D>, D: Dimension<D>>: TileSet<C, D>() {
}
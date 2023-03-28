package fr.bananasmoothii.wfc.tile.d3

import fr.bananasmoothii.wfc.space.d3.Dimension3D
import fr.bananasmoothii.wfc.tile.Rotatable

interface Rotatable3D: Rotatable<Dimension3D> {
    // TODO: maybe add methods like flipNorthSouth, flipEastWest, flipUpDown, rotateClockwise, rotateCounterClockwise, rotate180
}
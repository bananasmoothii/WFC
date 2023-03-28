package fr.bananasmoothii.wfc.wavefunction

/**
 * A versionable object is an object that can be committed or rolled back.
 */
interface Versionable {
    /**
     * Saves the current state of the object, so that it can be rolled back to it later.
     */
    fun commit()

    /**
     * @return true if the object can be rolled back to a previous state
     */
    fun canRollback(): Boolean

    /**
     * Rolls back the object to the last committed state.
     * @throws NoSuchElementException if [canRollback] returns false
     */
    fun rollback()
}
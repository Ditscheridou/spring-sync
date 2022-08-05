package de.jds.shadowstore

/**
 * Strategy interface for maintaining shadow copies across requests.
 *
 * @author Craig Walls
 */
interface ShadowStore {
    /**
     * Stores a shadow copy.
     * @param key the key to store the shadow under
     * @param shadow the shadow copy
     */
    fun putShadow(key: String, shadow: Shadow<*>?)

    /**
     * Retrieves a shadow copy.
     * @param key the key that the shadow is stored under
     * @return the shadow copy
     */
    fun getShadow(key: String): Shadow<*>?
}

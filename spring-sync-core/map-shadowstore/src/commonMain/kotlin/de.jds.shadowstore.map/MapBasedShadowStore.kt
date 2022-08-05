package de.jds.shadowstore.map

import de.jds.shadowstore.AbstractShadowStore
import de.jds.shadowstore.Shadow

/**
 * Implementation of [ShadowStore] that keeps shadows in an in-memory map.
 * Not recommended for production applications, as it isn't scalable in terms of the number of clients.
 * Consider RedisShadowStore instead.
 *
 * @author Craig Walls
 */
class MapBasedShadowStore(remoteNodeId: String) : AbstractShadowStore(remoteNodeId) {
    private val store: MutableMap<String, Shadow<*>?> = HashMap()

    override fun putShadow(key: String, shadow: Shadow<*>?) {
        store[getNodeSpecificKey(key)] = shadow
    }

    override fun getShadow(key: String): Shadow<*>? {
        return store[getNodeSpecificKey(key)]
    }
}
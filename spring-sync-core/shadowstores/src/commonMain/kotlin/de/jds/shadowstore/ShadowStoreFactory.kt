package de.jds.shadowstore

interface ShadowStoreFactory {
    fun getShadowStore(id: String): ShadowStore
}

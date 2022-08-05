package de.sync.core.persistence

import kotlinx.serialization.Serializable

interface IPersistenceCallbackRegistry {
    fun addPersistenceCallback(persistenceCallback: PersistenceCallback<Serializable>)
    fun findPersistenceCallback(key: String): PersistenceCallback<Serializable>
}
/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.sync.core.persistence

import kotlinx.serialization.Serializable

/**
 * Registry for looking up [PersistenceCallback]s to be used in the course of applying a [Patch] via
 * Differential Synchronization.
 *
 * @author Craig Walls
 */
class PersistenceCallbackRegistry : IPersistenceCallbackRegistry {
    private val persistenceCallbacks: MutableMap<String, PersistenceCallback<Serializable>> = mutableMapOf()

    /**
     * Adds a [PersistenceCallback] to the registry with a key that is pluralized by the pluralize() method.
     *
     * @param persistenceCallback the [PersistenceCallback] to add to the registry.
     */
    override fun addPersistenceCallback(persistenceCallback: PersistenceCallback<Serializable>) {
        val entityType = persistenceCallback.entityType
        val key = pluralize(entityType.simpleName)
        persistenceCallbacks[key] = persistenceCallback
    }

    /**
     * Looks up a [PersistenceCallback] from the registry.
     *
     * @param key the key that the [PersistenceCallback] has been registered under.
     * @return the [PersistenceCallback]
     */
    override fun findPersistenceCallback(key: String): PersistenceCallback<Serializable> {
        return persistenceCallbacks[key]!!
    }

    /**
     * Pluralizes an entity's type name. Default implementation is to naively add an 's' to the end of the given String.
     * Override to implement a more elegant pluralization technique.
     *
     * @param entityTypeName the entity type name to be pluralized.
     * @return the pluralized type name.
     */
    protected fun pluralize(entityTypeName: String?): String {
        return (entityTypeName?.lowercase() + "s")
    }
}
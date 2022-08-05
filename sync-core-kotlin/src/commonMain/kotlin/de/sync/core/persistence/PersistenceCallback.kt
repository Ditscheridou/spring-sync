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

import kotlin.reflect.KClass

/**
 * Callback to handle persistence in the course of applying a patch via Differential Synchronization.
 * Enables DiffSyncController to be decoupled from any particular persistence mechanism.
 * @author Craig Walls
 *
 * @param <T> The entity type
</T> */
interface PersistenceCallback<T : Any> {
    /**
     * Find all instances of the entity
     * @return all instance of the entity
     */
    fun findAll(): List<T>?

    /**
     * find a single entity
     *
     * @param id the id of the entity as a String. The implementation may convert it to the actual type.
     * @return the entity
     */
    fun findOne(id: String?): T?

    /**
     * Save a single item.
     * @param itemToSave the item to save.
     */
    fun persistChange(itemToSave: T)

    /**
     * Save changed items and delete removed items.
     * @param itemsToSave a list of items to be saved.
     * @param itemsToDelete a list of items to be deleted.
     */
    fun persistChanges(itemsToSave: List<T>?, itemsToDelete: List<T>?)

    /**
     * @return the type of entity that this callback works with.
     */
    val entityType: KClass<T>
}
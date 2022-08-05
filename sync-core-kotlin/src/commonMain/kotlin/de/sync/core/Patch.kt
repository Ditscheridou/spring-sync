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
package de.sync.core

import de.sync.core.operations.PatchOperation
import de.sync.core.util.DeepCloneUtils
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

/**
 *
 * Represents a Patch.
 *
 *
 *
 * This class (and [PatchOperation] capture the definition of a patch, but are not coupled
 * to any specific patch representation.
 *
 *
 * @author Craig Walls
 */
open class Patch(private val operations: List<PatchOperation>) {

    /**
     * @return the number of operations that make up this patch.
     */
    fun size(): Int {
        return operations.size
    }

    fun getOperations(): List<PatchOperation> {
        return operations
    }

    /**
     * Applies the Patch to a given Object graph. Makes a copy of the given object so that it will remain unchanged after application of the patch
     * and in case any errors occur while performing the patch.
     *
     * @param in   The object graph to apply the patch to.
     * @param type The object type.
     * @param <T>  the object type.
     * @return An object graph modified by the patch.
     * @throws PatchException if there are any errors while applying the patch.
    </T> */
    @Throws(PatchException::class)
    fun <T : Serializable> apply(`in`: T, type: KClass<T>): T {
        // Make defensive copy of in before performing operations so that if any op fails, the original is left untouched
        val work = DeepCloneUtils.deepClone(`in`)
        for (operation in operations) {
            operation.perform(work, type)
        }
        return work
    }

    /**
     * Applies the Patch to a given List of objects. Makes a copy of the given list so that it will remain unchanged after application of the patch
     * and in case any errors occur while performing the patch.
     *
     * @param in   The list to apply the patch to.
     * @param type The list's generic type.
     * @param <T>  the list's generic type.
     * @return An list modified by the patch.
     * @throws PatchException if there are any errors while applying the patch.
    </T> */
    @Throws(PatchException::class)
    fun <T : Serializable> apply(`in`: List<T>, type: KClass<T>): List<T> {
        // Make defensive copy of in before performing operations so that if any op fails, the original is left untouched
        val work: List<T> = DeepCloneUtils.deepClone(`in`)
        for (operation in operations) {
            operation.perform(work, type)
        }
        return work
    }
}
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

import de.sync.core.operations.*
import difflib.Delta
import difflib.Delta.TYPE
import difflib.DiffUtils
import org.springframework.util.ObjectUtils
import kotlin.reflect.KClass

/**
 * Provides support for producing a [Patch] from the comparison of two objects.
 *
 * @author Craig Walls
 */
object Diff {
    /**
     * Performs a difference operation between two objects, resulting in a [Patch] describing the differences.
     *
     * @param original the original, unmodified object.
     * @param modified the modified object.
     * @return a [Patch] describing the differences between the two objects.
     * @throws PatchException if an error occurs while performing the difference.
     */
    @Throws(PatchException::class)
    fun diff(original: Any?, modified: Any?): Patch {
        return try {
            val operations: MutableList<PatchOperation> = mutableListOf()
            if (original is List<*> && modified is List<*>) {
                diffList(operations, "", original, modified)
            } else {
                diffNonList(operations, "", original, modified)
            }
            Patch(operations)
        } catch (e: Exception) {
            throw PatchException("Error performing diff:", e)
        }
    }

    // private helpers
    @Throws(java.io.IOException::class, java.lang.IllegalAccessException::class)
    private fun diffList(operations: MutableList<PatchOperation>, path: String, original: List<*>, modified: List<*>) {
        val diff: difflib.Patch = DiffUtils.diff(original, modified)
        val deltas: List<Delta> = diff.getDeltas()
        for (delta in deltas) {
            val type: TYPE = delta.getType()
            val revisedPosition: Int = delta.getRevised().getPosition()
            if (type === TYPE.CHANGE) {
                val lines: List<*> = delta.getRevised().getLines()
                for (offset in lines.indices) {
                    val originalObject = original[revisedPosition + offset]!!
                    val revisedObject = modified[revisedPosition + offset]!!
                    diffNonList(operations, path + "/" + (revisedPosition + offset), originalObject, revisedObject)
                }
            } else if (type === TYPE.INSERT) {
                val lines: List<*> = delta.getRevised().getLines()
                for (offset in lines.indices) {
                    operations.add(AddOperation(path + "/" + (revisedPosition + offset), lines[offset]))
                }
            } else if (type === TYPE.DELETE) {
                val lines: List<*> = delta.getOriginal().getLines()
                for (offset in lines.indices) {
                    val originalObject = original[revisedPosition + offset]!!
                    operations.add(TestOperation("$path/$revisedPosition", originalObject))
                    operations.add(RemoveOperation("$path/$revisedPosition"))
                }
            }
        }
    }

    @Throws(java.io.IOException::class, java.lang.IllegalAccessException::class)
    private fun diffNonList(operations: MutableList<PatchOperation>, path: String, original: Any?, modified: Any?) {
        if (!ObjectUtils.nullSafeEquals(original, modified)) {
            if (modified == null) {
                operations.add(RemoveOperation(path))
                return
            }
            if (isPrimitive(modified)) {
                operations.add(TestOperation(path, original))
                if (original == null) {
                    operations.add(AddOperation(path, modified))
                } else {
                    operations.add(ReplaceOperation(path, modified))
                }
                return
            }
            val originalType: KClass<*> = original.javaClass
            val fields: Array<java.lang.reflect.Field> = originalType.getDeclaredFields()
            for (field in fields) {
                field.setAccessible(true)
                val fieldType: java.lang.Class<*> = field.getType()
                val origValue: Any = field.get(original)
                val modValue: Any = field.get(modified)
                if (fieldType.isArray() || MutableCollection::class.java.isAssignableFrom(fieldType)) {
                    if (MutableCollection::class.java.isAssignableFrom(fieldType)) {
                        diffList(operations, path + "/" + field.getName(), origValue as List<*>, modValue as List<*>)
                    } else if (fieldType.isArray()) {
                        diffList(
                            operations,
                            path + "/" + field.getName(),
                            java.util.Arrays.asList<Any>(*origValue as Array<Any?>),
                            java.util.Arrays.asList<Any>(*modValue as Array<Any?>)
                        )
                    }
                } else {
                    diffNonList(operations, path + "/" + field.getName(), origValue, modValue)
                }
            }
        }
    }

    private fun isPrimitive(o: Any): Boolean {
        return o is String || o is Number || o is Boolean
    }
}
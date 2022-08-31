package de.sync.core

import de.jds.diffi.DiffAware
import de.jds.diffi.algorithms.Heckel
import de.sync.core.operations.*
import difflib.Delta
import difflib.Delta.TYPE

actual object Diff {

    /**
     * Performs a difference operation between two objects, resulting in a [Patch] describing the differences.
     *
     * @param original the original, unmodified object.
     * @param modified the modified object.
     * @return a [Patch] describing the differences between the two objects.
     * @throws PatchException if an error occurs while performing the difference.
     */
    actual fun diff(original: Any?, modified: Any?): Patch {
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
        val diff: difflib.Patch = Heckel<DiffAware>().diff(original, modified)
        val deltas: List<Delta> = diff.deltas
        for (delta in deltas) {
            val type: TYPE = delta.type
            val revisedPosition: Int = delta.revised.position
            if (type === TYPE.CHANGE) {
                val lines: List<*> = delta.revised.lines
                for (offset in lines.indices) {
                    val originalObject = original[revisedPosition + offset]!!
                    val revisedObject = modified[revisedPosition + offset]!!
                    diffNonList(operations, path + "/" + (revisedPosition + offset), originalObject, revisedObject)
                }
            } else if (type === TYPE.INSERT) {
                val lines: List<*> = delta.revised.lines
                for (offset in lines.indices) {
                    operations.add(AddOperation(path + "/" + (revisedPosition + offset), lines[offset]))
                }
            } else if (type === TYPE.DELETE) {
                val lines: List<*> = delta.original.lines
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
        val originalType = original?.javaClass
        val fields: Array<java.lang.reflect.Field> = originalType?.declaredFields ?: arrayOf()
        for (field in fields) {
            field.isAccessible = true
            val fieldType: Class<*> = field.type
            val origValue: Any = field.get(original)
            val modValue: Any = field.get(modified)
            if (fieldType.isArray || MutableCollection::class.java.isAssignableFrom(fieldType)) {
                if (MutableCollection::class.java.isAssignableFrom(fieldType)) {
                    diffList(operations, path + "/" + field.name, origValue as List<*>, modValue as List<*>)
                } else if (fieldType.isArray) {
                    diffList(
                        operations,
                        path + "/" + field.name,
                        listOf(*origValue as Array<Any?>),
                        listOf(*modValue as Array<Any?>)
                    )
                }
            } else {
                diffNonList(operations, path + "/" + field.name, origValue, modValue)
            }
        }
    }

    private fun isPrimitive(o: Any): Boolean {
        return o is String || o is Number || o is Boolean
    }
}
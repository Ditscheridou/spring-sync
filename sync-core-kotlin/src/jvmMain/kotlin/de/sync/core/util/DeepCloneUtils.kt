package de.sync.core.util

import kotlinx.serialization.Serializable
import org.apache.commons.lang3.SerializationUtils
import kotlinx.serialization.cbor.Cbor

/**
 * Utility methods for deep cloning an object graph.
 *
 * @author Craig Walls
 */
actual object DeepCloneUtils {
    /**
     * Deep clones an object.
     *
     * @param original a single, non-list object to be cloned
     * @param <T>      the object's type
     * @return the cloned object
    </T> */
    actual fun deepClone(original: Serializable): Serializable {
        return Json
    }

    /**
     * Deep clones a list.
     *
     * @param original a list to be cloned
     * @param <T>      the list's generic type
     * @return the cloned list
    </T> */
    actual fun deepClone(original: List<Serializable>): List<Serializable> {
        val copy: MutableList<Serializable> = ArrayList(original.size)
        for (t in original) {
            copy.add(SerializationUtils.clone(t))
        }
        return copy
    }

}
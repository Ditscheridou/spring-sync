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
package de.sync.core.diffsync

import de.jds.shadowstore.Shadow
import de.jds.shadowstore.ShadowStore
import de.sync.core.Diff
import de.sync.core.Patch
import de.sync.core.util.DeepCloneUtils
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

/**
 *
 *
 * Implements essential steps of the Differential Synchronization routine as described in [ Neil Fraser's paper](https://neil.fraser.name/writing/sync/eng047-fraser.pdf).
 *
 *
 *
 *
 * The Differential Synchronization routine can be summarized as follows (with two nodes, A and B):
 *
 *
 *
 *  1. Node A compares a resource with its local shadow of that resource to produce a patch describing the differences
 *  1. Node A replaces the shadow with the resource.
 *  1. Node A sends the difference patch to Node B.
 *  1. Node B applies the patch to its copy of the resource as well as its local shadow of the resource.
 *
 *
 *
 *
 * The routine then repeats with Node A and B swapping roles, forming a continuous loop.
 *
 *
 *
 *
 * To fully understand the Differential Synchronization routine, it's helpful to recognize that a shadow can only be changed by applying a patch or by producing a
 * difference patch; a resource may be changed by applying a patch or by operations performed outside of the loop.
 *
 *
 *
 *
 * This class implements the handling of an incoming patch separately from the producing of the outgoing difference patch.
 * It performs no persistence of the patched resources, which is the responsibility of the caller.
 *
 *
 * @param <T> The entity type to perform differential synchronization against.
 * @author Craig Walls
</T> */
class DiffSync(private val shadowStore: ShadowStore, private val entityType: KClass<Serializable>) {


    /**
     * Applies one or more patches to a target object and the target object's shadow, per the Differential Synchronization algorithm.
     * The target object will remain unchanged and a patched copy will be returned.
     *
     * @param target  An object to apply a patch to. Will remain unchanged.
     * @param patches The patches to be applied.
     * @return a patched copy of the target.
     */
    fun apply(target: Serializable, vararg patches: Patch): Serializable {
        var result = target
        for (patch in patches) {
            result = apply(patch, result)
        }
        return result
    }

    /**
     * Applies a patch to a target object and the target object's shadow, per the Differential Synchronization algorithm.
     * The target object will remain unchanged and a patched copy will be returned.
     *
     * @param patch  The patch to be applied.
     * @param target An object to apply a patch to. Will remain unchanged.
     * @return a patched copy of the target.
     */
    fun apply(patch: Patch, target: Serializable): Serializable {
        if (patch.size() === 0) {
            return target
        }
        var shadow: Shadow<T> = getShadow(target)
        if (patch is VersionedPatch) {
            // e.g., if patch.serverVersion < shadow.serverVersion
            if (patch.serverVersion < shadow.serverVersion) {
                shadow = getBackupShadow(target)
                putShadow(shadow)
            }
        }
        if (shouldApplyPatch(patch, shadow)) {
            shadow = Shadow(
                patch.apply(shadow.resource, entityType), shadow.serverVersion,
                shadow.clientVersion + 1
            )
            val backupShadow: Shadow<T> =
                Shadow(shadow.resource, shadow.serverVersion, shadow.clientVersion)
            putShadow(shadow)
            putBackupShadow(backupShadow)
            return patch.apply(DeepCloneUtils.deepClone(target), entityType)
        }
        return target
    }

    /**
     * Applies one or more patches to a target list and the target list's shadow, per the Differential Synchronization algorithm.
     * The target object will remain unchanged and a patched copy will be returned.
     *
     * @param patches The patch to be applied.
     * @param target  A list to apply a patch to. Will remain unchanged.
     * @return a patched copy of the target.
     */
    fun apply(target: List<Serializable>?, vararg patches: Patch?): List<Serializable>? {
        var result = target
        for (patch in patches) {
            result = apply(patch, result)
        }
        return result
    }

    /**
     * Applies a patch to a target list and the target list's shadow, per the Differential Synchronization algorithm.
     * The target object will remain unchanged and a patched copy will be returned.
     *
     * @param patch  The patch to be applied.
     * @param target A list to apply a patch to. Will remain unchanged.
     * @return a patched copy of the target.
     */
    fun apply(patch: Patch, target: List<Serializable>?): List<Serializable>? {
        if (patch.size() === 0) {
            return target
        }
        var shadow: Shadow<List<Serializable>> = getShadow(target)
        if (patch is VersionedPatch && patch.serverVersion < shadow.serverVersion) {
            shadow = getBackupShadow(target)
            putListShadow(shadow)
        }
        if (shouldApplyPatch(patch, shadow)) {
            shadow = Shadow(
                patch.apply(shadow.resource, entityType), shadow.serverVersion,
                shadow.clientVersion + 1
            )
            val backupShadow: Shadow<List<Serializable>> = Shadow(
                shadow.resource, shadow.serverVersion,
                shadow.clientVersion
            )
            putListShadow(shadow)
            putBackupListShadow(backupShadow)
            return patch.apply(DeepCloneUtils.deepClone(target), entityType)
        }
        return target
    }

    /**
     * Compares a target object with its shadow, producing a patch describing the difference.
     * Upon completion, the shadow will be replaced with the target, per the Differential Synchronization algorithm.
     *
     * @param target The target object to produce a difference patch for.
     * @return a [VersionedPatch] describing the differences between the target and its shadow.
     */
    fun diff(target: Serializable): VersionedPatch {
        var shadow: Shadow<Serializable> = getShadow(target)
        val diff: Patch = Diff.diff(shadow.resource, target)
        val vDiff: VersionedPatch = VersionedPatch(
            diff.getOperations(), shadow.serverVersion,
            shadow.clientVersion
        )
        val patched: Serializable = diff.apply(shadow.resource, entityType)
        shadow = Shadow(patched, shadow.serverVersion + 1, shadow.clientVersion)
        putShadow(shadow)
        return vDiff
    }

    /**
     * Compares a target list with its shadow, producing a patch describing the difference.
     * Upon completion, the shadow will be replaced with the target, per the Differential Synchronization algorithm.
     *
     * @param target The target list to produce a difference patch for.
     * @return a [VersionedPatch] describing the differences between the target and its shadow.
     */
    fun diff(target: List<Serializable>?): VersionedPatch {
        var shadow: Shadow<List<Serializable>> = getShadow(target)
        val diff: Patch = Diff.diff(shadow.resource, target)
        val vDiff = VersionedPatch(
            diff.getOperations(), shadow.serverVersion,
            shadow.clientVersion
        )
        val patched: List<Serializable> = diff.apply(shadow.resource, entityType)
        shadow = Shadow(patched, shadow.serverVersion + 1, shadow.clientVersion)
        putListShadow(shadow)
        return vDiff
    }

    // private helper methods
    private fun shouldApplyPatch(patch: Patch, shadow: Shadow<*>): Boolean {
        if (patch !is VersionedPatch) return true
        val versionedPatch: VersionedPatch = patch
        return (versionedPatch.serverVersion === shadow.serverVersion
                && versionedPatch.clientVersion === shadow.clientVersion)
    }

    private fun getShadow(target: Serializable): Shadow<Serializable> {
        val shadowStoreKey = getShadowStoreKey(target)
        var shadow: Shadow<Serializable>? = shadowStore.getShadow(shadowStoreKey) as Shadow<Serializable>
        if (shadow == null) {
            shadow = Shadow(DeepCloneUtils.deepClone(target), 0, 0) // OKAY
        }
        return shadow
    }

    private fun getBackupShadow(target: Serializable): Shadow<Serializable> {
        val shadowStoreKey = getShadowStoreKey(target) + "_backup"
        var shadow: Shadow<Serializable>? = shadowStore.getShadow(shadowStoreKey) as Shadow<Serializable>
        if (shadow == null) {
            shadow = Shadow(DeepCloneUtils.deepClone(target), 0, 0) // OKAY
        }
        return shadow
    }

    private fun putShadow(shadow: Shadow<Serializable>) {
        val shadowStoreKey: String = getShadowStoreKey(shadow.resource)
        shadowStore.putShadow(shadowStoreKey, shadow)
    }

    private fun putBackupShadow(shadow: Shadow<Serializable>) {
        val shadowStoreKey: String = getShadowStoreKey(shadow.resource) + "_backup"
        shadowStore.putShadow(shadowStoreKey, shadow)
    }

    private fun putListShadow(shadow: Shadow<List<Serializable>>) {
        val shadowStoreKey: String = getShadowStoreKey(shadow.resource)
        shadowStore.putShadow(shadowStoreKey, shadow)
    }

    private fun putBackupListShadow(shadow: Shadow<List<Serializable>>) {
        val shadowStoreKey: String = getShadowStoreKey(shadow.resource) + "_backup"
        shadowStore.putShadow(shadowStoreKey, shadow)
    }

    private fun getShadow(target: List<Serializable>?): Shadow<List<Serializable>?>? {
        val shadowStoreKey = getShadowStoreKey(target)
        var shadow: Shadow<List<Serializable>?>? = shadowStore.getShadow(shadowStoreKey) as Shadow<List<Serializable>?>
        if (shadow == null) {
            shadow = Shadow(DeepCloneUtils.deepClone(target), 0, 0) // OKAY
        }
        return shadow
    }

    private fun getBackupShadow(target: List<Serializable>?): Shadow<List<Serializable>?>? {
        val shadowStoreKey = getShadowStoreKey(target) + "_backup"
        var shadow: Shadow<List<Serializable>?>? = shadowStore.getShadow(shadowStoreKey) as Shadow<List<Serializable>?>
        if (shadow == null) {
            shadow = Shadow(DeepCloneUtils.deepClone(target), 0, 0) // OKAY
        }
        return shadow
    }

    private fun getShadowStoreKey(t: Serializable): String {
        return "shadow/" + entityType.simpleName
    }

    private fun getShadowStoreKey(t: List<Serializable>?): String {
        return "shadow/" + entityType.simpleName + "List"
    }
}
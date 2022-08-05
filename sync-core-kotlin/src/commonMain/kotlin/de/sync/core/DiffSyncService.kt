package de.sync.core

import de.jds.shadowstore.ShadowStore
import de.jds.shadowstore.ShadowStoreFactory
import de.sync.core.diffsync.DiffSync
import de.sync.core.diffsync.Equivalency
import de.sync.core.persistence.PersistenceCallback
import de.sync.core.persistence.PersistenceCallbackRegistry
import kotlinx.serialization.Serializable

class DiffSyncService(
    private val shadowStoreFactory: ShadowStoreFactory,
    private val equivalency: Equivalency,
    private val callbackRegistry: PersistenceCallbackRegistry
) : IDiffSyncService {

    override fun patch(
        resource: String,
        resourceId: String,
        shadowStoreId: String,
        patch: Patch
    ): Patch {
        val persistenceCallback: PersistenceCallback<Serializable> =
            callbackRegistry.findPersistenceCallback(resource)
        val findOne: Any = persistenceCallback.findOne(resourceId)
        return applyAndDiff(patch, findOne, persistenceCallback, shadowStoreId)
    }

    override fun patch(resource: String, patch: Patch, shadowStoreId: String): Patch {
        val persistenceCallback: PersistenceCallback<Serializable> =
            callbackRegistry.findPersistenceCallback(resource)
        return applyAndDiffAgainstList(patch, persistenceCallback.findAll() as List, persistenceCallback, shadowStoreId)
    }

    private fun applyAndDiff(
        patch: Patch, target: Any,
        persistenceCallback: PersistenceCallback<Serializable>,
        shadowStoreId: String
    ): Patch {
        val shadowStore: ShadowStore = getShadowStore(shadowStoreId)
        val sync = DiffSync(shadowStore, persistenceCallback.entityType)
        val patched: Serializable = sync.apply(target, patch)
        persistenceCallback.persistChange(patched)
        return sync.diff(patched)
    }

    private fun applyAndDiffAgainstList(
        patch: Patch, target: List<Serializable>,
        persistenceCallback: PersistenceCallback<Serializable>,
        shadowStoreId: String
    ): Patch {
        val shadowStore: ShadowStore = getShadowStore(shadowStoreId)
        val sync = DiffSync(shadowStore, persistenceCallback.entityType)
        val patched: List<Serializable> = sync.apply(target, patch)
        val itemsToSave: MutableList<Serializable> = ArrayList(patched)
        itemsToSave.removeAll(target)

        // Determine which items should be deleted.
        // Make a shallow copy of the target, remove items that are equivalent to items in the working copy.
        // Equivalent is not the same as equals. It means "this is the same resource, even if it has changed".
        // It usually means "are the id properties equals".
        val itemsToDelete: MutableList<Serializable> = ArrayList(target)
        for (candidate in target) {
            for (item in patched) {
                if (equivalency.isEquivalent(candidate, item)) {
                    itemsToDelete.remove(candidate)
                    break
                }
            }
        }
        persistenceCallback.persistChanges(itemsToSave, itemsToDelete)
        return sync.diff(patched)
    }

    private fun getShadowStore(shadowStoreId: String): ShadowStore {
        val shadowStore: ShadowStore = try {
            shadowStoreFactory.getShadowStore(shadowStoreId)
        } catch (e: Exception) {
            throw PatchException("Could not instantiate a shadow store!")
        }
        return shadowStore
    }
}
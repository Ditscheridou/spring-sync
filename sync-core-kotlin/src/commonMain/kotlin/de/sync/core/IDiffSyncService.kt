package de.sync.core

interface IDiffSyncService {
    fun patch(
        resource: String,
        resourceId: String,
        shadowStoreId: String,
        patch: Patch
    ): Patch?

    fun patch(resource: String, patch: Patch, shadowStoreId: String): Patch?
}
package de.sync.core.diffsync

import de.sync.core.Patch
import de.sync.core.operations.PatchOperation


data class VersionedPatch(
    val operations: List<PatchOperation>,
    val serverVersion: Long,
    val clientVersion: Long
) : Patch(operations)
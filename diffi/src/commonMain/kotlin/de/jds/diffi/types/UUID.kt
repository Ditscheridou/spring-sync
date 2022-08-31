package de.jds.diffi.types

import com.benasher44.uuid.Uuid
import de.jds.diffi.DiffAware
import de.jds.diffi.DiffId

data class UUID(val value: Uuid) : DiffAware {
    override val id: DiffId
        get() = value

}

fun Uuid.diffAware() = UUID(this)

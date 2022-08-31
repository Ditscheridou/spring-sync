package de.jds.diffi.types

import de.jds.diffi.DiffAware
import de.jds.diffi.DiffId

data class String(val value: kotlin.String) : DiffAware {
    override val id: DiffId
        get() = value
}

fun kotlin.String.diffAware() = String(this)

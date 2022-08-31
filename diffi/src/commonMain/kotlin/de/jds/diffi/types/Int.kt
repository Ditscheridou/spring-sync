package de.jds.diffi.types

import de.jds.diffi.DiffAware
import de.jds.diffi.DiffId

data class Int(val value: kotlin.Int) : DiffAware {
    override val id: DiffId
        get() = value
}

fun kotlin.Int.diffAware() = Int(this)

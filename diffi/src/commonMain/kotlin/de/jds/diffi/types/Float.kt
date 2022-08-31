package de.jds.diffi.types

import de.jds.diffi.DiffAware
import de.jds.diffi.DiffId

data class Float(val value: kotlin.Float) : DiffAware {
    override val id: DiffId
        get() = value

}

fun kotlin.Float.diffAware() = Float(this)
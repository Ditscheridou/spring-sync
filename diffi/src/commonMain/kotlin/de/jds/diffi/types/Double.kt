package de.jds.diffi.types

import de.jds.diffi.DiffAware
import de.jds.diffi.DiffId

data class Double(val value: kotlin.Double) : DiffAware {
    override val id: DiffId
        get() = value

}

fun kotlin.Double.diffAware() = Double(this)
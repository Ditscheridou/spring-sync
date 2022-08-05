package de.jds.shadowstore

data class Shadow<T>(
    val resource: T? = null,

    // aka serverVersion in the context of a server app
    var serverVersion: Long,

    // aka clientVersion in the context of a server app
    var clientVersion: Long
)

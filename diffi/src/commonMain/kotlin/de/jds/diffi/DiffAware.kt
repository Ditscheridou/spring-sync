package de.jds.diffi

typealias DiffId = Any

interface DiffAware {
    val id: DiffId
}
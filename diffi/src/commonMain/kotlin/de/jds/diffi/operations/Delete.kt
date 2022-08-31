package de.jds.diffi.operations

data class Delete<T>(val item: T, val index: Int) : Change<T>
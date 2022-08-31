package de.jds.diffi.operations

data class Insert<T>(val item: T, var index: Int) : Change<T>

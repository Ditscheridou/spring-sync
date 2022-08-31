package de.jds.diffi.operations

data class Replace<T>(val oldItem: T, val newItem: T, val index: Int) : Change<T>

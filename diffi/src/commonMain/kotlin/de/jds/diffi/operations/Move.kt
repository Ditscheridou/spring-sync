package de.jds.diffi.operations

class Move<T>(val item: T, val fromIndex: Int, val toIndex: Int) : Change<T> {
}
package de.jds.diffi.algorithms

import de.jds.diffi.DiffAware
import de.jds.diffi.DiffId
import de.jds.diffi.operations.*

class Heckel<T : DiffAware> {
    enum class Counter {
        ZERO, ONE, MANY;

        fun increase(): Counter {
            return when (this) {
                ZERO -> ONE
                ONE -> MANY
                MANY -> this
            }
        }
    }

    fun diff(old: List<T>, new: List<T>): Set<Change<T>> {
        val table = mutableMapOf<DiffId, TableEntry>()
        val oldArray = mutableListOf<ArrayEntry>()
        val newArray = mutableListOf<ArrayEntry>()

        perform1stPass(new, table, newArray)
        perform2ndPass(old, table, oldArray)
        perform345Pass(newArray, oldArray)
        return perform6thPass(new, old, newArray, oldArray)
    }

    private fun perform1stPass(new: List<T>, table: MutableMap<DiffId, TableEntry>, newArray: MutableList<ArrayEntry>) {
        new.forEach { item ->
            val entry = table[item.id] ?: TableEntry()
            entry.newCounter = entry.newCounter.increase()
            newArray.add(entry)
            table[item.id] = entry
        }
    }

    private fun perform2ndPass(old: List<T>, table: MutableMap<DiffId, TableEntry>, oldArray: MutableList<ArrayEntry>) {
        old.forEachIndexed { index, item ->
            val entry = table[item.id] ?: TableEntry()
            entry.oldCounter = entry.oldCounter.increase()
            entry.indexesInOld.add(index)
            oldArray.add(entry)
            table[item.id] = entry
        }
    }

    private fun perform345Pass(newArray: MutableList<ArrayEntry>, oldArray: MutableList<ArrayEntry>) {
        for (indexOfNew in 0 until newArray.size) {
            when (val item = newArray[indexOfNew]) {
                is TableEntry -> {
                    if (item.indexesInOld.isEmpty()) return

                    val indexOfOld = item.indexesInOld.removeFirst()
                    val isObservation1 = item.newCounter == Counter.ONE && item.oldCounter == Counter.ONE
                    val isObservation2 =
                        item.newCounter != Counter.ZERO && item.oldCounter != Counter.ZERO && newArray[indexOfNew] == oldArray[indexOfOld]

                    if (!isObservation1 && !isObservation2) {
                        return
                    }
                    newArray[indexOfNew] = IndexInOther(indexOfOld)
                    newArray[indexOfOld] = IndexInOther(indexOfNew)
                }
                is IndexInOther -> {
                    break
                }
            }
        }
    }

    private fun perform6thPass(
        new: List<T>,
        old: List<T>,
        newArray: MutableList<ArrayEntry>,
        oldArray: MutableList<ArrayEntry>
    ): Set<Change<T>> {
        val changes = mutableSetOf<Change<T>>()
        val deleteOffsets = IntArray(old.size) { 0 }
        deletions(old, oldArray, changes, deleteOffsets)

        var runningOffset = 0
        for (newTuple in newArray.withIndex()) {
            when (newTuple.value) {
                is IndexInOther -> {
                    val oldIndex = (newTuple.value as IndexInOther).id
                    if (old[oldIndex] != new[newTuple.index]) {
                        changes.add(Replace(old[oldIndex], new[newTuple.index], newTuple.index))
                    }

                    if (oldIndex - deleteOffsets[oldIndex] + runningOffset != newTuple.index) {
                        changes.add(Move(new[newTuple.index], oldIndex, newTuple.index))
                    }
                }
                is TableEntry -> {
                    runningOffset++
                    changes.add(Insert(new[newTuple.index], newTuple.index))
                }
            }
        }
        return changes
    }

    private fun deletions(
        old: List<T>,
        oldArray: MutableList<ArrayEntry>,
        changes: MutableSet<Change<T>>,
        deleteOffsets: IntArray
    ) {


        var runningOffset = 0
        for (oldTuple in oldArray.withIndex()) {
            deleteOffsets[oldTuple.index] = runningOffset
            //TODO so richtig mit continue??
            if (oldTuple.value !is TableEntry) continue

            changes.add(Delete(old[oldTuple.index], oldTuple.index))
            runningOffset++

        }
    }

}

sealed class ArrayEntry

data class TableEntry(
    var oldCounter: Heckel.Counter = Heckel.Counter.ZERO,
    var newCounter: Heckel.Counter = Heckel.Counter.ZERO,
    var indexesInOld: MutableList<Int> = mutableListOf()
) : ArrayEntry()

data class IndexInOther(val id: Int) : ArrayEntry()
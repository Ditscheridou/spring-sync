package de.jds.example

import de.sync.core.persistence.PersistenceCallback

class DemoPersistenceCallback : PersistenceCallback<Todo> {
    private val store: MutableMap<String, Todo> = mutableMapOf()
    override fun findAll(): List<Todo> {
        return ArrayList(store.values)
    }

    override fun findOne(id: String?): Todo? {
        return store[id]
    }

    override fun persistChange(s: Todo) {
        store[s.id] = s
    }

    override fun persistChanges(
        itemsToSave: List<Todo>?,
        itemsToDelete: List<Todo>?
    ) {
        itemsToSave?.forEach {
            store[it.id] = it
        }

        itemsToDelete?.forEach {
            store.remove(it.id)
        }
    }

    override val entityType: KClass<Todo>
        get() = Todo::class
}
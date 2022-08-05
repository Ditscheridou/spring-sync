/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sync.operations

import org.junit.jupiter.api.Test
import org.springframework.sync.Todo

internal class CopyOperationTest {
    @Test
    fun copyBooleanPropertyValue() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/1/complete", "/0/complete")
        copy.perform(todos, Todo::class.java)
        Assertions.assertTrue(todos[1].isComplete())
    }

    @Test
    fun copyStringPropertyValue() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/1/description", "/0/description")
        copy.perform(todos, Todo::class.java)
        Assertions.assertEquals("A", todos[1].getDescription())
    }

    @Test
    fun copyBooleanPropertyValueIntoStringProperty() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/1/description", "/0/complete")
        copy.perform(todos, Todo::class.java)
        Assertions.assertEquals("true", todos[1].getDescription())
    }

    @Test
    fun copyListElementToBeginningOfList() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", true))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/0", "/1")
        copy.perform(todos, Todo::class.java)
        Assertions.assertEquals(4, todos.size)
        Assertions.assertEquals(
            2L, todos[0].getId()
                .longValue()
        ) // NOTE: This could be problematic if you try to save it to a DB because there'll be duplicate IDs
        Assertions.assertEquals("B", todos[0].getDescription())
        Assertions.assertTrue(todos[0].isComplete())
    }

    @Test
    fun copyListElementToMiddleOfList() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/2", "/0")
        copy.perform(todos, Todo::class.java)
        Assertions.assertEquals(4, todos.size)
        Assertions.assertEquals(
            1L, todos[2].getId()
                .longValue()
        ) // NOTE: This could be problematic if you try to save it to a DB because there'll be duplicate IDs
        Assertions.assertEquals("A", todos[2].getDescription())
        Assertions.assertTrue(todos[2].isComplete())
    }

    @Test
    fun copyListElementToEndOfList_usingIndex() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/3", "/0")
        copy.perform(todos, Todo::class.java)
        Assertions.assertEquals(4, todos.size)
        Assertions.assertEquals(
            1L, todos[3].getId()
                .longValue()
        ) // NOTE: This could be problematic if you try to save it to a DB because there'll be duplicate IDs
        Assertions.assertEquals("A", todos[3].getDescription())
        Assertions.assertTrue(todos[3].isComplete())
    }

    @Test
    fun copyListElementToEndOfList_usingTilde() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/~", "/0")
        copy.perform(todos, Todo::class.java)
        Assertions.assertEquals(4, todos.size)
        Assertions.assertEquals(
            Todo(1L, "A", true),
            todos[3]
        ) // NOTE: This could be problematic if you try to save it to a DB because there'll be duplicate IDs
    }

    @Test
    fun copyListElementFromEndOfList_usingTilde() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val copy = CopyOperation("/0", "/~")
        copy.perform(todos, Todo::class.java)
        Assertions.assertEquals(4, todos.size)
        Assertions.assertEquals(
            Todo(3L, "C", false),
            todos[0]
        ) // NOTE: This could be problematic if you try to save it to a DB because there'll be duplicate IDs
    }
}
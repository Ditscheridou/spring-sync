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
import org.springframework.sync.PatchException
import org.springframework.sync.Todo

internal class MoveOperationTest {
    @Test
    fun moveBooleanPropertyValue() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        try {
            val move = MoveOperation("/1/complete", "/0/complete")
            move.perform(todos, Todo::class.java)
            Assertions.fail()
        } catch (e: PatchException) {
            assertEquals("Path '/0/complete' is not nullable.", e.getMessage())
        }
        Assertions.assertFalse(todos[1].isComplete())
    }

    @Test
    fun moveStringPropertyValue() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val move = MoveOperation("/1/description", "/0/description")
        move.perform(todos, Todo::class.java)
        Assertions.assertEquals("A", todos[1].getDescription())
    }

    @Test
    fun moveBooleanPropertyValueIntoStringProperty() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        try {
            val move = MoveOperation("/1/description", "/0/complete")
            move.perform(todos, Todo::class.java)
            Assertions.fail()
        } catch (e: PatchException) {
            assertEquals("Path '/0/complete' is not nullable.", e.getMessage())
        }
        Assertions.assertEquals("B", todos[1].getDescription())
    }

    //
    // NOTE: Moving an item about in a list probably has zero effect, as the order of the list is
    //       usually determined by the DB query that produced the list. Moving things around in a
    //       java.util.List and then saving those items really means nothing to the DB, as the
    //       properties that determined the original order are still the same and will result in
    //       the same order when the objects are queries again.
    //
    @Test
    fun moveListElementToBeginningOfList() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", true))
        todos.add(Todo(3L, "C", false))
        val move = MoveOperation("/0", "/1")
        move.perform(todos, Todo::class.java)
        Assertions.assertEquals(3, todos.size)
        Assertions.assertEquals(2L, todos[0].getId().longValue())
        Assertions.assertEquals("B", todos[0].getDescription())
        Assertions.assertTrue(todos[0].isComplete())
    }

    @Test
    fun moveListElementToMiddleOfList() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val move = MoveOperation("/2", "/0")
        move.perform(todos, Todo::class.java)
        Assertions.assertEquals(3, todos.size)
        Assertions.assertEquals(1L, todos[2].getId().longValue())
        Assertions.assertEquals("A", todos[2].getDescription())
        Assertions.assertTrue(todos[2].isComplete())
    }

    @Test
    fun moveListElementToEndOfList_usingIndex() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val move = MoveOperation("/2", "/0")
        move.perform(todos, Todo::class.java)
        Assertions.assertEquals(3, todos.size)
        Assertions.assertEquals(1L, todos[2].getId().longValue())
        Assertions.assertEquals("A", todos[2].getDescription())
        Assertions.assertTrue(todos[2].isComplete())
    }

    @Test
    fun moveListElementToBeginningOfList_usingTilde() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(3L, "C", false))
        todos.add(Todo(4L, "E", false))
        todos.add(Todo(2L, "G", false))
        val expected: MutableList<Todo> = ArrayList<Todo>()
        expected.add(Todo(1L, "A", true))
        expected.add(Todo(2L, "G", false))
        expected.add(Todo(3L, "C", false))
        expected.add(Todo(4L, "E", false))
        val move = MoveOperation("/1", "/~")
        move.perform(todos, Todo::class.java)
        Assertions.assertEquals(expected, todos)
    }

    @Test
    fun moveListElementToEndOfList_usingTilde() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "G", false))
        todos.add(Todo(3L, "C", false))
        todos.add(Todo(4L, "E", false))
        val expected: MutableList<Todo> = ArrayList<Todo>()
        expected.add(Todo(1L, "A", true))
        expected.add(Todo(3L, "C", false))
        expected.add(Todo(4L, "E", false))
        expected.add(Todo(2L, "G", false))
        val move = MoveOperation("/~", "/1")
        move.perform(todos, Todo::class.java)
        Assertions.assertEquals(expected, todos)
    }
}
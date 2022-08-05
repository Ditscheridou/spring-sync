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

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.sync.PatchException
import org.springframework.sync.Todo

internal class TestOperationTest {
    @Test
    fun testPropertyValueEquals() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", true))
        todos.add(Todo(3L, "C", false))
        val test = TestOperation("/0/complete", false)
        test.perform(todos, Todo::class.java)
        val test2 = TestOperation("/1/complete", true)
        test2.perform(todos, Todo::class.java)
    }

    @Test
    fun testPropertyValueNotEquals() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", true))
        todos.add(Todo(3L, "C", false))
        val test = TestOperation("/0/complete", true)
        Assertions.assertThrows(PatchException::class.java, Executable { test.perform(todos, Todo::class.java) })
    }

    @Test
    fun testListElementEquals() {
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", true))
        todos.add(Todo(3L, "C", false))
        val test = TestOperation("/1", Todo(2L, "B", true))
        test.perform(todos, Todo::class.java)
    }
}
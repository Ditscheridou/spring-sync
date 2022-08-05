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

internal class ReplaceOperationTest {
    @Test
    fun replaceBooleanPropertyValue() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val replace = ReplaceOperation("/1/complete", true)
        replace.perform(todos, Todo::class.java)
        Assertions.assertTrue(todos[1].isComplete())
    }

    @Test
    fun replaceTextPropertyValue() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val replace = ReplaceOperation("/1/description", "BBB")
        replace.perform(todos, Todo::class.java)
        Assertions.assertEquals("BBB", todos[1].getDescription())
    }

    @Test
    fun replaceTextPropertyValueWithANumber() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val replace = ReplaceOperation("/1/description", 22)
        replace.perform(todos, Todo::class.java)
        Assertions.assertEquals("22", todos[1].getDescription())
    }
}
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

internal class RemoveOperationTest {
    @Test
    fun removePropertyFromObject() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        RemoveOperation("/1/description").perform(todos, Todo::class.java)
        Assertions.assertNull(todos[1].getDescription())
    }

    @Test
    fun removeItemFromList() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList<Todo>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        RemoveOperation("/1").perform(todos, Todo::class.java)
        Assertions.assertEquals(2, todos.size)
        Assertions.assertEquals("A", todos[0].getDescription())
        Assertions.assertEquals("C", todos[1].getDescription())
    }
}
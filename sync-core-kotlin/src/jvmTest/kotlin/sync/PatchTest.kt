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
package sync

import org.junit.jupiter.api.Test
import org.springframework.sync.operations.PatchOperation

internal class PatchTest {
    @Test
    fun replacePropertyOnEntityInListProperty() {
        var todos = ArrayList<Todo?>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val before = TodoList()
        before.todos = todos
        todos = ArrayList()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        todos.add(Todo(4L, "D", false))
        val after = TodoList()
        after.todos = todos
        val diff: Patch = Diff.diff(before, after)
        val operations: List<PatchOperation> = diff.getOperations()
        assertEquals(1, diff.size())
        assertEquals("add", operations[0].getOp())
        assertEquals("/todos/3", operations[0].getPath())
        assertEquals(Todo(4L, "D", false), operations[0].getValue())
    }
}
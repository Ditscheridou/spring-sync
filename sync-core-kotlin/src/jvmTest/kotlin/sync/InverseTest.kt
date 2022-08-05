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

internal class InverseTest {
    @Test
    fun inverseOnObjects() {
        val original = Todo(123L, "A", false)
        val modified = Todo(124L, "B", true)
        val patch: Patch = Diff.diff(original, modified)
        val patched: Todo = patch.apply(original, Todo::class.java)
        Assertions.assertEquals(modified, patched)
    }

    @Test
    fun inverseOnLists() {
        val original: MutableList<Todo> = ArrayList()
        original.add(Todo(1L, "A", false))
        original.add(Todo(2L, "B", false))
        original.add(Todo(3L, "C", false))
        val modified: MutableList<Todo> = ArrayList()
        modified.add(Todo(111L, "A", false))
        modified.add(Todo(2L, "BBB", false))
        modified.add(Todo(3L, "C", true))
        val patch: Patch = Diff.diff(original, modified)
        val patched: List<Todo> = patch.apply(original, Todo::class.java)
        Assertions.assertEquals(modified, patched)
    }
}
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

import de.sync.core.Patch
import de.sync.core.PatchException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.io.IOException

internal class JsonPatchTest {
    @Test
    @Throws(Exception::class)
    fun manySuccessfulOperations() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        todos.add(Todo(4L, "D", false))
        todos.add(Todo(5L, "E", false))
        todos.add(Todo(6L, "F", false))
        val patch = readJsonPatch("/org/springframework/sync/patch-many-successful-operations.json")
        Assertions.assertEquals(6, patch.size())
        val patchedTodos = patch.apply(todos, Todo::class)
        Assertions.assertEquals(6, todos.size)
        Assertions.assertTrue(patchedTodos[1].complete)
        Assertions.assertEquals("C", patchedTodos[3].description)
        Assertions.assertEquals("A", patchedTodos[4].description)
    }

    @Test
    @Throws(Exception::class)
    fun failureAtBeginning() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        todos.add(Todo(4L, "D", false))
        todos.add(Todo(5L, "E", false))
        todos.add(Todo(6L, "F", false))
        val patch = readJsonPatch("/org/springframework/sync/patch-failing-operation-first.json")
        try {
            patch.apply(todos, Todo::class.java)
            Assertions.fail<Any>()
        } catch (e: PatchException) {
            Assertions.assertEquals("Test against path '/5/description' failed.", e.message)
        }

        // nothing should have changed
        Assertions.assertEquals(6, todos.size)
        Assertions.assertFalse(todos[1].complete)
        Assertions.assertEquals("D", todos[3].description)
        Assertions.assertEquals("E", todos[4].description)
        Assertions.assertEquals("F", todos[5].description)
    }

    @Test
    @Throws(Exception::class)
    fun failureInMiddle() {
        // initial Todo list
        val todos: MutableList<Todo> = ArrayList()
        todos.add(Todo(1L, "A", true))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        todos.add(Todo(4L, "D", false))
        todos.add(Todo(5L, "E", false))
        todos.add(Todo(6L, "F", false))
        val patch = readJsonPatch("/org/springframework/sync/patch-failing-operation-in-middle.json")
        try {
            patch.apply(todos, Todo::class.java)
            Assertions.fail<Any>()
        } catch (e: PatchException) {
            Assertions.assertEquals("Test against path '/5/description' failed.", e.message)
        }

        // nothing should have changed
        Assertions.assertEquals(6, todos.size)
        Assertions.assertFalse(todos[1].complete)
        Assertions.assertEquals("D", todos[3].description)
        Assertions.assertEquals("E", todos[4].description)
        Assertions.assertEquals("F", todos[5].description)
    }

    @Throws(IOException::class)
    private fun readJsonPatch(jsonPatchFile: String): Patch {
        val resource = ClassPathResource(jsonPatchFile)
        val mapper = ObjectMapper()
        val node: JsonNode = mapper.readValue(resource.getInputStream(), JsonNode::class.java)
        return JsonPatchPatchConverter().convert(node)
    }
}
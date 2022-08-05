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

import com.fasterxml.jackson.core.JsonProcessingException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

internal class DiffSyncTest {
    //
    // Apply patches - lists
    //
    @Test
    @Throws(IOException::class)
    fun patchList_emptyPatch() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-empty")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)
        Assertions.assertEquals(patched, todoList)
        // original remains unchanged
        Assertions.assertEquals(todos, todoList)
    }

    @Test
    @Throws(IOException::class)
    fun patchList_addNewItem() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-add-new-item")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(4, patched.size)
        Assertions.assertEquals(todos[0], patched[0])
        Assertions.assertEquals(todos[1], patched[1])
        Assertions.assertEquals(todos[2], patched[2])
        Assertions.assertEquals(sync.Todo(null, "D", false), patched[3])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_changeSingleEntityStatusAndDescription() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-change-single-status-and-desc")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(3, patched.size)
        Assertions.assertEquals(todos[0], patched[0])
        Assertions.assertEquals(sync.Todo(2L, "BBB", true), patched[1])
        Assertions.assertEquals(todos[2], patched[2])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_changeSingleEntityStatus() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-change-single-status")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(3, patched.size)
        Assertions.assertEquals(todos[0], patched[0])
        Assertions.assertEquals(sync.Todo(2L, "B", true), patched[1])
        Assertions.assertEquals(todos[2], patched[2])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_changeStatusAndDeleteTwoItems() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-change-status-and-delete-two-items")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(1, patched.size)
        Assertions.assertEquals(sync.Todo(1L, "A", true), patched[0])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_changeTwoStatusAndDescription() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-change-two-status-and-desc")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(3, patched.size)
        Assertions.assertEquals(sync.Todo(1L, "AAA", false), patched[0])
        Assertions.assertEquals(sync.Todo(2L, "B", true), patched[1])
        Assertions.assertEquals(sync.Todo(3L, "C", false), patched[2])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_deleteTwoItemsAndChangeStatusOnAnother() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-delete-twoitems-and-change-status-on-another")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(1, patched.size)
        Assertions.assertEquals(sync.Todo(3L, "C", true), patched[0])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_patchFailingOperationFirst() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-failing-operation-first")
        val todos = todoList
        var patched: List<Todo?>? = null
        try {
            patched = sync.apply(todos, patch)
            Assertions.fail<Any>()
        } catch (e: PatchException) {
            // original should remain unchanged
            Assertions.assertEquals(todos, todoList)
            Assertions.assertNull(patched)
        }
    }

    @Test
    @Throws(IOException::class)
    fun patchList_patchFailingOperationInMiddle() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-failing-operation-in-middle")
        val todos = todoList
        var patched: List<Todo?>? = null
        try {
            patched = sync.apply(todos, patch)
            Assertions.fail<Any>()
        } catch (e: PatchException) {
            // original should remain unchanged
            Assertions.assertEquals(todos, todoList)
            Assertions.assertNull(patched)
        }
    }

    @Test
    @Throws(IOException::class)
    fun patchList_manySuccessfulOperations() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-many-successful-operations")
        val todos = bigTodoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, bigTodoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(6, patched.size)
        Assertions.assertEquals(sync.Todo(1L, "A", true), patched[0])
        Assertions.assertEquals(sync.Todo(2L, "B", true), patched[1])
        Assertions.assertEquals(sync.Todo(3L, "C", false), patched[2])
        Assertions.assertEquals(sync.Todo(4L, "C", false), patched[3])
        Assertions.assertEquals(sync.Todo(1L, "A", true), patched[4])
        Assertions.assertEquals(sync.Todo(5L, "E", false), patched[5])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_modifyThenRemoveItem() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-modify-then-remove-item")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(2, patched.size)
        Assertions.assertEquals(sync.Todo(1L, "A", false), patched[0])
        Assertions.assertEquals(sync.Todo(3L, "C", false), patched[1])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_removeItem() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-remove-item")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(2, patched.size)
        Assertions.assertEquals(sync.Todo(1L, "A", false), patched[0])
        Assertions.assertEquals(sync.Todo(3L, "C", false), patched[1])
    }

    @Test
    @Throws(IOException::class)
    fun patchList_removeTwoItems() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-remove-two-items")
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, patch)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(1, patched.size)
        Assertions.assertEquals(sync.Todo(1L, "A", false), patched[0])
    }

    //
    // Apply patches - single entity
    //
    @Test
    @Throws(IOException::class)
    fun patchEntity_emptyPatch() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-empty")
        val todo: Todo = sync.Todo(1L, "A", false)
        val patched: Todo = sync.apply(todo, patch)
        Assertions.assertEquals(1L, patched.id.toLong())
        Assertions.assertEquals("A", patched.description)
        Assertions.assertFalse(patched.isComplete)
        // original remains unchanged
        Assertions.assertEquals(1L, todo.id.toLong())
        Assertions.assertEquals("A", todo.description)
        Assertions.assertFalse(todo.isComplete)
    }

    @Test
    @Throws(IOException::class)
    fun patchEntity_booleanProperty() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("single-change-status")
        val todo: Todo = sync.Todo(1L, "A", false)
        val patched: Todo = sync.apply(todo, patch)
        Assertions.assertEquals(1L, patched.id.toLong())
        Assertions.assertEquals("A", patched.description)
        Assertions.assertTrue(patched.isComplete)
        // original remains unchanged
        Assertions.assertEquals(1L, todo.id.toLong())
        Assertions.assertEquals("A", todo.description)
        Assertions.assertFalse(todo.isComplete)
    }

    @Test
    @Throws(IOException::class)
    fun patchEntity_stringProperty() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("single-change-description")
        val todo: Todo = sync.Todo(1L, "A", false)
        val patched: Todo = sync.apply(todo, patch)
        Assertions.assertEquals(1L, patched.id.toLong())
        Assertions.assertEquals("AAA", patched.description)
        Assertions.assertFalse(patched.isComplete)
        // original remains unchanged
        Assertions.assertEquals(1L, todo.id.toLong())
        Assertions.assertEquals("A", todo.description)
        Assertions.assertFalse(todo.isComplete)
    }

    @Test
    @Throws(IOException::class)
    fun patchEntity_numericProperty() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("single-change-id")
        val todo: Todo = sync.Todo(1L, "A", false)
        val patched: Todo = sync.apply(todo, patch)
        Assertions.assertEquals(123L, patched.id.toLong())
        Assertions.assertEquals("A", patched.description)
        Assertions.assertFalse(patched.isComplete)
        // original remains unchanged
        Assertions.assertEquals(1L, todo.id.toLong())
        Assertions.assertEquals("A", todo.description)
        Assertions.assertFalse(todo.isComplete)
    }

    @Test
    @Throws(IOException::class)
    fun patchEntity_stringAndBooleanProperties() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("single-change-status-and-desc")
        val todo: Todo = sync.Todo(1L, "A", false)
        val patched: Todo = sync.apply(todo, patch)
        Assertions.assertEquals(1L, patched.id.toLong())
        Assertions.assertEquals("BBB", patched.description)
        Assertions.assertTrue(patched.isComplete)
        // original remains unchanged
        Assertions.assertEquals(1L, todo.id.toLong())
        Assertions.assertEquals("A", todo.description)
        Assertions.assertFalse(todo.isComplete)
    }

    @Test
    fun patchEntity_moveProperty() {
        val sync: DiffSync<Person> = DiffSync(MapBasedShadowStore("x"), Person::class.java)
        val ops: MutableList<PatchOperation> = ArrayList<PatchOperation>()
        ops.add(MoveOperation("/firstName", "/lastName"))
        val patch = Patch(ops)
        val person: Person = sync.Person("Edmund", "Blackadder")
        val patched: Person = sync.apply(person, patch)
        Assertions.assertEquals("Blackadder", patched.getFirstName())
        Assertions.assertNull(patched.getLastName())
    }

    //
    // Guaranteed Delivery - Normal operations scenario
    //
    @Test
    @Throws(IOException::class)
    fun patchList_addNewItem_normal() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-add-new-item")
        val versionedPatch = VersionedPatch(patch.getOperations(), 0, 0)
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, versionedPatch)
        val diff: VersionedPatch = sync.diff(patched)
        // the server is acknowledge client version 1 (the client should be at that version by this time)
        assertEquals(1, diff.clientVersion)
        // the server created the patch against server version 0 (but it will be 1 after the patch is created)
        assertEquals(0, diff.getServerVersion())

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(4, patched.size)
        Assertions.assertEquals(todos[0], patched[0])
        Assertions.assertEquals(todos[1], patched[1])
        Assertions.assertEquals(todos[2], patched[2])
        Assertions.assertEquals(sync.Todo(null, "D", false), patched[3])
    }

    @Test
    fun patchEntity_moveProperty_normal() {
        val sync: DiffSync<Person> = DiffSync(MapBasedShadowStore("x"), Person::class.java)
        val ops: MutableList<PatchOperation> = ArrayList<PatchOperation>()
        ops.add(MoveOperation("/firstName", "/lastName"))
        val vPatch1 = VersionedPatch(ops, 0, 0)
        val person: Person = sync.Person("Edmund", "Blackadder")
        val patched: Person = sync.apply(person, vPatch1)
        val diff: VersionedPatch = sync.diff(patched)
        assertEquals(
            1,
            diff.clientVersion
        ) // the server is acknowledge client version 1 (the client should be at that version by this time)
        assertEquals(
            0,
            diff.getServerVersion()
        ) // the server created the patch against server version 0 (but it will be 1 after the patch is created)
        Assertions.assertEquals("Blackadder", patched.getFirstName())
        Assertions.assertNull(patched.getLastName())
    }

    //
    // Guaranteed Delivery - Duplicate packet scenario
    //
    @Test
    @Throws(IOException::class)
    fun patchList_addNewItem_duplicate() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)
        val patch: Patch = readJsonPatchFromResource("patch-add-new-item")
        val versionedPatch = VersionedPatch(patch.getOperations(), 0, 0)
        val versionedPatch2 = VersionedPatch(patch.getOperations(), 0, 0)
        val todos = todoList
        val patched: List<Todo> = sync.apply(todos, versionedPatch, versionedPatch2)
        val diff: VersionedPatch = sync.diff(patched)
        assertEquals(
            1,
            diff.clientVersion
        ) // the server is acknowledge client version 1 (the client should be at that version by this time)
        assertEquals(
            0,
            diff.getServerVersion()
        ) // the server created the patch against server version 0 (but it will be 1 after the patch is created)

        // original should remain unchanged
        Assertions.assertEquals(todos, todoList)
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(4, patched.size)
        Assertions.assertEquals(todos[0], patched[0])
        Assertions.assertEquals(todos[1], patched[1])
        Assertions.assertEquals(todos[2], patched[2])
        Assertions.assertEquals(sync.Todo(null, "D", false), patched[3])
    }

    @Test
    fun patchEntity_moveProperty_duplicate() {
        val sync: DiffSync<Person> = DiffSync(MapBasedShadowStore("x"), Person::class.java)
        val ops: MutableList<PatchOperation> = ArrayList<PatchOperation>()
        ops.add(MoveOperation("/firstName", "/lastName"))
        val vPatch1 = VersionedPatch(ops, 0, 0)
        val vPatch2 = VersionedPatch(ops, 0, 0)
        val person: Person = sync.Person("Edmund", "Blackadder")
        val patched: Person = sync.apply(person, vPatch1, vPatch2)
        val diff: VersionedPatch = sync.diff(patched)
        assertEquals(
            1,
            diff.clientVersion
        ) // the server is acknowledge client version 1 (the client should be at that version by this time)
        assertEquals(
            0,
            diff.getServerVersion()
        ) // the server created the patch against server version 0 (but it will be 1 after the patch is created)
        Assertions.assertEquals("Blackadder", patched.getFirstName())
        Assertions.assertNull(patched.getLastName())
    }

    //
    // Guaranteed Delivery - Lost outbound packet scenario
    //
    // TODO: This is primarily a client-side case. By definition, the server never receives the patch.
    //       Therefore, there's nothing server-side to be tested.
    //       However, this case *does* apply to Spring Sync when used in an Android client.
    //       Therefore, tests for this scenario will need to be fleshed out.
    //
    // Guaranteed Delivery - Lost return packet scenario
    //
    @Test
    fun patchList_addNewItem_lostReturn() {
        val sync: DiffSync<Todo> = DiffSync(MapBasedShadowStore("x"), Todo::class.java)

        // Create the list resource
        val todos = todoList

        // Apply an initial patch to get the server shadow's client version bumped up.
        // Initially, the server shadow's server and client versions are both 0,
        // matching the incoming patch's versions, so the patch is applied normally.
        val ops1: MutableList<PatchOperation> = ArrayList<PatchOperation>()
        ops1.add(AddOperation("/~", sync.Todo(100L, "NEW ITEM 100", false)))
        val versionedPatch = VersionedPatch(ops1, 0, 0)

        // At this point, the client sends the patch to the server, the client puts the patch in an outbound stack,
        // the client increments its shadow client version to 1, and the server calls sync.apply() to apply the patch.
        var patched: List<Todo?> = sync.apply(todos, versionedPatch)

        // After the patch is applied, the server shadow versions are
        //   - Primary shadow: serverVersion = 0, clientVersion = 1
        //   - Backup shadow : serverVersion = 0, clientVersion = 1

        // At this point, the server's shadow has client version 1 and server version 0
        // The server then copies its current shadow to backup shadow before performing a new diff against the shadow, bumping the server version to 1 *after* the diff is performed.
        // The backup shadow, having been taken before the new diff was created, still has server version 0.
        // Before it performs the diff, however, it copies its current shadow to backup shadow.
        // The diff was performed against the shadow whose client version 1 and server version 0, therefore the patch will have client version 1 and server version 0.
        val lostDiff: VersionedPatch = sync.diff(patched)

        // After the diff is applied, the server shadow's server version is incremented.
        //   - Primary shadow: serverVersion = 1, clientVersion = 1
        //   - Backup shadow : serverVersion = 0, clientVersion = 1

        // Verify that the patch has client version 1, server version 0
        assertEquals(1, lostDiff.clientVersion)
        assertEquals(0, lostDiff.getServerVersion())

        // In the lost return packet scenario, the client never receives that return diff (lostDiff) or acknowledgement of the server having applied the first patch.
        // The client can only assume that the server never received it (although it did).
        // So it produces a new patch against its shadow (whose server version is still at 0 and client version is 1).
        // It then sends both patches to the server and the server attempts to apply them both.
        val ops2: MutableList<PatchOperation> = ArrayList<PatchOperation>()
        ops2.add(AddOperation("/~", sync.Todo(200L, "NEW ITEM 200", false)))
        val versionedPatch2 = VersionedPatch(ops2, 0, 1)
        patched = sync.apply(patched, versionedPatch, versionedPatch2)

        // The first patch's server version is 0, which is less than the server shadow's server version of 1.
        // This indicates a lost packet scenario, meaning that the client never received or applied the
        // return patch from the previous cycle.
        // So the server resurrects the backup shadow into the primary shadow:
        //   - Primary shadow: serverVersion = 0, clientVersion = 1
        //   - Backup shadow : serverVersion = 0, clientVersion = 1
        // Then it tries to apply the first patch. Since the patch's client version is less than the shadow's client version,
        // it ignores the patch as a duplicate (that was applied earlier)
        // Then it tries to apply the second patch. This patch's client version is the same as the shadow's client version,
        // so it applies it as with normal operation.

        // After the applying the 2nd patch, the server shadow's server version is incremented.
        //   - Primary shadow: serverVersion = 0, clientVersion = 2
        //   - Backup shadow : serverVersion = 0, clientVersion = 2

        // Finally, the server performs a diff against the shadow (whose server version is 0 and whose client version is 2).
        // Therefore, the patch produced should have client version 2, server version 0.
        // After the diff, the server version will be 1, but there's no way to verify that, except to perform another patch.
        val diff: VersionedPatch = sync.diff(patched)
        assertEquals(
            2,
            diff.clientVersion
        ) // the server is acknowledging client version 1 and 2 (the client should be at that version by this time)
        assertEquals(
            0,
            diff.getServerVersion()
        ) // the server created the patch against server version 0 (but it will be 1 after the patch is created)

        // After the diff is applied, the server shadow's server version is incremented.
        //   - Primary shadow: serverVersion = 1, clientVersion = 2
        //   - Backup shadow : serverVersion = 0, clientVersion = 2

        // Now test that the resulting list is as expected.
        // The original should remain unchanged
        Assertions.assertEquals(todos, todoList)

        // The patched resource should now contain 2 additional items, one from each patch sent.
        // It should *NOT* have two of the item that was added as part of the initial patch (the one that was sent twice).
        Assertions.assertNotEquals(patched, todos)
        Assertions.assertEquals(
            5,
            patched.size
        ) // Should only have added 2 new items. It shouldn't have added the first new item twice.
        Assertions.assertEquals(todos[0], patched[0])
        Assertions.assertEquals(todos[1], patched[1])
        Assertions.assertEquals(todos[2], patched[2])
        Assertions.assertEquals(sync.Todo(100L, "NEW ITEM 100", false), patched[3])
        Assertions.assertEquals(sync.Todo(200L, "NEW ITEM 200", false), patched[4])
    }

    @Test
    fun patchEntity_moveProperty_lostReturnPacket() {
        val sync: DiffSync<Person> = DiffSync(MapBasedShadowStore("x"), Person::class.java)
        val person: Person = sync.Person("Edmund", "Blackadder")
        val ops1: MutableList<PatchOperation> = ArrayList<PatchOperation>()
        ops1.add(MoveOperation("/firstName", "/lastName"))
        val vPatch1 = VersionedPatch(ops1, 0, 0)
        var patched: Person = sync.apply(person, vPatch1)
        Assertions.assertEquals("Blackadder", patched.getFirstName())
        Assertions.assertNull(patched.getLastName())
        val lostDiff: VersionedPatch = sync.diff(patched)
        assertEquals(1, lostDiff.clientVersion)
        assertEquals(0, lostDiff.getServerVersion())
        val ops2: MutableList<PatchOperation> = ArrayList<PatchOperation>()
        ops2.add(MoveOperation("/lastName", "/firstName"))
        val vPatch2 = VersionedPatch(ops2, 0, 1)
        patched = sync.apply(patched, vPatch1, vPatch2)
        val diff: VersionedPatch = sync.diff(patched)
        assertEquals(2, diff.clientVersion)
        assertEquals(0, diff.getServerVersion())
        Assertions.assertNull(patched.getFirstName())
        Assertions.assertEquals("Blackadder", patched.getLastName())
    }

    //
    // private helpers
    //
    private val todoList: List<Todo>
        private get() {
            val todos: MutableList<Todo> = ArrayList()
            todos.add(Todo(1L, "A", false))
            todos.add(Todo(2L, "B", false))
            todos.add(Todo(3L, "C", false))
            return todos
        }
    private val bigTodoList: List<Todo>
        private get() {
            val todos: MutableList<Todo> = ArrayList()
            todos.add(Todo(1L, "A", true))
            todos.add(Todo(2L, "B", false))
            todos.add(Todo(3L, "C", false))
            todos.add(Todo(4L, "D", false))
            todos.add(Todo(5L, "E", false))
            todos.add(Todo(6L, "F", false))
            return todos
        }

    @Throws(IOException::class, JsonProcessingException::class)
    private fun readJsonPatchFromResource(resource: String): Patch {
        return JsonPatchPatchConverter().convert(OBJECT_MAPPER.readTree(resource(resource)))
    }

    @Throws(IOException::class)
    private fun resource(name: String): String {
        val resource = ClassPathResource("/org/springframework/sync/$name.json")
        val reader = BufferedReader(InputStreamReader(resource.getInputStream()))
        val builder = StringBuilder()
        while (reader.ready()) {
            builder.append(reader.readLine())
        }
        return builder.toString()
    }

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }
}
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

internal class DiffTest {
    @Test
    fun noChanges() {
        val original = buildTodoList()
        val modified = buildTodoList()
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(0, diff.size())
    }

    @Test
    fun nullPropertyToNonNullProperty() {
        val original = Todo(null, "A", false)
        val modified = Todo(1L, "A", false)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        val op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/id", op.getPath())
        Assertions.assertNull(op.getValue())
    }

    @Test
    fun singleBooleanPropertyChangeOnObject() {
        val original = Todo(1L, "A", false)
        val modified = Todo(1L, "A", true)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/complete", op.getPath())
        Assertions.assertFalse(op.getValue() as Boolean)
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/complete", op.getPath())
        Assertions.assertTrue(op.getValue() as Boolean)
    }

    @Test
    fun singleStringPropertyChangeOnObject() {
        val original = Todo(1L, "A", false)
        val modified = Todo(1L, "B", false)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/description", op.getPath())
        assertEquals("A", op.getValue())
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/description", op.getPath())
        assertEquals("B", op.getValue())
    }

    @Test
    fun singleNumericPropertyChangeOnObject() {
        val original = Todo(1L, "A", false)
        val modified = Todo(2L, "A", false)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/id", op.getPath())
        assertEquals(1L, op.getValue())
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/id", op.getPath())
        assertEquals(2L, op.getValue())
    }

    @Test
    fun changeTwoPropertiesOnObject() {
        val original = Todo(1L, "A", false)
        val modified = Todo(1L, "B", true)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(4, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/description", op.getPath())
        assertEquals("A", op.getValue())
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/description", op.getPath())
        assertEquals("B", op.getValue())
        op = ops[2]
        assertEquals("test", op.getOp())
        assertEquals("/complete", op.getPath())
        assertEquals(false, op.getValue())
        op = ops[3]
        assertEquals("replace", op.getOp())
        assertEquals("/complete", op.getPath())
        assertEquals(true, op.getValue())
    }

    @Test
    fun singleBooleanPropertyChangeOnItemInList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified[1].isComplete = true
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/1/complete", op.getPath())
        assertEquals(false, op.getValue())
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/1/complete", op.getPath())
        assertEquals(true, op.getValue())
    }

    @Test
    fun singleStringPropertyChangeOnItemInList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified[1].description = "BBB"
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/1/description", op.getPath())
        assertEquals("B", op.getValue())
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/1/description", op.getPath())
        assertEquals("BBB", op.getValue())
    }

    @Test
    fun singleMultiplePropertyChangeOnItemInList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified[1].isComplete = true
        modified[1].description = "BBB"
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(4, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/1/description", op.getPath())
        assertEquals("B", op.getValue())
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/1/description", op.getPath())
        assertEquals("BBB", op.getValue())
        op = ops[2]
        assertEquals("test", op.getOp())
        assertEquals("/1/complete", op.getPath())
        assertEquals(false, op.getValue())
        op = ops[3]
        assertEquals("replace", op.getOp())
        assertEquals("/1/complete", op.getPath())
        assertEquals(true, op.getValue())
    }

    @Test
    fun propertyChangeOnTwoItemsInList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified[0].description = "AAA"
        modified[1].isComplete = true
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(4, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/0/description", op.getPath())
        assertEquals("A", op.getValue())
        op = ops[1]
        assertEquals("replace", op.getOp())
        assertEquals("/0/description", op.getPath())
        assertEquals("AAA", op.getValue())
        op = ops[2]
        assertEquals("test", op.getOp())
        assertEquals("/1/complete", op.getPath())
        assertEquals(false, op.getValue())
        op = ops[3]
        assertEquals("replace", op.getOp())
        assertEquals("/1/complete", op.getPath())
        assertEquals(true, op.getValue())
    }

    @Test
    fun insertItemAtBeginningOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.add(0, Todo(0L, "Z", false))
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(1, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        val op: PatchOperation = ops[0]
        assertEquals("add", op.getOp())
        assertEquals("/0", op.getPath())
        val value = op.getValue() as Todo
        Assertions.assertEquals(0L, value.id.toLong())
        Assertions.assertEquals("Z", value.description)
        Assertions.assertFalse(value.isComplete)
    }

    @Test
    fun insertTwoItemsAtBeginningOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.add(0, Todo(25L, "Y", false))
        modified.add(0, Todo(26L, "Z", true))
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("add", op.getOp())
        assertEquals("/0", op.getPath())
        var value = op.getValue() as Todo
        Assertions.assertEquals(26L, value.id.toLong())
        Assertions.assertEquals("Z", value.description)
        Assertions.assertTrue(value.isComplete)
        op = ops[1]
        assertEquals("add", op.getOp())
        assertEquals("/1", op.getPath())
        value = op.getValue()
        Assertions.assertEquals(25L, value.id.toLong())
        Assertions.assertEquals("Y", value.description)
        Assertions.assertFalse(value.isComplete)
    }

    @Test
    fun insertItemAtMiddleOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.add(2, Todo(0L, "Z", false))
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(1, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        val op: PatchOperation = ops[0]
        assertEquals("add", op.getOp())
        assertEquals("/2", op.getPath())
        val value = op.getValue() as Todo
        Assertions.assertEquals(0L, value.id.toLong())
        Assertions.assertEquals("Z", value.description)
        Assertions.assertFalse(value.isComplete)
    }

    @Test
    fun insertTwoItemsAtMiddleOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.add(2, Todo(25L, "Y", false))
        modified.add(2, Todo(26L, "Z", true))
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("add", op.getOp())
        assertEquals("/2", op.getPath())
        var value = op.getValue() as Todo
        Assertions.assertEquals(26L, value.id.toLong())
        Assertions.assertEquals("Z", value.description)
        Assertions.assertTrue(value.isComplete)
        op = ops[1]
        assertEquals("add", op.getOp())
        assertEquals("/3", op.getPath())
        value = op.getValue()
        Assertions.assertEquals(25L, value.id.toLong())
        Assertions.assertEquals("Y", value.description)
        Assertions.assertFalse(value.isComplete)
    }

    @Test
    fun insertItemAtEndOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.add(3, Todo(0L, "Z", false))
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(1, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        val op: PatchOperation = ops[0]
        assertEquals("add", op.getOp())
        assertEquals("/3", op.getPath())
        val value = op.getValue() as Todo
        Assertions.assertEquals(0L, value.id.toLong())
        Assertions.assertEquals("Z", value.description)
        Assertions.assertFalse(value.isComplete)
    }

    @Test
    fun insertTwoItemsAtEndOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.add(3, Todo(25L, "Y", false))
        modified.add(4, Todo(26L, "Z", true))
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("add", op.getOp())
        assertEquals("/3", op.getPath())
        var value = op.getValue() as Todo
        Assertions.assertEquals(25L, value.id.toLong())
        Assertions.assertEquals("Y", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[1]
        assertEquals("add", op.getOp())
        assertEquals("/4", op.getPath())
        value = op.getValue()
        Assertions.assertEquals(26L, value.id.toLong())
        Assertions.assertEquals("Z", value.description)
        Assertions.assertTrue(value.isComplete)
    }

    @Test
    fun insertItemsAtBeginningAndEndOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.add(0, Todo(25L, "Y", false))
        modified.add(4, Todo(26L, "Z", true))
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("add", op.getOp())
        assertEquals("/0", op.getPath())
        var value = op.getValue() as Todo
        Assertions.assertEquals(25L, value.id.toLong())
        Assertions.assertEquals("Y", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[1]
        assertEquals("add", op.getOp())
        assertEquals("/4", op.getPath())
        value = op.getValue()
        Assertions.assertEquals(26L, value.id.toLong())
        Assertions.assertEquals("Z", value.description)
        Assertions.assertTrue(value.isComplete)
    }

    @Test
    fun removeItemFromBeginningOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.removeAt(0)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/0", op.getPath())
        val value = op.getValue() as Todo
        Assertions.assertEquals(1L, value.id.toLong())
        Assertions.assertEquals("A", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[1]
        assertEquals("remove", op.getOp())
        assertEquals("/0", op.getPath())
    }

    @Test
    fun removeItemFromMiddleOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.removeAt(1)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/1", op.getPath())
        val value = op.getValue() as Todo
        Assertions.assertEquals(2L, value.id.toLong())
        Assertions.assertEquals("B", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[1]
        assertEquals("remove", op.getOp())
        assertEquals("/1", op.getPath())
    }

    @Test
    fun removeItemFromEndOfList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.removeAt(2)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(2, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/2", op.getPath())
        val value = op.getValue() as Todo
        Assertions.assertEquals(3L, value.id.toLong())
        Assertions.assertEquals("C", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[1]
        assertEquals("remove", op.getOp())
        assertEquals("/2", op.getPath())
    }

    @Test
    fun removeAllItemsFromList() {
        val original = buildTodoList()
        val modified = buildTodoList()
        modified.removeAt(0)
        modified.removeAt(0)
        modified.removeAt(0)
        val diff: Patch = Diff.diff(original, modified)
        assertEquals(6, diff.size())
        val ops: List<PatchOperation> = diff.getOperations()
        var op: PatchOperation = ops[0]
        assertEquals("test", op.getOp())
        assertEquals("/0", op.getPath())
        var value = op.getValue() as Todo
        Assertions.assertEquals(1L, value.id.toLong())
        Assertions.assertEquals("A", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[1]
        assertEquals("remove", op.getOp())
        assertEquals("/0", op.getPath())
        op = ops[2]
        assertEquals("test", op.getOp())
        assertEquals("/0", op.getPath())
        value = op.getValue()
        Assertions.assertEquals(2L, value.id.toLong())
        Assertions.assertEquals("B", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[3]
        assertEquals("remove", op.getOp())
        assertEquals("/0", op.getPath())
        op = ops[4]
        assertEquals("test", op.getOp())
        assertEquals("/0", op.getPath())
        value = op.getValue()
        Assertions.assertEquals(3L, value.id.toLong())
        Assertions.assertEquals("C", value.description)
        Assertions.assertFalse(value.isComplete)
        op = ops[5]
        assertEquals("remove", op.getOp())
        assertEquals("/0", op.getPath())
    }

    @Test
    fun addEntryToListProperty() {
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

    @Test
    fun removeEntryFromListProperty() {
        var todos = ArrayList<Todo?>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val before = TodoList()
        before.todos = todos
        todos = ArrayList()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(3L, "C", false))
        val after = TodoList()
        after.todos = todos
        val diff: Patch = Diff.diff(before, after)
        val operations: List<PatchOperation> = diff.getOperations()
        assertEquals(2, diff.size())
        assertEquals("test", operations[0].getOp())
        assertEquals("/todos/1", operations[0].getPath())
        assertEquals(Todo(2L, "B", false), operations[0].getValue())
        assertEquals("remove", operations[1].getOp())
        assertEquals("/todos/1", operations[1].getPath())
    }

    @Test
    fun editEntryInListProperty() {
        var todos = ArrayList<Todo?>()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        val before = TodoList()
        before.todos = todos
        todos = ArrayList()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "BBB", true))
        todos.add(Todo(3L, "C", false))
        val after = TodoList()
        after.todos = todos
        val diff: Patch = Diff.diff(before, after)
        val operations: List<PatchOperation> = diff.getOperations()
        assertEquals(4, diff.size())
        assertEquals("test", operations[0].getOp())
        assertEquals("/todos/1/description", operations[0].getPath())
        assertEquals("B", operations[0].getValue())
        assertEquals("replace", operations[1].getOp())
        assertEquals("/todos/1/description", operations[1].getPath())
        assertEquals("BBB", operations[1].getValue())
        assertEquals("test", operations[2].getOp())
        assertEquals("/todos/1/complete", operations[2].getPath())
        assertEquals(false, operations[2].getValue())
        assertEquals("replace", operations[3].getOp())
        assertEquals("/todos/1/complete", operations[3].getPath())
        assertEquals(true, operations[3].getValue())
    }

    @Test
    fun addEntryToArrayProperty() {
        var todos = arrayOfNulls<Todo>(3)
        todos[0] = Todo(1L, "A", false)
        todos[1] = Todo(2L, "B", false)
        todos[2] = Todo(3L, "C", false)
        val before = TodoList()
        before.todoArray = todos
        todos = arrayOfNulls(4)
        todos[0] = Todo(1L, "A", false)
        todos[1] = Todo(2L, "B", false)
        todos[2] = Todo(3L, "C", false)
        todos[3] = Todo(4L, "D", false)
        val after = TodoList()
        after.todoArray = todos
        val diff: Patch = Diff.diff(before, after)
        val operations: List<PatchOperation> = diff.getOperations()
        assertEquals(1, diff.size())
        assertEquals("add", operations[0].getOp())
        assertEquals("/todoArray/3", operations[0].getPath())
        assertEquals(Todo(4L, "D", false), operations[0].getValue())
    }

    @Test
    fun removeEntryFromArrayProperty() {
        var todos = arrayOfNulls<Todo>(3)
        todos[0] = Todo(1L, "A", false)
        todos[1] = Todo(2L, "B", false)
        todos[2] = Todo(3L, "C", false)
        val before = TodoList()
        before.todoArray = todos
        todos = arrayOfNulls(2)
        todos[0] = Todo(1L, "A", false)
        todos[1] = Todo(3L, "C", false)
        val after = TodoList()
        after.todoArray = todos
        val diff: Patch = Diff.diff(before, after)
        val operations: List<PatchOperation> = diff.getOperations()
        assertEquals(2, diff.size())
        assertEquals("test", operations[0].getOp())
        assertEquals("/todoArray/1", operations[0].getPath())
        assertEquals(Todo(2L, "B", false), operations[0].getValue())
        assertEquals("remove", operations[1].getOp())
        assertEquals("/todoArray/1", operations[1].getPath())
    }

    @Test
    fun editEntryInArrayProperty() {
        var todos = arrayOfNulls<Todo>(3)
        todos[0] = Todo(1L, "A", false)
        todos[1] = Todo(2L, "B", false)
        todos[2] = Todo(3L, "C", false)
        val before = TodoList()
        before.todoArray = todos
        todos = arrayOfNulls(3)
        todos[0] = Todo(1L, "A", false)
        todos[1] = Todo(2L, "BBB", true)
        todos[2] = Todo(3L, "C", false)
        val after = TodoList()
        after.todoArray = todos
        val diff: Patch = Diff.diff(before, after)
        val operations: List<PatchOperation> = diff.getOperations()
        assertEquals(4, diff.size())
        assertEquals("test", operations[0].getOp())
        assertEquals("/todoArray/1/description", operations[0].getPath())
        assertEquals("B", operations[0].getValue())
        assertEquals("replace", operations[1].getOp())
        assertEquals("/todoArray/1/description", operations[1].getPath())
        assertEquals("BBB", operations[1].getValue())
        assertEquals("test", operations[2].getOp())
        assertEquals("/todoArray/1/complete", operations[2].getPath())
        assertEquals(false, operations[2].getValue())
        assertEquals("replace", operations[3].getOp())
        assertEquals("/todoArray/1/complete", operations[3].getPath())
        assertEquals(true, operations[3].getValue())
    }

    private fun buildTodoList(): List<Todo> {
        val original: MutableList<Todo> = ArrayList()
        original.add(Todo(1L, "A", false))
        original.add(Todo(2L, "B", false))
        original.add(Todo(3L, "C", false))
        return original
    }
}
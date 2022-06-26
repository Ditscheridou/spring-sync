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
package org.springframework.sync;

import org.junit.jupiter.api.Test;
import org.springframework.sync.operations.PatchOperation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiffTest {

  @Test
  void noChanges() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();

    Patch diff = Diff.diff(original, modified);
    assertEquals(0, diff.size());
  }

  @Test
  void nullPropertyToNonNullProperty() {
    Todo original = new Todo(null, "A", false);
    Todo modified = new Todo(1L, "A", false);
    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());

    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/id", op.getPath());
    assertNull(op.getValue());
  }

  @Test
  void singleBooleanPropertyChangeOnObject() {
    Todo original = new Todo(1L, "A", false);
    Todo modified = new Todo(1L, "A", true);

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());

    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/complete", op.getPath());
    assertFalse((Boolean) op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/complete", op.getPath());
    assertTrue((Boolean) op.getValue());
  }

  @Test
  void singleStringPropertyChangeOnObject() {
    Todo original = new Todo(1L, "A", false);
    Todo modified = new Todo(1L, "B", false);

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/description", op.getPath());
    assertEquals("A", op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/description", op.getPath());
    assertEquals("B", op.getValue());
  }

  @Test
  void singleNumericPropertyChangeOnObject() {
    Todo original = new Todo(1L, "A", false);
    Todo modified = new Todo(2L, "A", false);

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/id", op.getPath());
    assertEquals(1L, op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/id", op.getPath());
    assertEquals(2L, op.getValue());
  }

  @Test
  void changeTwoPropertiesOnObject() {
    Todo original = new Todo(1L, "A", false);
    Todo modified = new Todo(1L, "B", true);

    Patch diff = Diff.diff(original, modified);
    assertEquals(4, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/description", op.getPath());
    assertEquals("A", op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/description", op.getPath());
    assertEquals("B", op.getValue());
    op = ops.get(2);
    assertEquals("test", op.getOp());
    assertEquals("/complete", op.getPath());
    assertEquals(false, op.getValue());
    op = ops.get(3);
    assertEquals("replace", op.getOp());
    assertEquals("/complete", op.getPath());
    assertEquals(true, op.getValue());
  }

  @Test
  void singleBooleanPropertyChangeOnItemInList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.get(1).setComplete(true);

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/1/complete", op.getPath());
    assertEquals(false, op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/1/complete", op.getPath());
    assertEquals(true, op.getValue());
  }

  @Test
  void singleStringPropertyChangeOnItemInList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.get(1).setDescription("BBB");

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/1/description", op.getPath());
    assertEquals("B", op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/1/description", op.getPath());
    assertEquals("BBB", op.getValue());
  }

  @Test
  void singleMultiplePropertyChangeOnItemInList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.get(1).setComplete(true);
    modified.get(1).setDescription("BBB");

    Patch diff = Diff.diff(original, modified);
    assertEquals(4, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/1/description", op.getPath());
    assertEquals("B", op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/1/description", op.getPath());
    assertEquals("BBB", op.getValue());
    op = ops.get(2);
    assertEquals("test", op.getOp());
    assertEquals("/1/complete", op.getPath());
    assertEquals(false, op.getValue());
    op = ops.get(3);
    assertEquals("replace", op.getOp());
    assertEquals("/1/complete", op.getPath());
    assertEquals(true, op.getValue());
  }

  @Test
  void propertyChangeOnTwoItemsInList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.get(0).setDescription("AAA");
    modified.get(1).setComplete(true);

    Patch diff = Diff.diff(original, modified);
    assertEquals(4, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/0/description", op.getPath());
    assertEquals("A", op.getValue());
    op = ops.get(1);
    assertEquals("replace", op.getOp());
    assertEquals("/0/description", op.getPath());
    assertEquals("AAA", op.getValue());
    op = ops.get(2);
    assertEquals("test", op.getOp());
    assertEquals("/1/complete", op.getPath());
    assertEquals(false, op.getValue());
    op = ops.get(3);
    assertEquals("replace", op.getOp());
    assertEquals("/1/complete", op.getPath());
    assertEquals(true, op.getValue());
  }

  @Test
  void insertItemAtBeginningOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.add(0, new Todo(0L, "Z", false));
    Patch diff = Diff.diff(original, modified);
    assertEquals(1, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("add", op.getOp());
    assertEquals("/0", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(0L, value.getId().longValue());
    assertEquals("Z", value.getDescription());
    assertFalse(value.isComplete());
  }

  @Test
  void insertTwoItemsAtBeginningOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.add(0, new Todo(25L, "Y", false));
    modified.add(0, new Todo(26L, "Z", true));

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("add", op.getOp());
    assertEquals("/0", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(26L, value.getId().longValue());
    assertEquals("Z", value.getDescription());
    assertTrue(value.isComplete());
    op = ops.get(1);
    assertEquals("add", op.getOp());
    assertEquals("/1", op.getPath());
    value = (Todo) op.getValue();
    assertEquals(25L, value.getId().longValue());
    assertEquals("Y", value.getDescription());
    assertFalse(value.isComplete());
  }

  @Test
  void insertItemAtMiddleOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.add(2, new Todo(0L, "Z", false));
    Patch diff = Diff.diff(original, modified);
    assertEquals(1, diff.size());

    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("add", op.getOp());
    assertEquals("/2", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(0L, value.getId().longValue());
    assertEquals("Z", value.getDescription());
    assertFalse(value.isComplete());
  }

  @Test
  void insertTwoItemsAtMiddleOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.add(2, new Todo(25L, "Y", false));
    modified.add(2, new Todo(26L, "Z", true));

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("add", op.getOp());
    assertEquals("/2", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(26L, value.getId().longValue());
    assertEquals("Z", value.getDescription());
    assertTrue(value.isComplete());
    op = ops.get(1);
    assertEquals("add", op.getOp());
    assertEquals("/3", op.getPath());
    value = (Todo) op.getValue();
    assertEquals(25L, value.getId().longValue());
    assertEquals("Y", value.getDescription());
    assertFalse(value.isComplete());
  }

  @Test
  void insertItemAtEndOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.add(3, new Todo(0L, "Z", false));
    Patch diff = Diff.diff(original, modified);
    assertEquals(1, diff.size());

    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("add", op.getOp());
    assertEquals("/3", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(0L, value.getId().longValue());
    assertEquals("Z", value.getDescription());
    assertFalse(value.isComplete());
  }

  @Test
  void insertTwoItemsAtEndOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.add(3, new Todo(25L, "Y", false));
    modified.add(4, new Todo(26L, "Z", true));

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("add", op.getOp());
    assertEquals("/3", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(25L, value.getId().longValue());
    assertEquals("Y", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(1);
    assertEquals("add", op.getOp());
    assertEquals("/4", op.getPath());
    value = (Todo) op.getValue();
    assertEquals(26L, value.getId().longValue());
    assertEquals("Z", value.getDescription());
    assertTrue(value.isComplete());
  }

  @Test
  void insertItemsAtBeginningAndEndOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.add(0, new Todo(25L, "Y", false));
    modified.add(4, new Todo(26L, "Z", true));

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("add", op.getOp());
    assertEquals("/0", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(25L, value.getId().longValue());
    assertEquals("Y", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(1);
    assertEquals("add", op.getOp());
    assertEquals("/4", op.getPath());
    value = (Todo) op.getValue();
    assertEquals(26L, value.getId().longValue());
    assertEquals("Z", value.getDescription());
    assertTrue(value.isComplete());
  }

  @Test
  void removeItemFromBeginningOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.remove(0);

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());

    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/0", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(1L, value.getId().longValue());
    assertEquals("A", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(1);
    assertEquals("remove", op.getOp());
    assertEquals("/0", op.getPath());
  }

  @Test
  void removeItemFromMiddleOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.remove(1);

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/1", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(2L, value.getId().longValue());
    assertEquals("B", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(1);
    assertEquals("remove", op.getOp());
    assertEquals("/1", op.getPath());
  }

  @Test
  void removeItemFromEndOfList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.remove(2);

    Patch diff = Diff.diff(original, modified);
    assertEquals(2, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/2", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(3L, value.getId().longValue());
    assertEquals("C", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(1);
    assertEquals("remove", op.getOp());
    assertEquals("/2", op.getPath());
  }

  @Test
  void removeAllItemsFromList() {
    List<Todo> original = buildTodoList();
    List<Todo> modified = buildTodoList();
    modified.remove(0);
    modified.remove(0);
    modified.remove(0);

    Patch diff = Diff.diff(original, modified);
    assertEquals(6, diff.size());
    List<PatchOperation> ops = diff.getOperations();
    PatchOperation op = ops.get(0);
    assertEquals("test", op.getOp());
    assertEquals("/0", op.getPath());
    Todo value = (Todo) op.getValue();
    assertEquals(1L, value.getId().longValue());
    assertEquals("A", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(1);
    assertEquals("remove", op.getOp());
    assertEquals("/0", op.getPath());
    op = ops.get(2);
    assertEquals("test", op.getOp());
    assertEquals("/0", op.getPath());
    value = (Todo) op.getValue();
    assertEquals(2L, value.getId().longValue());
    assertEquals("B", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(3);
    assertEquals("remove", op.getOp());
    assertEquals("/0", op.getPath());
    op = ops.get(4);
    assertEquals("test", op.getOp());
    assertEquals("/0", op.getPath());
    value = (Todo) op.getValue();
    assertEquals(3L, value.getId().longValue());
    assertEquals("C", value.getDescription());
    assertFalse(value.isComplete());
    op = ops.get(5);
    assertEquals("remove", op.getOp());
    assertEquals("/0", op.getPath());
  }

  @Test
  void addEntryToListProperty() {
    ArrayList<Todo> todos = new ArrayList<>();
    todos.add(new Todo(1L, "A", false));
    todos.add(new Todo(2L, "B", false));
    todos.add(new Todo(3L, "C", false));
    TodoList before = new TodoList();
    before.setTodos(todos);

    todos = new ArrayList<>();
    todos.add(new Todo(1L, "A", false));
    todos.add(new Todo(2L, "B", false));
    todos.add(new Todo(3L, "C", false));
    todos.add(new Todo(4L, "D", false));
    TodoList after = new TodoList();
    after.setTodos(todos);

    Patch diff = Diff.diff(before, after);
    List<PatchOperation> operations = diff.getOperations();
    assertEquals(1, diff.size());
    assertEquals("add", operations.get(0).getOp());
    assertEquals("/todos/3", operations.get(0).getPath());
    assertEquals(new Todo(4L, "D", false), operations.get(0).getValue());
  }

  @Test
  void removeEntryFromListProperty() {
    ArrayList<Todo> todos = new ArrayList<>();
    todos.add(new Todo(1L, "A", false));
    todos.add(new Todo(2L, "B", false));
    todos.add(new Todo(3L, "C", false));
    TodoList before = new TodoList();
    before.setTodos(todos);

    todos = new ArrayList<>();
    todos.add(new Todo(1L, "A", false));
    todos.add(new Todo(3L, "C", false));
    TodoList after = new TodoList();
    after.setTodos(todos);

    Patch diff = Diff.diff(before, after);
    List<PatchOperation> operations = diff.getOperations();
    assertEquals(2, diff.size());
    assertEquals("test", operations.get(0).getOp());
    assertEquals("/todos/1", operations.get(0).getPath());
    assertEquals(new Todo(2L, "B", false), operations.get(0).getValue());
    assertEquals("remove", operations.get(1).getOp());
    assertEquals("/todos/1", operations.get(1).getPath());
  }

  @Test
  void editEntryInListProperty() {
    ArrayList<Todo> todos = new ArrayList<>();
    todos.add(new Todo(1L, "A", false));
    todos.add(new Todo(2L, "B", false));
    todos.add(new Todo(3L, "C", false));
    TodoList before = new TodoList();
    before.setTodos(todos);

    todos = new ArrayList<>();
    todos.add(new Todo(1L, "A", false));
    todos.add(new Todo(2L, "BBB", true));
    todos.add(new Todo(3L, "C", false));
    TodoList after = new TodoList();
    after.setTodos(todos);

    Patch diff = Diff.diff(before, after);
    List<PatchOperation> operations = diff.getOperations();
    assertEquals(4, diff.size());
    assertEquals("test", operations.get(0).getOp());
    assertEquals("/todos/1/description", operations.get(0).getPath());
    assertEquals("B", operations.get(0).getValue());
    assertEquals("replace", operations.get(1).getOp());
    assertEquals("/todos/1/description", operations.get(1).getPath());
    assertEquals("BBB", operations.get(1).getValue());
    assertEquals("test", operations.get(2).getOp());
    assertEquals("/todos/1/complete", operations.get(2).getPath());
    assertEquals(false, operations.get(2).getValue());
    assertEquals("replace", operations.get(3).getOp());
    assertEquals("/todos/1/complete", operations.get(3).getPath());
    assertEquals(true, operations.get(3).getValue());
  }

  @Test
  void addEntryToArrayProperty() {
    Todo[] todos = new Todo[3];
    todos[0] = new Todo(1L, "A", false);
    todos[1] = new Todo(2L, "B", false);
    todos[2] = new Todo(3L, "C", false);
    TodoList before = new TodoList();
    before.setTodoArray(todos);

    todos = new Todo[4];
    todos[0] = new Todo(1L, "A", false);
    todos[1] = new Todo(2L, "B", false);
    todos[2] = new Todo(3L, "C", false);
    todos[3] = new Todo(4L, "D", false);
    TodoList after = new TodoList();
    after.setTodoArray(todos);

    Patch diff = Diff.diff(before, after);
    List<PatchOperation> operations = diff.getOperations();
    assertEquals(1, diff.size());
    assertEquals("add", operations.get(0).getOp());
    assertEquals("/todoArray/3", operations.get(0).getPath());
    assertEquals(new Todo(4L, "D", false), operations.get(0).getValue());
  }

  @Test
  void removeEntryFromArrayProperty() {
    Todo[] todos = new Todo[3];
    todos[0] = new Todo(1L, "A", false);
    todos[1] = new Todo(2L, "B", false);
    todos[2] = new Todo(3L, "C", false);
    TodoList before = new TodoList();
    before.setTodoArray(todos);

    todos = new Todo[2];
    todos[0] = new Todo(1L, "A", false);
    todos[1] = new Todo(3L, "C", false);
    TodoList after = new TodoList();
    after.setTodoArray(todos);

    Patch diff = Diff.diff(before, after);
    List<PatchOperation> operations = diff.getOperations();
    assertEquals(2, diff.size());
    assertEquals("test", operations.get(0).getOp());
    assertEquals("/todoArray/1", operations.get(0).getPath());
    assertEquals(new Todo(2L, "B", false), operations.get(0).getValue());
    assertEquals("remove", operations.get(1).getOp());
    assertEquals("/todoArray/1", operations.get(1).getPath());
  }

  @Test
  void editEntryInArrayProperty() {
    Todo[] todos = new Todo[3];
    todos[0] = new Todo(1L, "A", false);
    todos[1] = new Todo(2L, "B", false);
    todos[2] = new Todo(3L, "C", false);
    TodoList before = new TodoList();
    before.setTodoArray(todos);

    todos = new Todo[3];
    todos[0] = new Todo(1L, "A", false);
    todos[1] = new Todo(2L, "BBB", true);
    todos[2] = new Todo(3L, "C", false);
    TodoList after = new TodoList();
    after.setTodoArray(todos);

    Patch diff = Diff.diff(before, after);
    List<PatchOperation> operations = diff.getOperations();
    assertEquals(4, diff.size());
    assertEquals("test", operations.get(0).getOp());
    assertEquals("/todoArray/1/description", operations.get(0).getPath());
    assertEquals("B", operations.get(0).getValue());
    assertEquals("replace", operations.get(1).getOp());
    assertEquals("/todoArray/1/description", operations.get(1).getPath());
    assertEquals("BBB", operations.get(1).getValue());
    assertEquals("test", operations.get(2).getOp());
    assertEquals("/todoArray/1/complete", operations.get(2).getPath());
    assertEquals(false, operations.get(2).getValue());
    assertEquals("replace", operations.get(3).getOp());
    assertEquals("/todoArray/1/complete", operations.get(3).getPath());
    assertEquals(true, operations.get(3).getValue());
  }

  private List<Todo> buildTodoList() {
    List<Todo> original = new ArrayList<>();
    original.add(new Todo(1L, "A", false));
    original.add(new Todo(2L, "B", false));
    original.add(new Todo(3L, "C", false));
    return original;
  }

}

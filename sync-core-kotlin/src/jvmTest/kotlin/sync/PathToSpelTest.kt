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
import org.springframework.expression.Expression

internal class PathToSpelTest {
    @Test
    fun listIndex() {
        val expr: Expression = PathToSpEL.pathToExpression("/1/description")
        val todos: MutableList<Todo> = ArrayList()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        Assertions.assertEquals("B", expr.getValue(todos) as String)
    }

    @Test
    fun listTilde() {
        val expr: Expression = PathToSpEL.pathToExpression("/~/description")
        val todos: MutableList<Todo> = ArrayList()
        todos.add(Todo(1L, "A", false))
        todos.add(Todo(2L, "B", false))
        todos.add(Todo(3L, "C", false))
        Assertions.assertEquals("C", expr.getValue(todos) as String)
    }
}
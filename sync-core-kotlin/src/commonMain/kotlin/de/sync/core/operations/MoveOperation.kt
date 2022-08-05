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
package de.sync.core.operations

import org.springframework.sync.PatchException
import kotlin.reflect.KClass

/**
 *
 *
 * Operation that moves a value from the given "from" path to the given "path".
 * Will throw a [PatchException] if either path is invalid or if the from path is non-nullable.
 *
 *
 *
 *
 * NOTE: When dealing with lists, the move operation may effectively be a no-op.
 * That's because the order of a list is probably dictated by a database query that produced the list.
 * Moving things around in the list will have no bearing on the values of each item in the list.
 * When the same list resource is retrieved again later, the order will again be decided by the query,
 * effectively undoing any previous move operation.
 *
 *
 * @author Craig Walls
 */
class MoveOperation
/**
 * Constructs the move operation.
 * @param path The path to move the source value to. (e.g., '/foo/bar/4')
 * @param from The source path from which a value will be moved. (e.g., '/foo/bar/5')
 */
    (path: String, from: String?) : FromOperation("move", path, from) {
    override fun <T : Any> perform(target: Any?, type: KClass<T>) {
        addValue(target, popValueAtPath(target, from!!))
    }
}
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

import kotlin.reflect.KClass

/**
 * Operation that replaces the value at the given path with a new value.
 *
 * @author Craig Walls
 */
class ReplaceOperation
/**
 * Constructs the replace operation
 * @param path The path whose value is to be replaced. (e.g., '/foo/bar/4')
 * @param value The value that will replace the current path value.
 */
    (path: String, value: Any?) : PatchOperation("replace", path, value) {
    override fun <T : Any> perform(target: Any?, type: KClass<T>) {
        setValueOnTarget(target, evaluateValueFromTarget(target, type))
    }
}
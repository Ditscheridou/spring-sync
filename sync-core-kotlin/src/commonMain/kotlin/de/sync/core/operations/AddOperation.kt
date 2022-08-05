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
 * Operation to add a new value to the given "path".
 * Will throw a [PatchException] if the path is invalid or if the given value
 * is not assignable to the given path.
 *
 * @author Craig Walls
 */
class AddOperation(path: String, value: Any?) : PatchOperation("add", path, value) {
    override fun <T : Any> perform(targetObject: Any?, type: KClass<T>) {
        addValue(targetObject, evaluateValueFromTarget(targetObject, type))
    }
}
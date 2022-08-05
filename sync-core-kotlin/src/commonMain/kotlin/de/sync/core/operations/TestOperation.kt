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

import de.sync.core.PatchException
import org.springframework.util.ObjectUtils
import kotlin.reflect.KClass

/**
 *
 * Operation to test values on a given target.
 *
 *
 *
 * If the value given matches the value given at the path, the operation completes as a no-op.
 * On the other hand, if the values do not match or if there are any errors interpreting the path,
 * a [PatchException] will be thrown.
 *
 *
 * @author Craig Walls
 */
class TestOperation
/**
 * Constructs the test operation
 * @param path The path to test. (e.g., '/foo/bar/4')
 * @param value The value to test the path against.
 */
    (path: String, value: Any?) : PatchOperation("test", path, value) {
    override fun <T : Any> perform(target: Any?, type: KClass<T>) {
        val expected = normalizeIfNumber(evaluateValueFromTarget(target, type))
        val actual = normalizeIfNumber(getValueFromTarget(target))
        if (!ObjectUtils.nullSafeEquals(expected, actual)) {
            throw PatchException("Test against path '$path' failed.")
        }
    }

    private fun normalizeIfNumber(expected: Any): Any {
        var expected = expected
        if (expected is Double || expected is Float) {
            expected = (expected as Number).toDouble()
        } else if (expected is Number) {
            expected = expected.toLong()
        }
        return expected
    }
}
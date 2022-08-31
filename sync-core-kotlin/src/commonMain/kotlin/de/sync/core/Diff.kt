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
package de.sync.core
/**
 * Provides support for producing a [Patch] from the comparison of two objects.
 *
 * @author Craig Walls
 */
expect object Diff {
    /**
     * Performs a difference operation between two objects, resulting in a [Patch] describing the differences.
     *
     * @param original the original, unmodified object.
     * @param modified the modified object.
     * @return a [Patch] describing the differences between the two objects.
     * @throws PatchException if an error occurs while performing the difference.
     */
    fun diff(original: Any?, modified: Any?): Patch

}

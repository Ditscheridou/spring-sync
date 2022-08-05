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

import org.springframework.expression.Expression
import org.springframework.expression.spel.standard.SpelExpressionParser
import kotlin.math.min
import kotlin.reflect.KClass

/**
 * Utilities for converting patch paths to/from SpEL expressions.
 *
 *
 * For example, "/foo/bars/1/baz" becomes "foo.bars[1].baz".
 *
 * @author Craig Walls
 */
object PathToSpEL {
    private val SPEL_EXPRESSION_PARSER: SpelExpressionParser = SpelExpressionParser()

    /**
     * Converts a patch path to an [Expression].
     *
     * @param path the patch path to convert.
     * @return an [Expression]
     */
    fun pathToExpression(path: String): Expression {
        return SPEL_EXPRESSION_PARSER.parseExpression(pathToSpEL(path))
    }

    /**
     * Convenience method to convert a SpEL String to an [Expression].
     *
     * @param spel the SpEL expression as a String
     * @return an [Expression]
     */
    fun spelToExpression(spel: String?): Expression {
        return SPEL_EXPRESSION_PARSER.parseExpression(spel)
    }

    /**
     * Produces an expression targeting the parent of the object that the given path targets.
     *
     * @param path the path to find a parent expression for.
     * @return an [Expression] targeting the parent of the object specifed by path.
     */
    fun pathToParentExpression(path: String): Expression {
        return spelToExpression(pathNodesToSpEL(copyOf(path.split("/".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray(), path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size - 1)))
    }

    // private helpers
    private fun pathToSpEL(path: String): String {
        return pathNodesToSpEL(path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    private fun pathNodesToSpEL(pathNodes: Array<String>): String {
        val spelBuilder = StringBuilder()
        for (pathNode in pathNodes) {
            if (pathNode.isEmpty()) {
                continue
            }
            if ("~" == pathNode) {
                spelBuilder.append("[size() - 1]")
                continue
            }
            try {
                val index = pathNode.toInt()
                spelBuilder.append('[').append(index).append(']')
            } catch (e: NumberFormatException) {
                if (spelBuilder.isNotEmpty()) {
                    spelBuilder.append('.')
                }
                spelBuilder.append(pathNode)
            }
        }
        var spel: String = spelBuilder.toString()
        if (spel.isEmpty()) {
            spel = "#this"
        }
        return spel
    }

    private fun <T> copyOf(original: Array<T>, newLength: Int): Array<T> {
        return copyOf<Any, T>(original, newLength, original.javaClass) as Array<T>
    }

    // reproduces Arrays.copyOf because that API is missing on Android 2.2
    private fun <T, U> copyOf(
        original: Array<U>, newLength: Int,
        newType: KClass<out Array<T>>
    ): Array<T?> {
        val copy: Array<T?> =
            if (newType == Array<Any>::class.java) arrayOfNulls<Any>(newLength) as Array<T?> else java.lang.reflect.Array.newInstance(
                newType.getComponentType(),
                newLength
            )
        java.lang.System.arraycopy(original, 0, copy, 0, min(original.size, newLength))
        return copy
    }
}
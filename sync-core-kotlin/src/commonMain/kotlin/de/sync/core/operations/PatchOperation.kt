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

import de.sync.core.LateObjectEvaluator
import de.sync.core.PatchException
import kotlinx.serialization.Serializable
import org.springframework.expression.Expression
import org.springframework.expression.ExpressionException
import org.springframework.expression.spel.SpelEvaluationException
import org.springframework.sync.PatchException
import org.springframework.sync.PathToSpEL.*
import kotlin.reflect.KClass

/**
 * Abstract base class representing and providing support methods for patch operations.
 *
 * @author Craig Walls
 */
abstract class PatchOperation protected constructor(
    /**
     * @return the operation name
     */
    val op: String?,
    /**
     * @return the operation path
     */
    val path: String,
    /**
     * @return the operation's value (or [LateObjectEvaluator])
     */
    val value: Any? = null
) {
    protected val spelExpression: Expression
    /**
     * Constructs the operation.
     *
     * @param op    the operation name. (e.g., 'move')
     * @param path  the path to perform the operation on. (e.g., '/1/description')
     * @param value the value to apply in the operation. Could be an actual value or an implementation of [LateObjectEvaluator].
     */
    /**
     * Constructs the operation.
     *
     * @param op   the operation name. (e.g., 'move')
     * @param path the path to perform the operation on. (e.g., '/1/description')
     */
    init {
        spelExpression = pathToExpression(path)
    }

    /**
     * Pops a value from the given path.
     *
     * @param target     the target from which to pop a value.
     * @param removePath the path from which to pop a value. Must be a list.
     * @return the value popped from the list
     */
    protected fun popValueAtPath(target: Any?, removePath: String): Any {
        val listIndex = targetListIndex(removePath)
        val expression: Expression = pathToExpression(removePath)
        val value: Any = expression.getValue(target)
        return if (listIndex == null) {
            try {
                expression.setValue(target, null)
                value
            } catch (e: SpelEvaluationException) {
                throw PatchException("Path '$removePath' is not nullable.")
            }
        } else {
            val parentExpression: Expression = pathToParentExpression(removePath)
            val list = parentExpression.getValue(target) as List<*>
                ?: throw PatchException(String.format("parent expression for target %s was not present", target))
            list.removeAt(if (listIndex >= 0) listIndex else list.size - 1)
            value
        }
    }

    /**
     * Adds a value to the operation's path.
     * If the path references a list index, the value is added to the list at the given index.
     * If the path references an object property, the property is set to the value.
     *
     * @param target The target object.
     * @param value  The value to add.
     */
    protected fun addValue(target: Any?, value: Any) {
        val parentExpression: Expression = pathToParentExpression(path)
        val parent: Any = parentExpression.getValue(target)
        val listIndex = targetListIndex(path)
        if (parent !is List<*> || listIndex == null) {
            spelExpression.setValue(target, value)
        } else {
            val list = parentExpression.getValue(target) as List<Any>
            val addAtIndex: Int = if (listIndex >= 0) listIndex else list.size
            list.add(addAtIndex, value)
        }
    }

    /**
     * Sets a value to the operation's path.
     *
     * @param target The target object.
     * @param value  The value to set.
     */
    protected fun setValueOnTarget(target: Any?, value: Any?) {
        spelExpression.setValue(target, value)
    }

    /**
     * Retrieves a value from the operation's path.
     *
     * @param target the target object.
     * @return the value at the path on the given target object.
     */
    protected fun getValueFromTarget(target: Any?): Any {
        return try {
            spelExpression.getValue(target)
        } catch (e: ExpressionException) {
            throw PatchException("Unable to get value from target", e)
        }
    }

    /**
     * Performs late-value evaluation on the operation value if the value is a [LateObjectEvaluator].
     *
     * @param targetObject the target object, used as assistance in determining the evaluated object's type.
     * @param entityType   the entityType
     * @param <T>          the entity type
     * @return the result of late-value evaluation if the value is a [LateObjectEvaluator]; the value itself otherwise.
    </T> */
    protected fun evaluateValueFromTarget(
        targetObject: Any?,
        entityType: KClass<Serializable>
    ): Any {
        return (if (value is LateObjectEvaluator) (value as LateObjectEvaluator?)?.evaluate(entityType) else value)!!
    }

    /**
     * Perform the operation.
     *
     * @param target the target of the operation.
     */
    abstract fun <T : Any> perform(target: Any?, type: KClass<T>)

    // private helpers
    private fun targetListIndex(path: String): Int? {
        val pathNodes = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val lastNode = pathNodes[pathNodes.size - 1]
        return if ("~" == lastNode) {
            -1
        } else try {
            lastNode.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }
}
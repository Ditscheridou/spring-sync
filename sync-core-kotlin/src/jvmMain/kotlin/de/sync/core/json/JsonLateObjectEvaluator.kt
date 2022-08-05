package de.sync.core.json

import de.sync.core.LateObjectEvaluator
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.internal.readJson

/**
 * [LateObjectEvaluator] implementation that assumes values represented as JSON objects.
 *
 * @author Craig Walls
 */
internal actual class JsonLateObjectEvaluator(private val valueNode: JsonObject) : LateObjectEvaluator {


    override fun <T : Any> evaluate(type: Serializable): Any? {
        return try {
            Json.readJson(valueNode, type)
        } catch (e: Exception) {
            null
        }
    }
}
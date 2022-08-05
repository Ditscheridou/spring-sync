package de.sync.core.json

import com.fasterxml.jackson.databind.JsonNode
import de.sync.core.Patch
import de.sync.core.PatchException
import de.sync.core.operations.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import kotlinx.serialization.json.internal.writeJson
import valueOrNull

/**
 * Convert [JsonNode]s containing JSON Patch to/from [Patch] objects.
 *
 * @author Craig Walls
 */
actual class JsonPatchPatchConverter : PatchConverter<JsonElement> {

    /**
     * Constructs a [Patch] object given a JsonNode.
     *
     * @param patchRepresentation a JsonNode containing the JSON Patch
     * @return a [Patch]
     */
    override fun convert(patchRepresentation: JsonElement): Patch {
        if (patchRepresentation !is JsonArray) {
            throw java.lang.IllegalArgumentException("JsonNode must be an instance of ArrayNode")
        }
        val opNodes: JsonArray = patchRepresentation
        val ops: MutableList<PatchOperation> = mutableListOf()
        val elements: Iterator<JsonElement> = opNodes.iterator()
        while (elements.hasNext()) {
            val opNode = elements.next().jsonObject
            val opType: String = opNode["op"]?.jsonPrimitive?.content ?: throw PatchException("No op property for node")
            val path: String =
                opNode["path"]?.jsonPrimitive?.content ?: throw PatchException("No path property for node")
            val value = valueFromJsonNode(opNode["value"]?.jsonPrimitive)
            val from: String? = if (opNode.containsKey("from")) opNode["from"]?.jsonPrimitive.contentOrNull else null
            when (opType) {
                "test" -> ops.add(TestOperation(path, value))
                "replace" -> ops.add(ReplaceOperation(path, value))
                "remove" -> ops.add(RemoveOperation(path))
                "add" -> ops.add(AddOperation(path, value))
                "copy" -> ops.add(CopyOperation(path, from))
                "move" -> ops.add(MoveOperation(path, from))
                else -> throw PatchException("Unrecognized operation type: $opType")
            }
        }
        return Patch(ops)
    }

    /**
     * Renders a [Patch] as a [JsonNode].
     *
     * @param patch the patch
     * @return a [JsonNode] containing JSON Patch.
     */
    override fun convert(patch: Patch): JsonElement {
        val operations: List<PatchOperation> = patch.getOperations()
        return buildJsonArray {
            for (operation in operations) {
                add(buildJsonObject {
                    this.put("op", operation.op)
                    this.put("path", operation.path)
                    if (operation is FromOperation) {
                        val fromOp: FromOperation = operation
                        this.put("from", fromOp.from)
                    }
                    val value = operation.value
                    this.put("value", Json.writeJson(value, String.serializer()))
                })
            }
        }
    }

    private fun valueFromJsonNode(valueNode: JsonElement?): Any? {
        return when (valueNode) {
            is JsonPrimitive -> {
                valueNode.valueOrNull
            }
            is JsonArray -> {
                TODO("Not yet implemented")
            }
            is JsonObject -> {
                JsonLateObjectEvaluator(valueNode)
            }
            null -> {
                null
            }

        }
    }
}
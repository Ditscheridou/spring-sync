import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int

private val INT_REGEX = Regex("""^-?\d+$""")
private val DOUBLE_REGEX = Regex("""^-?\d+\.\d+(?:E-?\d+)?$""")

val JsonPrimitive.valueOrNull: Any?
    get() = when {
        this is JsonNull -> null
        this.isString -> this.content
        else -> this.content.toBooleanStrictOrNull()
            ?: when {
                INT_REGEX.matches(this.content) -> this.int
                DOUBLE_REGEX.matches(this.content) -> this.double
                else -> throw IllegalArgumentException("Unknown type for JSON value: ${this.content}")
            }
    }
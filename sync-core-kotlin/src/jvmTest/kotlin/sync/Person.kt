package sync

import lombok.AllArgsConstructor
import java.io.Serializable

@Data
@NoArgsConstructor
@AllArgsConstructor
class Person : Serializable {
    private val firstName: String? = null
    private val lastName: String? = null

    companion object {
        private const val serialVersionUID = 1L
    }
}
package cash.andrew.lightalarm.data.serializers

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test
import java.util.UUID

class UuidSerializerTest {

    @Serializable
    data class Temp(
        @Serializable(with = UuidSerializer::class)
        val uuid: UUID
    )

    @Test
    fun `should serialize UUID and then de-serialize`() {
        val expected = Temp(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
        val stringResult = Json.encodeToString(expected)

        val result = Json.decodeFromString<Temp>(stringResult)

        assertThat(result).isEqualTo(expected)
    }
}
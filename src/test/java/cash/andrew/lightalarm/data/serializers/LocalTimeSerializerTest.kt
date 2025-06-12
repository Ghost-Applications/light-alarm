package cash.andrew.lightalarm.data.serializers

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test
import java.time.LocalTime

class LocalTimeSerializerTest {

    @Serializable
    data class Temp(
        @Serializable(with = LocalTimeSerializer::class)
        val localTime: LocalTime
    )

    @Test
    fun `should serialize LocalTime and then de-serialize`() {
        val expected = Temp(LocalTime.of(1, 1))

        val stringResult = Json.encodeToString(expected)
        val result = Json.decodeFromString<Temp>(stringResult)

        assertThat(result).isEqualTo(expected)
    }
}
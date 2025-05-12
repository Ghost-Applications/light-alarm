package cash.andrew.lightalarm.data.serializers

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Test
import java.util.EnumSet

class EnumSetSerializerTest {

    enum class MyEnum {
        ENABLED,
        DISABLED
    }

    @Serializable
    data class Temp(
        @Serializable(with = EnumSetSerializer::class)
        val localTime: EnumSet<MyEnum>
    )

    @Test
    fun `should serialize EnumSet and then de-serialize`() {
        val expected = Temp(EnumSet.allOf(MyEnum::class.java))

        val stringResult = Json.encodeToString(expected)
        val result = Json.decodeFromString<Temp>(stringResult)

        assertThat(result).isEqualTo(expected)
    }
}
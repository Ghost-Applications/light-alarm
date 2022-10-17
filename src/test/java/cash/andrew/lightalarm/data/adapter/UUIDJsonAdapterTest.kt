package cash.andrew.lightalarm.data.adapter

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import org.junit.Test
import java.util.*

class UUIDJsonAdapterTest {

    private val uuid = UUID.fromString("0481b9f8-4ea2-4551-b952-432e3ab61caa")

    private val adapter = Moshi.Builder()
        .add(UUIDJsonAdapter())
        .build()
        .adapter(UUID::class.java)

    @Test fun `should convert uuid to json`() {
        val json = adapter.toJson(uuid)
        assertThat(json).isEqualTo("\"$uuid\"")
    }

    @Test fun `should convert json to uuid`() {
        val jsonString = "\"$uuid\""
        val result = adapter.fromJson(jsonString)
        assertThat(result).isEqualTo(uuid)
    }
}

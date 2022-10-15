package cash.andrew.lightalarm.data.adapter

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.junit.Test
import java.time.DayOfWeek
import java.util.EnumSet

class DayOfWeekEnumSetJsonAdapterTest {

    private val adapter = Moshi.Builder()
        .add(DayOfWeekEnumSetJsonAdapter)
        .build()
        .adapter<EnumSet<DayOfWeek>>(Types.newParameterizedType(EnumSet::class.java, DayOfWeek::class.java))

    @Test fun `should create json from enum set`() {
        val json = adapter.toJson(EnumSet.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY))
        assertThat(json).isEqualTo("[\"FRIDAY\",\"SATURDAY\"]")
    }

    @Test fun `should create enum set from json`() {
        val jsonString = "[\"FRIDAY\",\"SATURDAY\"]"
        val result = adapter.fromJson(jsonString)
        assertThat(result).isEqualTo(EnumSet.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY))
    }
}

package cash.andrew.lightalarm.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.time.DayOfWeek
import java.util.EnumSet

object DayOfWeekEnumSetJsonAdapter {
    @ToJson
    fun toJson(jsonWriter: JsonWriter, value: EnumSet<DayOfWeek>?, jsonAdapter: JsonAdapter<Set<DayOfWeek>>) {
        jsonAdapter.toJson(jsonWriter, value as Set<DayOfWeek>)
    }

    @FromJson
    fun fromJson(jsonReader: JsonReader, jsonAdapter: JsonAdapter<Set<DayOfWeek>>): EnumSet<DayOfWeek> {
        val fromJson: Set<DayOfWeek>? = jsonAdapter.fromJson(jsonReader)
        return EnumSet.copyOf(checkNotNull(fromJson))
    }
}

package cash.andrew.lightalarm.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalTime

object LocalTimeJsonAdapter {
    @ToJson fun toJson(value: LocalTime): String {
        return value.toString()
    }

    @FromJson fun fromJson(json: String): LocalTime {
        return LocalTime.parse(json)
    }
}

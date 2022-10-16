package cash.andrew.lightalarm.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.UUID

object UUIDJsonAdapter {
    @ToJson
    fun toJson(uuid: UUID): String = uuid.toString()

    @FromJson
    fun fromJson(uuid: String): UUID = UUID.fromString(uuid)
}

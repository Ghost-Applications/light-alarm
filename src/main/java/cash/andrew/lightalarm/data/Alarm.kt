package cash.andrew.lightalarm.data

import cash.andrew.lightalarm.data.serializers.EnumSetSerializer
import cash.andrew.lightalarm.data.serializers.LocalTimeSerializer
import cash.andrew.lightalarm.data.serializers.UuidSerializer
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.EnumSet
import java.util.UUID

@Serializable
data class Alarm(
    @Serializable(with = UuidSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val enabled: Boolean = true,
    val strobe: Boolean = false,
    val repeat: Boolean = false,
    @Serializable(with = EnumSetSerializer::class)
    val days: EnumSet<DayOfWeek> = EnumSet.noneOf(DayOfWeek::class.java),
    @Serializable(with = LocalTimeSerializer::class)
    val time: LocalTime
)

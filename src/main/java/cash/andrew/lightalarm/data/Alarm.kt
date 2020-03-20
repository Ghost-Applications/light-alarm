package cash.andrew.lightalarm.data

import java.time.DayOfWeek
import java.time.LocalTime
import java.util.*

data class Alarm(
    val id: UUID = UUID.randomUUID(),
    val enabled: Boolean = true,
    val strobe: Boolean = false,
    val repeat: Boolean = false,
    val days: EnumSet<DayOfWeek> = EnumSet.noneOf(DayOfWeek::class.java),
    val time: LocalTime
)

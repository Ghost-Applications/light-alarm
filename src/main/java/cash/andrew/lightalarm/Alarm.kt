package cash.andrew.lightalarm

import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

data class Alarm(
    val id: UUID = UUID.randomUUID(),
    val enabled: Boolean = true,
    val strobe: Boolean = false,
    val repeat: Boolean = false,
    val days: Set<DayOfWeek> = emptySet(),
    val time: LocalTime
)

package cash.andrew.lightalarm.misc

import android.content.Intent
import java.util.*

private const val ALARM_ID_KEY = "cash.andrew.lightalarm.reciever.ALARM_ID"

fun Intent.putAlarmIdExtra(id: UUID) {
    putExtra(ALARM_ID_KEY, id)
}

val Intent.alarmIdExtra: UUID get() = getSerializableExtra(ALARM_ID_KEY) as UUID

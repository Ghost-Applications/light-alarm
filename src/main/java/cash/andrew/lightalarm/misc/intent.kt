package cash.andrew.lightalarm.misc

import android.content.Intent
import java.util.*

private const val ALARM_ID_KEY = "cash.andrew.lightalarm.reciever.ALARM_ID"

fun Intent.putAlarmKeyExtra(id: UUID) {
    putExtra(ALARM_ID_KEY, id)
}

fun Intent.getAlarmKeyExtra(): UUID = getSerializableExtra(ALARM_ID_KEY) as UUID

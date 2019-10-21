package cash.andrew.lightalarm

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val ALARM_EDIT_INTENT_ID = 1337
private const val ALARM_BROADCAST_RECIEVER_ID = 1338

@Singleton
class AlarmScheduler @Inject constructor(
    private val application: Application,
    private val alarmManager: AlarmManager
) {

    fun schedule(alarm: Alarm) = with(alarm) {
        if (repeat) {

        }

        var alarmTime = ZonedDateTime.now()
            .withSecond(0)
            .withHour(time.hour)
            .withMinute(time.minute)

        if (alarmTime.isBefore(ZonedDateTime.now())) {
            alarmTime = alarmTime.plusDays(1)
        }

        val showIntent = PendingIntent.getActivity(
            application,
            ALARM_EDIT_INTENT_ID,
            Intent(application, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val operation = PendingIntent.getBroadcast(
            application,
            ALARM_BROADCAST_RECIEVER_ID,
            Intent(application, AlarmReceiver::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        Timber.d("Setting alarm to $alarmTime")
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(alarmTime.toInstant().toEpochMilli(), showIntent),
            operation
        )
    }
}

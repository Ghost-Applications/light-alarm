package cash.andrew.lightalarm.data

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import cash.andrew.lightalarm.misc.putAlarmIdExtra
import cash.andrew.lightalarm.receiver.AlarmReceiver
import cash.andrew.lightalarm.ui.MainActivity
import org.jetbrains.annotations.TestOnly
import timber.log.Timber
import java.time.Clock
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val ALARM_EDIT_INTENT_ID = 5682
private const val ALARM_BROADCAST_RECEIVER_ID = 4371

@Singleton
class AlarmScheduler @Inject constructor(
    private val application: Application,
    private val alarmManager: LightAlarmAlarmManager,
    private val alarmKeeper: AlarmKeeper,
    private val clock: Clock
) {
    suspend fun scheduleNextAlarm() {
        cancelAlarms()

        val (nextAlarm, alarmId) = nextAlarmTime() ?: return
        schedule(nextAlarm, alarmId)
    }

    @TestOnly
    suspend fun nextAlarmTime(): Pair<ZonedDateTime, UUID>? = alarmKeeper.alarms()
        .asSequence()
        .filter { it.enabled }
        .map {
            val hour = it.time.hour
            val minute = it.time.minute
            val alarmId = it.id

            if (it.repeat) {
                it.days.map { dayOfWeek ->
                    val alarmTime = now.withSecond(0)
                        .withHour(hour)
                        .withMinute(minute)
                        .with(TemporalAdjusters.nextOrSame(dayOfWeek))

                    alarmTime to alarmId
                }.filter { (time) -> time.isAfter(now) }
            } else {
                var alarmTime = now
                    .withSecond(0)
                    .withHour(hour)
                    .withMinute(minute)

                if (alarmTime.isBefore(now)) {
                    alarmTime = alarmTime.plusDays(1)
                }

                listOf(alarmTime to alarmId)
            }
        }
        .flatten()
        .sortedBy { (time) -> time }
        .firstOrNull()

    fun schedule(time: ZonedDateTime, alarmId: UUID) {
        val showIntent = PendingIntent.getActivity(
            application,
            ALARM_EDIT_INTENT_ID,
            Intent(application, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val operation = PendingIntent.getBroadcast(
            application,
            ALARM_BROADCAST_RECEIVER_ID,
            Intent(application, AlarmReceiver::class.java).also { it.putAlarmIdExtra(alarmId) },
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleAlarm(time, showIntent, operation)
            } else {
                Timber.w("Application does not have permission to schedule alarms.")
                Toast.makeText(application, "Unable to schedule alarm, the Alarms & reminders permission is required.", Toast.LENGTH_LONG).show()
            }
        } else {
            scheduleAlarm(time, showIntent, operation)
        }
    }

    private fun scheduleAlarm(
        time: ZonedDateTime,
        showIntent: PendingIntent?,
        operation: PendingIntent
    ) {
        Timber.d("Scheduling alarm at %s", time)
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(time.toInstant().toEpochMilli(), showIntent),
            operation
        )
    }

    private fun cancelAlarms() {
        Timber.d("Canceling alarm")

        val alarmIntent = PendingIntent.getBroadcast(
            application,
            ALARM_BROADCAST_RECEIVER_ID,
            Intent(application, AlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (alarmIntent == null) {
            Timber.d("Alarm intent is null no intents have been created")
            return
        }

        alarmManager.cancel(alarmIntent)
    }

    private val now: ZonedDateTime get() = ZonedDateTime.now(clock)
}

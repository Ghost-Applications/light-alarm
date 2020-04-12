package cash.andrew.lightalarm.data

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @param timeFormat should be `DateFormat.getTimeFormat` extracted because always invert your dependencies (mostly for testing here)
 */
@Singleton
class AlarmDateTimeFormatter @Inject constructor(
    private val timeFormat: DateFormat
) {
    /**
     * Format the alarm time as a string in HH:mm zz, should automatically handle 24 hours
     * and am/pm
     */
    fun formatAlarmTime(alarm: Alarm): String = timeFormat.format(
        Date.from(
            ZonedDateTime.now().withHour(alarm.time.hour).withMinute(alarm.time.minute).toInstant()
        )
    )

    /**
     * Format the alarm's time in EEE HH:mm automatically handling am/pm
     * ex Tue 6:30pm.
     * This is used for the current notification going off.
     */
    fun formatCurrentDayPluAlarmTime(alarm: Alarm): String = "${SimpleDateFormat("EEE", Locale.getDefault()).format(Date())}, ${formatAlarmTime(alarm)}"
}
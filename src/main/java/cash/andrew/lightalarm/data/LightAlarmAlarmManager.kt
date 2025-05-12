package cash.andrew.lightalarm.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Used to wrap the Android alarm manager for testing purposes
 */
interface LightAlarmAlarmManager {
    fun canScheduleExactAlarms(): Boolean
    fun setAlarmClock(info: AlarmManager.AlarmClockInfo, operation: PendingIntent)
    fun cancel(operation: PendingIntent)
}

@Singleton
class AndroidAlarmManagerWrapper @Inject constructor(
    private val alarmManager: AlarmManager
): LightAlarmAlarmManager {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun canScheduleExactAlarms(): Boolean {
        return alarmManager.canScheduleExactAlarms()
    }

    override fun setAlarmClock(info: AlarmManager.AlarmClockInfo, operation: PendingIntent) {
        alarmManager.setAlarmClock(info, operation)
    }

    override fun cancel(operation: PendingIntent) {
        alarmManager.cancel(operation)
    }
}

package cash.andrew.lightalarm

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.data.AlarmDateTimeFormatter
import cash.andrew.lightalarm.misc.putAlarmIdExtra
import cash.andrew.lightalarm.ui.AlarmActivity
import javax.inject.Inject
import javax.inject.Singleton

private const val ALARM_CHANNEL_ID = "Alarm"
private const val ALARM_NOTIFICATION_PENDING_INTENT_ID = 7589

@Singleton
class NotificationManager @Inject constructor(
    private val context: Application,
    private val notificationManager: NotificationManager,
    private val alarmDateTimeFormatter: AlarmDateTimeFormatter
) {
    companion object {
        const val ALARM_NOTIFICATION_ID = 3779
    }

    fun showAlarmNotification(alarm: Alarm) {
        val notification = alarmNotification(alarm)
        notificationManager.notify(ALARM_NOTIFICATION_ID, notification)
    }

    fun alarmNotification(alarm: Alarm): Notification {
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            putAlarmIdExtra(alarm.id)
        }

        val operation = PendingIntent.getActivity(
            context,
            ALARM_NOTIFICATION_PENDING_INTENT_ID,
            activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        return Notification.Builder(context, ALARM_CHANNEL_ID)
            .setFullScreenIntent(operation, true)
            .setLargeIcon(Icon.createWithResource(context, R.drawable.ic_alarm_black_24dp))
            .setSmallIcon(Icon.createWithResource(context, R.drawable.ic_alarm_black_24dp))
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(alarmDateTimeFormatter.formatCurrentDayPluAlarmTime(alarm))
            .build()
    }
}
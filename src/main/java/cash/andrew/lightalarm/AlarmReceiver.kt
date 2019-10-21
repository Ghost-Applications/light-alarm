package cash.andrew.lightalarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import timber.log.Timber

const val NOTIFICATION_ID = 3779
const val PENDING_INTENT_ID = 1337

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceived called context=$context, intent=$intent")

        val activityIntent = Intent(context, AlarmActivity::class.java)
        val operation = PendingIntent.getActivity(
            context,
            PENDING_INTENT_ID, activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val lightController = LightController(requireNotNull(context.getSystemService()))
        lightController.turnOn()

        val notification = Notification.Builder(context, ALARM_CHANNEL_ID)
            .setFullScreenIntent(operation, true)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Light Alarm!")
            .build()

        context.getSystemService<NotificationManager>()!!.notify(NOTIFICATION_ID, notification)
    }
}

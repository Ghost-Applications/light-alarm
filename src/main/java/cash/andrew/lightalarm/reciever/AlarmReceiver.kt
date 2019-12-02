package cash.andrew.lightalarm.reciever

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import cash.andrew.lightalarm.ALARM_CHANNEL_ID
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.data.LightController
import cash.andrew.lightalarm.ui.AlarmActivity
import cash.andrew.lightalarm.ui.activityComponent
import timber.log.Timber
import javax.inject.Inject

const val NOTIFICATION_ID = 3779
const val PENDING_INTENT_ID = 7589

class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var lightController: LightController
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceived called context=$context, intent=$intent")

        context.alarmAppComponent.inject(this)

        alarmScheduler.scheduleNextAlarm()
        lightController.turnOn()

        val activityIntent = Intent(context, AlarmActivity::class.java)
        val operation = PendingIntent.getActivity(
            context,
            PENDING_INTENT_ID, activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification = Notification.Builder(context, ALARM_CHANNEL_ID)
            .setFullScreenIntent(operation, true)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Light Alarm!")
            .build()

        context.getSystemService<NotificationManager>()!!.notify(NOTIFICATION_ID, notification)
    }
}

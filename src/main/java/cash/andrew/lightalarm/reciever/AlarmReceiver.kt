package cash.andrew.lightalarm.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.data.*
import cash.andrew.lightalarm.misc.alarmIdExtra
import cash.andrew.lightalarm.service.startLightService
import cash.andrew.lightalarm.service.startStrobeService
import timber.log.Timber
import javax.inject.Inject

const val NOTIFICATION_ID = 3779
const val PENDING_INTENT_ID = 7589

class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var lightController: LightController
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceived called context=%s, intent=%s", context, intent)

        context.alarmAppComponent.inject(this)

        val id = intent.alarmIdExtra
        val alarm = alarmKeeper.getAlarmById(id)!!

        alarmScheduler.scheduleNextAlarm()

        if (alarm.strobe) {
            context.startStrobeService(id)
            return
        }

        context.startLightService(id)

//        val activityIntent = Intent(context, AlarmActivity::class.java)
//        val operation = PendingIntent.getActivity(
//            context,
//            PENDING_INTENT_ID,
//                activityIntent,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//
//        val notification = Notification.Builder(context, ALARM_CHANNEL_ID)
//            .setFullScreenIntent(operation, true)
//            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
//            .setContentTitle("Light Alarm!") // todo
//            .build()
//
//        context.getSystemService<NotificationManager>()!!
//            .notify(NOTIFICATION_ID, notification)
    }
}

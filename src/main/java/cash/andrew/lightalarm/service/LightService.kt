package cash.andrew.lightalarm.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import cash.andrew.lightalarm.ALARM_CHANNEL_ID
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.data.LightController
import cash.andrew.lightalarm.misc.alarmIdExtra
import cash.andrew.lightalarm.misc.putAlarmIdExtra
import cash.andrew.lightalarm.reciever.NOTIFICATION_ID
import cash.andrew.lightalarm.reciever.PENDING_INTENT_ID
import cash.andrew.lightalarm.ui.AlarmActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class LightService : Service(), CoroutineScope by MainScope() {
    private val Intent.lightServiceAction get() = LightServiceAction.values().first { action == it.name }

    enum class LightServiceAction {
        START,
        STOP
    }

    @Inject lateinit var lightController: LightController

    private var job: Job? = null

    final override fun onCreate() {
        alarmAppComponent.inject(this)
    }

    final override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.lightServiceAction == LightServiceAction.STOP) {
            job?.let {
                it.cancel()
                job = null
                stopSelf()
            }
            return START_REDELIVER_INTENT
        }

        val activityIntent = Intent(this, AlarmActivity::class.java).apply {
            putAlarmIdExtra(intent.alarmIdExtra)
        }

        val operation = PendingIntent.getActivity(
            this,
            PENDING_INTENT_ID,
            activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification = Notification.Builder(this, ALARM_CHANNEL_ID)
            .setFullScreenIntent(operation, true)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Light Alarm!") // todo
            .build()

        startForeground(NOTIFICATION_ID, notification)

        job = launch { start() }

        return START_REDELIVER_INTENT
    }

    abstract suspend fun start()

    final override fun onDestroy() {
        lightController.turnOff()
        job?.cancel()
    }

}

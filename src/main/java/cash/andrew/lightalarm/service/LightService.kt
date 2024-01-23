package cash.andrew.lightalarm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import cash.andrew.lightalarm.NotificationManager
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.data.AlarmKeeper
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.data.LightController
import cash.andrew.lightalarm.misc.alarmIdExtra
import cash.andrew.lightalarm.NotificationManager.Companion.ALARM_NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

abstract class LightService : Service(), CoroutineScope by MainScope() {
    private val Intent.lightServiceAction get() = LightServiceAction.values().first { action == it.name }

    enum class LightServiceAction {
        START,
        STOP
    }

    @Inject lateinit var lightController: LightController
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var alarmScheduler: AlarmScheduler

    private var job: Job? = null

    final override fun onCreate() {
        alarmAppComponent.lightServiceComponent.inject(this)
    }

    final override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.lightServiceAction == LightServiceAction.STOP) {
            stop()
            return START_REDELIVER_INTENT
        }

        val alarm = requireNotNull(alarmKeeper.getAlarmById(intent.alarmIdExtra))
        startForeground(ALARM_NOTIFICATION_ID, notificationManager.alarmNotification(alarm))

        job = launch {
            // give the system some time to show the notification first
            delay(8 * 1000)
            start()
        }

        return START_REDELIVER_INTENT
    }

    abstract suspend fun start()

    final override fun onDestroy() {
        lightController.turnOff()
        job?.cancel()
    }

    private fun stop() {
        job?.let {
            it.cancel()
            job = null
            stopSelf()
        }
    }
}

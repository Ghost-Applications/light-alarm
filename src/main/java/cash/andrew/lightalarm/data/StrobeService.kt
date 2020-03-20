package cash.andrew.lightalarm.data

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.os.IBinder
import cash.andrew.lightalarm.ALARM_CHANNEL_ID
import cash.andrew.lightalarm.R
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.reciever.NOTIFICATION_ID
import cash.andrew.lightalarm.reciever.PENDING_INTENT_ID
import cash.andrew.lightalarm.ui.AlarmActivity
import kotlinx.coroutines.*
import javax.inject.Inject

enum class StrobeServiceActions {
    START,
    STOP
}

class StrobeService : Service(), CoroutineScope by MainScope() {

    @Inject lateinit var lightController: LightController

    private var strobeJob: Job? = null

    override fun onCreate() {
        alarmAppComponent.inject(this)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        // todo don't duplicate code.
        // this is in Alarm Reciecer too :D
        val activityIntent = Intent(this, AlarmActivity::class.java)
        val operation = PendingIntent.getActivity(
            this,
            PENDING_INTENT_ID,
            activityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification: Notification = Notification.Builder(this, ALARM_CHANNEL_ID)
            .setContentTitle("Testing")
            .setContentText("Testing some stuff")
            .setSmallIcon(R.drawable.ic_add_alarm_24px)
            .setContentIntent(operation)
            .setTicker("Ticker...")
            .build()

        startForeground(NOTIFICATION_ID, notification)

        strobeJob = when(intent.strobeAction) {
            StrobeServiceActions.START -> {
                launch { strobe() }
            }
            StrobeServiceActions.STOP -> {
                strobeJob?.cancel()
                null
            }
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        strobeJob?.cancel()
    }

    private suspend fun strobe() {
        while(isActive) {
            if (lightController.isLightOn) {
                lightController.turnOff()
            } else {
                lightController.turnOn()
            }
            delay(1000) // todo setting?
        }
    }
}

val Intent.strobeAction get() = StrobeServiceActions.values().first { action == it.name }

fun Context.startStrobeService() {
    val intent = Intent(this, StrobeService::class.java).apply {
        action = StrobeServiceActions.START.name
    }
    startForegroundService(intent)
}

fun Context.stopStrobeService() {
    val intent = Intent(this, StrobeService::class.java).apply {
        action = StrobeServiceActions.STOP.name
    }
    startForegroundService(intent)
}

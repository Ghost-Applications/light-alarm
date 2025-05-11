package cash.andrew.lightalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cash.andrew.lightalarm.data.*
import cash.andrew.lightalarm.misc.alarmIdExtra
import cash.andrew.lightalarm.service.startLightService
import cash.andrew.lightalarm.service.startStrobeService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var lightController: LightController
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceived called context=%s, intent=%s", context, intent)

        val id = intent.alarmIdExtra
        val alarm = requireNotNull(alarmKeeper.getAlarmById(id))

        // if alarm doesn't repeat disable it
        // so it doesn't go off again tomorrow.
        if (!alarm.repeat) {
            alarmKeeper.updateAlarm(alarm.copy(enabled = false))
        }

        alarmScheduler.scheduleNextAlarm()

        if (alarm.strobe) {
            context.startStrobeService(id)
            return
        }

        context.startLightService(id)
    }
}

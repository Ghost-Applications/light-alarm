package cash.andrew.lightalarm.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.data.AlarmScheduler
import timber.log.Timber
import javax.inject.Inject

class AlarmBootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive() context=%s, intent=%s", context, intent)
        context.alarmAppComponent.inject(this)

        alarmScheduler.scheduleNextAlarm()
    }
}

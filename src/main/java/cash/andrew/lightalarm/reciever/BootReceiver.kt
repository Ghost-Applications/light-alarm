package cash.andrew.lightalarm.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.data.AlarmScheduler
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        context.alarmAppComponent.inject(this)

        alarmScheduler.scheduleNextAlarm()
    }
}

package cash.andrew.lightalarm.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import cash.andrew.lightalarm.alarmAppComponent
import cash.andrew.lightalarm.data.AlarmScheduler
import timber.log.Timber
import javax.inject.Inject

class AlarmBootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive() context=%s, intent=%s", context, intent)

        if (intent.action != ACTION_BOOT_COMPLETED) return

        context.alarmAppComponent.inject(this)

        alarmScheduler.scheduleNextAlarm()
    }
}

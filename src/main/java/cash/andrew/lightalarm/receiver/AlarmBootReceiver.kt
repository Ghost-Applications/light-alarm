package cash.andrew.lightalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import cash.andrew.lightalarm.data.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive() context=%s, intent=%s", context, intent)

        if (intent.action != ACTION_BOOT_COMPLETED) return

        alarmScheduler.scheduleNextAlarm()
    }
}

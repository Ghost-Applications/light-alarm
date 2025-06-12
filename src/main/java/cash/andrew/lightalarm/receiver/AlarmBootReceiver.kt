package cash.andrew.lightalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import cash.andrew.lightalarm.data.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBootReceiver : BroadcastReceiver() {

    @Inject lateinit var alarmScheduler: AlarmScheduler

    private val coroutineScope = MainScope() + CoroutineName("AlarmBootReceiverScope")

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive() context=%s, intent=%s", context, intent)

        if (intent.action != ACTION_BOOT_COMPLETED) return

        coroutineScope.launch {
            alarmScheduler.scheduleNextAlarm()
        }
    }
}

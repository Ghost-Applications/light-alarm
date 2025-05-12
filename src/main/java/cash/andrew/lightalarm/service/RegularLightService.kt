package cash.andrew.lightalarm.service

import android.content.Context
import android.content.Intent
import cash.andrew.lightalarm.misc.putAlarmIdExtra
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.coroutineContext

class RegularLightService : LightService() {
    override suspend fun start() {
        // run to keep the light turned on until turned off inside the app
        // 10 minutes or 10 * 60 * 2 (because we delay for 500 ms)
        for (i in 0 .. (10 * 60 * 2)) {
            lightController.turnOn()
            delay(500)
        }
    }
}

fun Context.startLightService(alarmId: UUID) {
    val intent = lightServiceIntent.apply {
        action = LightService.LightServiceAction.START.name
        putAlarmIdExtra(alarmId)
    }
    startForegroundService(intent)
}

fun Context.stopLightService() {
    startService(stopLightServiceIntent)
}

private val Context.stopLightServiceIntent get() = lightServiceIntent.apply {
    action = LightService.LightServiceAction.STOP.name
}

private val Context.lightServiceIntent get() = Intent(this, RegularLightService::class.java)

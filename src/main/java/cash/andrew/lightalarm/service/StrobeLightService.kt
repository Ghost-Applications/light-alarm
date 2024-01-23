package cash.andrew.lightalarm.service

import android.content.Intent
import android.content.Context
import cash.andrew.lightalarm.misc.putAlarmIdExtra
import kotlinx.coroutines.*
import java.util.*

class StrobeLightService : LightService() {

    override suspend fun start() {
        while(isActive) {
            if (lightController.isLightOn) {
                lightController.turnOff()
            } else {
                lightController.turnOn()
            }
            delay(1000)
        }
    }
}

fun Context.startStrobeService(alarmId: UUID) {
    val intent = strobeLightServiceIntent.apply {
        action = LightService.LightServiceAction.START.name
        putAlarmIdExtra(alarmId)
    }


    startForegroundService(intent)
}

fun Context.stopStrobeService() {
    startService(stopStrobeStrobeServiceIntent)
}

private val Context.stopStrobeStrobeServiceIntent get() = strobeLightServiceIntent.apply {
    action = LightService.LightServiceAction.STOP.name
}

private val Context.strobeLightServiceIntent get() = Intent(this, StrobeLightService::class.java)

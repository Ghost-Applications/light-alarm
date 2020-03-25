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
            delay(1000) // todo setting?
        }
    }
}

fun Context.startStrobeService(alarmId: UUID) {
    val intent = Intent(this, StrobeLightService::class.java).apply {
        action = LightService.LightServiceAction.START.name
        putAlarmIdExtra(alarmId)
    }
    startForegroundService(intent)
}

fun Context.stopStrobeService() {
    val intent = Intent(this, StrobeLightService::class.java).apply {
        action = LightService.LightServiceAction.STOP.name
    }
    startForegroundService(intent)
}

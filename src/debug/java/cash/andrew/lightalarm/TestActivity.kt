package cash.andrew.lightalarm

import android.app.Activity
import android.os.Bundle
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.databinding.ActivityNotificationTestBinding
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class TestActivity: Activity() {

    private lateinit var binding: ActivityNotificationTestBinding

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lightController = alarmAppComponent.lightServiceComponent.lightController
        val notificationManager = alarmAppComponent.lightAlarmNotificationManager
        val alarmScheduler = alarmAppComponent.alarmScheduler
        val alarmKeeper = alarmAppComponent.alarmKeeper

        binding.testAlarmNotification.setOnClickListener {
            it.postDelayed({
                notificationManager.showAlarmNotification(Alarm(time = LocalTime.now()))
            }, 3.seconds.inWholeMilliseconds)
        }

        binding.testCrash.setOnClickListener {
            throw Exception("☠️ Test Crash ️☠️")
        }

        binding.turnOnFlashlight.setOnClickListener {
            if (lightController.hasFlashLight) {
                Timber.w("No light available.")
            }

            if (lightController.isLightOn) {
                lightController.turnOff()
            } else {
                lightController.turnOn()
            }
        }

        binding.testScheduler.setOnClickListener {
            val testAlarm = Alarm(
                enabled = true,
                days = EnumSet.allOf(DayOfWeek::class.java),
                time = LocalTime.now()
            )
            alarmKeeper.addAlarm(testAlarm)

            alarmScheduler.schedule(ZonedDateTime.now().plusSeconds(5), testAlarm.id)
        }
    }
}

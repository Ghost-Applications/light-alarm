package cash.andrew.lightalarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.data.AlarmKeeper
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.data.LightController
import cash.andrew.lightalarm.databinding.ActivityNotificationTestBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.EnumSet
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
class TestActivity: AppCompatActivity() {

    private lateinit var binding: ActivityNotificationTestBinding

    @Inject lateinit var lightController: LightController
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var notificationManager: NotificationManager

    private val coroutineScope = MainScope() + CoroutineName("TestActivityScope")

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            coroutineScope.launch {
                alarmKeeper.addAlarm(testAlarm)
            }

            alarmScheduler.schedule(ZonedDateTime.now().plusSeconds(5), testAlarm.id)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}

package cash.andrew.lightalarm

import android.app.Activity
import android.os.Bundle
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.databinding.ActivityNotificationTestBinding
import java.time.LocalTime
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class TestActivity: Activity() {

    private lateinit var binding: ActivityNotificationTestBinding

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notificationManager = alarmAppComponent.lightServiceComponent
            .notificationManager

        binding.testAlarmNotification.setOnClickListener {
            it.postDelayed({
                notificationManager.showAlarmNotification(Alarm(time = LocalTime.now()))
            }, 3.seconds.toLongMilliseconds())
        }

        binding.testCrash.setOnClickListener {
            throw Exception("☠️ Test Crash ️☠️")
        }
    }
}
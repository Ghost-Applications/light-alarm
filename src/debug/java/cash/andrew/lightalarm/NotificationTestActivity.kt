package cash.andrew.lightalarm

import android.app.Activity
import android.os.Bundle
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.databinding.ActivityNotificationTestBinding
import java.time.LocalTime

class NotificationTestActivity: Activity() {

    private lateinit var binding: ActivityNotificationTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notificationManager = alarmAppComponent.lightServiceComponent
            .notificationManager

        binding.button.setOnClickListener {
            it.postDelayed({
                notificationManager.showAlarmNotification(Alarm(time = LocalTime.now()))
            }, 3000)
        }
    }
}
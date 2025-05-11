package cash.andrew.lightalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import io.paperdb.Paper
import javax.inject.Inject

abstract class AlarmApp : Application() {

    companion object {
        const val ALARM_CHANNEL_ID = "Alarm"
    }

    @Inject lateinit var notificationManager: NotificationManager


    override fun onCreate() {
        super.onCreate()

        setup()

        Paper.init(this)

        notificationManager.createNotificationChannel(
            NotificationChannel(ALARM_CHANNEL_ID, "Alarm", NotificationManager.IMPORTANCE_HIGH)
        )
    }

    // override in build specific settings to set things up
    abstract fun setup()
}

package cash.andrew.lightalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import io.paperdb.Paper

abstract class AlarmApp : Application(), ComponentContainer<AlarmAppComponent> {

    companion object {
        const val ALARM_CHANNEL_ID = "Alarm"
    }

    private lateinit var _component: AlarmAppComponent
    override val component: AlarmAppComponent by lazy { _component }

    override fun onCreate() {
        super.onCreate()

        _component = componentBuilder
            .application(this)
            .build()

        setup()

        Paper.init(this)

        component.notificationManager.createNotificationChannel(
            NotificationChannel(ALARM_CHANNEL_ID, "Alarm", NotificationManager.IMPORTANCE_HIGH)
        )
    }

    // override in build specific settings to set things up
    abstract fun setup()
}

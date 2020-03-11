package cash.andrew.lightalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import io.paperdb.Paper
import timber.log.Timber
import timber.log.Timber.DebugTree

const val ALARM_CHANNEL_ID = "Alarm"

class AlarmApp : Application(), ComponentContainer<AlarmAppComponent>{

    private lateinit var _component: AlarmAppComponent
    override val component: AlarmAppComponent by lazy { _component }

    override fun onCreate() {
        super.onCreate()

        _component = componentBuilder
            .application(this)
            .build()

        Timber.plant(DebugTree())

        Paper.init(this)

        component.notificationManager.createNotificationChannel(
            NotificationChannel(ALARM_CHANNEL_ID, "Alarm", NotificationManager.IMPORTANCE_HIGH)
        )
    }
}

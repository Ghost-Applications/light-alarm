package cash.andrew.lightalarm

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.os.Vibrator
import android.text.format.DateFormat
import androidx.core.content.getSystemService
import cash.andrew.lightalarm.data.DataModule
import cash.andrew.lightalarm.service.LightService
import cash.andrew.lightalarm.reciever.AlarmReceiver
import cash.andrew.lightalarm.reciever.AlarmBootReceiver
import cash.andrew.lightalarm.ui.ActivityComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.paperdb.Book
import io.paperdb.Paper
import java.time.Clock
import javax.inject.Singleton

val componentBuilder: AlarmAppComponent.Builder = DaggerAlarmAppComponent.builder()

@Suppress("UNCHECKED_CAST")
val Context.alarmAppComponent: AlarmAppComponent get() = (applicationContext as ComponentContainer<AlarmAppComponent>).component

@Singleton
@Component(modules = [AlarmAppModule::class, DataModule::class])
interface AlarmAppComponent {

    fun inject(lightService: LightService)
    fun inject(alarmReceiver: AlarmReceiver)
    fun inject(bootReceiver: AlarmBootReceiver)

    val activityComponentBuilder: ActivityComponent.Builder
    val notificationManager: NotificationManager

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AlarmAppComponent
    }
}

@Module
object AlarmAppModule {

    @Provides
    @Singleton
    fun provideNotificationManager(application: Application): NotificationManager = requireNotNull(application.getSystemService())

    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences = app.getSharedPreferences("App-SharedPrefs", 0)

    @Provides
    @Singleton
    fun provideAlarmManager(application: Application): AlarmManager = requireNotNull(application.getSystemService())

    @Provides
    @Singleton
    fun provideCameraManager(app: Application): CameraManager = requireNotNull(app.getSystemService())

    @Provides
    @Singleton
    fun provideBook(): Book = Paper.book()

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

    @Provides
    @Singleton
    fun provideDateTimeFormatter(app: Application): java.text.DateFormat = DateFormat.getTimeFormat(app)

    @Provides
    @Singleton
    fun provideVibrator(app: Application): Vibrator = requireNotNull(app.getSystemService())
}

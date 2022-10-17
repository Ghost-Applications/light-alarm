package cash.andrew.lightalarm

import android.app.AlarmManager
import android.app.Application
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.os.Vibrator
import android.text.format.DateFormat
import androidx.core.content.getSystemService
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.data.DataModule
import cash.andrew.lightalarm.data.adapter.DayOfWeekEnumSetJsonAdapter
import cash.andrew.lightalarm.data.adapter.LocalTimeJsonAdapter
import cash.andrew.lightalarm.data.adapter.UUIDJsonAdapter
import cash.andrew.lightalarm.reciever.AlarmReceiver
import cash.andrew.lightalarm.reciever.AlarmBootReceiver
import cash.andrew.lightalarm.service.LightServiceComponent
import cash.andrew.lightalarm.ui.ActivityComponent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.paperdb.Book
import io.paperdb.Paper
import java.io.File
import java.time.Clock
import javax.inject.Singleton

val componentBuilder: AlarmAppComponent.Builder = DaggerAlarmAppComponent.builder()

@Suppress("UNCHECKED_CAST")
val Context.alarmAppComponent: AlarmAppComponent get() = (applicationContext as ComponentContainer<AlarmAppComponent>).component

@Singleton
@Component(modules = [AlarmAppModule::class, DataModule::class])
interface AlarmAppComponent {
    fun inject(alarmReceiver: AlarmReceiver)
    fun inject(bootReceiver: AlarmBootReceiver)

    val activityComponentBuilder: ActivityComponent.Builder
    val lightServiceComponent: LightServiceComponent
    val notificationManager: NotificationManager
    val firebaseCrashlytics: FirebaseCrashlytics

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
    fun provideKeyGaurdManager(app: Application): KeyguardManager = requireNotNull(app.getSystemService())

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

    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(UUIDJsonAdapter())
        .add(DayOfWeekEnumSetJsonAdapter())
        .add(LocalTimeJsonAdapter())
        .build()

    @Provides
    @Singleton
    fun provideAlarmListAdapter(moshi: Moshi): JsonAdapter<List<Alarm>> {
        val type = Types.newParameterizedType(List::class.java, Alarm::class.java)
        return moshi.adapter(type)
    }

    @Provides
    @Singleton
    fun provideAlarmStorageFile(app: Application): File {
        return File(app.getDir("alarms", Context.MODE_PRIVATE), "alarms.json")
    }
}

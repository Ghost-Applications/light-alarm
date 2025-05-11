package cash.andrew.lightalarm

import android.app.AlarmManager
import android.app.Application
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.os.Vibrator
import androidx.core.content.getSystemService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.paperdb.Book
import io.paperdb.Paper
import java.io.File
import java.text.DateFormat
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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
    fun provideDateTimeFormatter(app: Application): DateFormat = android.text.format.DateFormat.getTimeFormat(app)

    @Provides
    @Singleton
    fun provideVibrator(app: Application): Vibrator = requireNotNull(app.getSystemService())

    @Provides
    @Singleton
    fun provideCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun provideAlarmStorageFile(app: Application): File {
        return File(app.getDir("alarms", Context.MODE_PRIVATE), "alarms.json")
    }
}
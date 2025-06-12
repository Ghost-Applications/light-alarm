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
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.preferencesDataStoreFile
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.data.AndroidAlarmManagerWrapper
import cash.andrew.lightalarm.data.LightAlarmAlarmManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeBufferedSourceToSequence
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.File
import java.text.DateFormat
import java.time.Clock
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmAppModule {

    companion object {
        @Provides
        @Singleton
        fun provideNotificationManager(application: Application): NotificationManager =
            requireNotNull(application.getSystemService())

        @Provides
        @Singleton
        fun provideSharedPreferences(app: Application): SharedPreferences =
            app.getSharedPreferences("App-SharedPrefs", 0)

        @Provides
        @Singleton
        fun provideAlarmManager(application: Application): AlarmManager =
            requireNotNull(application.getSystemService())

        @Provides
        @Singleton
        fun provideKeyGaurdManager(app: Application): KeyguardManager =
            requireNotNull(app.getSystemService())

        @Provides
        @Singleton
        fun provideCameraManager(app: Application): CameraManager =
            requireNotNull(app.getSystemService())

        @Provides
        @Singleton
        fun provideAlarmDataStore(@ApplicationContext context: Context): DataStore<List<Alarm>> {
            return DataStoreFactory.create(
                storage = OkioStorage<List<Alarm>>(
                    fileSystem = FileSystem.SYSTEM,
                    serializer = object : OkioSerializer<List<Alarm>> {

                        override val defaultValue: List<Alarm> = emptyList()

                        override suspend fun readFrom(source: BufferedSource): List<Alarm> {
                            return Json.decodeFromBufferedSource(source)
                        }

                        override suspend fun writeTo(t: List<Alarm>, sink: BufferedSink) {
                            Json.encodeToBufferedSink(t, sink)
                        }
                    },
                    producePath = { context.dataStoreFile("alarms").toOkioPath() }
                ),
                corruptionHandler = ReplaceFileCorruptionHandler { emptyList<Alarm>() }
            )
        }

        @Provides
        @Singleton
        fun provideClock(): Clock = Clock.systemDefaultZone()

        @Provides
        @Singleton
        fun provideDateTimeFormatter(app: Application): DateFormat =
            android.text.format.DateFormat.getTimeFormat(app)

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

    @Binds
    abstract fun bindsLightAlarmAlarmManager(impl: AndroidAlarmManagerWrapper): LightAlarmAlarmManager
}
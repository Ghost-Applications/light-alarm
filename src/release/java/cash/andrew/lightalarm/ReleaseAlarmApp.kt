package cash.andrew.lightalarm

import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ReleaseAlarmApp: AlarmApp() {

    @Inject lateinit var firebaseCrashlytics: FirebaseCrashlytics

    override fun setup() {
        Timber.plant(CrashlyticsTree(firebaseCrashlytics))
    }
}
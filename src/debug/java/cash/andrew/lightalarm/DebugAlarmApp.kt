package cash.andrew.lightalarm

import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class DebugAlarmApp: AlarmApp() {
    override fun setup() {
        Timber.plant(Timber.DebugTree())
    }
}
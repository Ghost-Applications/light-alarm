package cash.andrew.lightalarm

import timber.log.Timber

class ReleaseAlarmApp: AlarmApp() {
    override fun setup() {
        Timber.plant(CrashlyticsTree(component.firebaseCrashlytics))
    }
}
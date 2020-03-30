package cash.andrew.lightalarm

import timber.log.Timber

class DebugAlarmApp: AlarmApp() {
    override fun setup() {
        Timber.plant(Timber.DebugTree())
    }
}
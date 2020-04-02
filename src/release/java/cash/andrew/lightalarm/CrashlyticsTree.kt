package cash.andrew.lightalarm

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

private val SKIP_PRIORITIES = setOf(Log.VERBOSE, Log.DEBUG, Log.INFO)

class CrashlyticsTree(private val firebaseCrashlytics: FirebaseCrashlytics) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority in SKIP_PRIORITIES) return

        if (t == null) {
            firebaseCrashlytics.recordException(Exception(message))
        } else {
            firebaseCrashlytics.recordException(t)
        }
    }
}

package cash.andrew.lightalarm

import android.app.Activity
import android.content.Context.POWER_SERVICE
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.*
import android.view.WindowManager.LayoutParams.*
import cash.andrew.lightalarm.ui.ActivityScope
import javax.inject.Inject

@ActivityScope
class DefaultRiseAndShine @Inject constructor(
    private val activity: Activity
): RiseAndShine {
    override fun riseAndShine() {

        val power = activity.getSystemService(POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        val lock = power.newWakeLock(
            FULL_WAKE_LOCK or ACQUIRE_CAUSES_WAKEUP or ON_AFTER_RELEASE,
            "debug:wakeup!"
        )
        lock.acquire(1000)
        lock.release()

        if (Build.VERSION.SDK_INT >= 27) {
            activity.setShowWhenLocked(true)
            activity.setTurnScreenOn(true)
            activity.window.addFlags(FLAG_KEEP_SCREEN_ON)
            return
        }

        @Suppress("DEPRECATION")
        activity.window.addFlags(
            FLAG_SHOW_WHEN_LOCKED
                    or FLAG_TURN_SCREEN_ON
                    or FLAG_KEEP_SCREEN_ON
        )
    }
}
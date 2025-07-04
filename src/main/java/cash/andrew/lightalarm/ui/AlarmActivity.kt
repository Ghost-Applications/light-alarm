package cash.andrew.lightalarm.ui

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.databinding.ActivityAlarmBinding
import cash.andrew.lightalarm.misc.addOnGlobalLayoutListener
import cash.andrew.lightalarm.misc.alarmIdExtra
import cash.andrew.lightalarm.service.stopLightService
import cash.andrew.lightalarm.service.stopStrobeService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : AppCompatActivity() {

    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var vibrator: Vibrator
    @Inject lateinit var keyguardManager: KeyguardManager

    private lateinit var binding: ActivityAlarmBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showOverLockScreen()

        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val (root, alarmSlider, alarmSnooze, alarmOff) = binding.run {
            listOf(root, alarmSlider, alarmSnooze, alarmOff)
        }

        ((alarmSlider as ImageView).drawable as Animatable).start()


        val springAnimation = SpringAnimation(alarmSlider, DynamicAnimation.TRANSLATION_Y)

        // bottom view
        var alarmSnoozeCenterY = 0f
        var alarmOffCenterY = 0f

        root.addOnGlobalLayoutListener {
            alarmOffCenterY = alarmOff.y + (alarmOff.height / 2)
            alarmSnoozeCenterY = alarmSnooze.y + (alarmSnooze.height / 2)

            springAnimation.apply {
                spring = SpringForce(root.y / 2).apply {
                    stiffness = SpringForce.STIFFNESS_LOW
                    dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
                }
            }
        }

        var dy = 0f
        alarmSlider.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    Timber.d("Action down")
                    dy = view.y - event.rawY
                    springAnimation.cancel()

                    vibrate()
                }
                MotionEvent.ACTION_MOVE -> {
                    Timber.d("Action move")
                    val sliderCenter = alarmSlider.y + (alarmSlider.height.toFloat() / 2f)

                    if (sliderCenter >= alarmSnoozeCenterY) {
                        alarmScheduler.schedule(
                            time = ZonedDateTime.now().plusMinutes(10),
                            alarmId = intent.alarmIdExtra
                        )
                        shutdown()
                    }

                    if (sliderCenter <= alarmOffCenterY) shutdown()

                    alarmSlider.animate()
                        .y(event.rawY + dy)
                        .setDuration(0)
                        .start()
                }
                MotionEvent.ACTION_UP -> {
                    Timber.d("Action up")
                    springAnimation.start()
                }
            }
            true
        }
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT < 27) {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
            return
        }

        setShowWhenLocked(true)
        setTurnScreenOn(true)
        keyguardManager.requestDismissKeyguard(this, null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun shutdown() {
        vibrate()
        stopLightService()
        stopStrobeService()
        overridePendingTransition(0, android.R.anim.fade_out)
        finish()
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT < 29) {
            @Suppress("DEPRECATION")
            vibrator.vibrate(75)
            return
        }

        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK).let {
            vibrator.vibrate(it)
        }
    }
}

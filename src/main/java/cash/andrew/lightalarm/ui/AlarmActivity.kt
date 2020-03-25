package cash.andrew.lightalarm.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import cash.andrew.lightalarm.ComponentContainer
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.service.stopLightService
import cash.andrew.lightalarm.service.stopStrobeService
import cash.andrew.lightalarm.databinding.ActivityAlarmBinding
import cash.andrew.lightalarm.misc.addOnGlobalLayoutListener
import cash.andrew.lightalarm.misc.alarmIdExtra
import java.time.ZonedDateTime
import javax.inject.Inject

class AlarmActivity : AppCompatActivity(), ComponentContainer<ActivityComponent> {

    @Inject lateinit var alarmScheduler: AlarmScheduler

    private lateinit var binding: ActivityAlarmBinding

    override val component: ActivityComponent by lazy { makeComponent() }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component.inject(this)

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
                    dy = view.y - event.rawY
                    springAnimation.cancel()
                }
                MotionEvent.ACTION_MOVE -> {
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
                    springAnimation.start()
                }
            }
            true
        }
    }

    private fun shutdown() {
        stopLightService()
        stopStrobeService()
        overridePendingTransition(0, android.R.anim.fade_out)
        finish()
    }
}

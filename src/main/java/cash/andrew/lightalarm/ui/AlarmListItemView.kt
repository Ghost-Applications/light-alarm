package cash.andrew.lightalarm.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.data.AlarmDateTimeFormatter
import cash.andrew.lightalarm.data.AlarmKeeper
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.databinding.AlarmListItemViewBinding
import cash.andrew.lightalarm.misc.toEnumSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmListItemView(
    context: Context,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    private val coroutineScope = MainScope() + CoroutineName("AlarmListItmeViewScope")

    @Inject lateinit var dateFormat: java.text.DateFormat
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var alarmDateTimeFormatter: AlarmDateTimeFormatter

    private val dayViews
        get() = listOf(
            binding.chipSunday,
            binding.chipMonday,
            binding.chipTuesday,
            binding.chipWednesday,
            binding.chipThursday,
            binding.chipFriday,
            binding.chipSaturday
        )

    private val binding: AlarmListItemViewBinding by lazy { AlarmListItemViewBinding.bind(this) }

    fun bind(alarm: Alarm) {
        Timber.d("bind() called alarm = %s", alarm)

        val currentAlarm: suspend () -> Alarm = {
            // return a default alarm if an alarm has been
            // added quickly after an alarm has been swiped away.
            alarmKeeper.getAlarmById(alarm.id) ?: Alarm(
                id = alarm.id,
                time = LocalTime.now()
            ).also { Timber.w("alarm with id %s not found, using default", alarm.id) }
        }

        alarm.setupViewWithAlarmData()

        binding.enableAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            // disable all the things and delete the alarm for the alarm
            binding.strobeCheckbox.isEnabled = isChecked
            binding.repeatCheckbox.isEnabled = isChecked
            binding.alarmDayGroup.children.forEach { it.isEnabled = isChecked }

            coroutineScope.launch {
                val newAlarm = currentAlarm().copy(enabled = isChecked)
                alarmKeeper.updateAlarm(newAlarm)

                alarmScheduler.scheduleNextAlarm()
            }
        }

        binding.strobeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            coroutineScope.launch {
                // update alarm on disk...
                val newAlarm = currentAlarm().copy(strobe = isChecked)
                alarmKeeper.updateAlarm(newAlarm)
            }
        }

        binding.repeatCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // deal with alarm being one time or schedule for each day...
            binding.alarmDayGroup.children.forEach { it.isEnabled = isChecked }

            coroutineScope.launch {
                val newAlarm = currentAlarm().copy(repeat = isChecked)
                alarmKeeper.updateAlarm(newAlarm)
                alarmScheduler.scheduleNextAlarm()
            }
        }

        dayViews.forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                coroutineScope.launch {
                    // update and schedule
                    val dayOfWeek = buttonView.tag as DayOfWeek
                    val updated = currentAlarm().copy(
                        days = currentAlarm().days.toMutableSet()
                            .apply { if (isChecked) add(dayOfWeek) else remove(dayOfWeek) }
                            .toEnumSet()
                    )

                    alarmKeeper.updateAlarm(updated)
                    alarmScheduler.scheduleNextAlarm()
                }
            }
        }
    }

    private fun Alarm.setupViewWithAlarmData() {
        binding.alarmText.text = alarmDateTimeFormatter.formatAlarmTime(this)
        binding.enableAlarmSwitch.isChecked = enabled

        binding.strobeCheckbox.isChecked = strobe
        binding.strobeCheckbox.isEnabled = enabled

        binding.repeatCheckbox.isChecked = repeat
        binding.repeatCheckbox.isEnabled = enabled

        dayViews.forEach { it.isEnabled = repeat }
        DayOfWeek.entries.forEach { dayOfWeek ->
            val dayChip = when (dayOfWeek) {
                MONDAY -> binding.chipMonday
                TUESDAY -> binding.chipTuesday
                WEDNESDAY -> binding.chipWednesday
                THURSDAY -> binding.chipThursday
                FRIDAY -> binding.chipFriday
                SATURDAY -> binding.chipSaturday
                SUNDAY -> binding.chipSunday
            }

            dayChip.tag = dayOfWeek
            dayChip.isChecked = days.contains(dayOfWeek)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        coroutineScope.cancel()
    }
}

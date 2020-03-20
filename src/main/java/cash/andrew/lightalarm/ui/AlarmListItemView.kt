package cash.andrew.lightalarm.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.data.AlarmKeeper
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.databinding.AlarmListItemViewBinding
import cash.andrew.lightalarm.misc.toEnumSet
import java.time.DayOfWeek
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject

class AlarmListItemView(
    context: Context,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    @Inject lateinit var dateFormat: java.text.DateFormat
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var alarmScheduler: AlarmScheduler

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

    init {
        context.activityComponent.inject(this)
    }

    fun bind(alarm: Alarm) {
        val currentAlarm = { alarmKeeper.getAlarmById(alarm.id)!! }
        alarm.setupViewWithAlarmData()

        binding.enableAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            // disable all the things and delete the alarm for the alarm
            binding.strobeCheckbox.isEnabled = isChecked
            binding.repeatCheckbox.isEnabled = isChecked
            binding.alarmDayGroup.children.forEach { it.isEnabled = isChecked }

            val newAlarm = currentAlarm().copy(enabled = isChecked)
            alarmKeeper.updateAlarm(newAlarm)

            alarmScheduler.scheduleNextAlarm()
        }

        binding.strobeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // update alarm on disk...
            val newAlarm = currentAlarm().copy(strobe = isChecked)
            alarmKeeper.updateAlarm(newAlarm)
        }

        binding.repeatCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // deal with alarm being one time or schedule for each day...
            binding.alarmDayGroup.children.forEach { it.isEnabled = isChecked }

            val newAlarm = currentAlarm().copy(repeat = isChecked)
            alarmKeeper.updateAlarm(newAlarm)
            alarmScheduler.scheduleNextAlarm()
        }

        dayViews.forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
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

    private fun Alarm.setupViewWithAlarmData() {
        binding.alarmText.text = dateFormat.format(
            Date.from(ZonedDateTime.now().withHour(time.hour).withMinute(time.minute).toInstant())
        )
        binding.enableAlarmSwitch.isChecked = enabled

        binding.strobeCheckbox.isChecked = strobe
        binding.strobeCheckbox.isEnabled = enabled

        binding.repeatCheckbox.isChecked = repeat
        binding.repeatCheckbox.isEnabled = enabled

        dayViews.forEach { it.isEnabled = repeat }
        DayOfWeek.values().forEach { dayOfWeek ->
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
}

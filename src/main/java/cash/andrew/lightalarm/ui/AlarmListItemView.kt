package cash.andrew.lightalarm.ui

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.data.AlarmKeeper
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.toEnumSet
import kotlinx.android.synthetic.main.alarm_list_item_view.view.alarm_day_group
import kotlinx.android.synthetic.main.alarm_list_item_view.view.alarm_text
import kotlinx.android.synthetic.main.alarm_list_item_view.view.chip_friday
import kotlinx.android.synthetic.main.alarm_list_item_view.view.chip_monday
import kotlinx.android.synthetic.main.alarm_list_item_view.view.chip_saturday
import kotlinx.android.synthetic.main.alarm_list_item_view.view.chip_sunday
import kotlinx.android.synthetic.main.alarm_list_item_view.view.chip_thursday
import kotlinx.android.synthetic.main.alarm_list_item_view.view.chip_tuesday
import kotlinx.android.synthetic.main.alarm_list_item_view.view.chip_wednesday
import kotlinx.android.synthetic.main.alarm_list_item_view.view.enable_alarm_switch
import kotlinx.android.synthetic.main.alarm_list_item_view.view.repeat_checkbox
import kotlinx.android.synthetic.main.alarm_list_item_view.view.strobe_checkbox
import timber.log.Timber
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
            chip_sunday,
            chip_monday,
            chip_tuesday,
            chip_wednesday,
            chip_thursday,
            chip_friday,
            chip_saturday
        )

    init {
        context.activityComponent.inject(this)
    }

    fun bind(alarm: Alarm) {
        val currentAlarm = { alarmKeeper.getAlarmById(alarm.id)!! }
        alarm.setupViewWithAlarmData()

        enable_alarm_switch.setOnCheckedChangeListener { _, isChecked ->
            // disable all the things and delete the alarm for the alarm
            strobe_checkbox.isEnabled = isChecked
            repeat_checkbox.isEnabled = isChecked
            alarm_day_group.children.forEach { it.isEnabled = isChecked }

            val newAlarm = currentAlarm().copy(enabled = isChecked)
            alarmKeeper.updateAlarm(newAlarm)

            alarmScheduler.scheduleNextAlarm()
        }

        strobe_checkbox.setOnCheckedChangeListener { _, isChecked ->
            // update alarm on disk...
            val newAlarm = currentAlarm().copy(strobe = isChecked)
            alarmKeeper.updateAlarm(newAlarm)
        }

        repeat_checkbox.setOnCheckedChangeListener { _, isChecked ->
            // deal with alarm being one time or schedule for each day...
            alarm_day_group.children.forEach { it.isEnabled = isChecked }

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
        alarm_text.text = dateFormat.format(
            Date.from(ZonedDateTime.now().withHour(time.hour).withMinute(time.minute).toInstant())
        )
        enable_alarm_switch.isChecked = enabled

        strobe_checkbox.isChecked = strobe
        strobe_checkbox.isEnabled = enabled

        repeat_checkbox.isChecked = repeat
        repeat_checkbox.isEnabled = enabled

        dayViews.forEach { it.isEnabled = repeat }
        DayOfWeek.values().forEach { dayOfWeek ->
            val dayChip = when (dayOfWeek) {
                MONDAY -> chip_monday
                TUESDAY -> chip_tuesday
                WEDNESDAY -> chip_wednesday
                THURSDAY -> chip_thursday
                FRIDAY -> chip_friday
                SATURDAY -> chip_saturday
                SUNDAY -> chip_sunday
            }

            dayChip.tag = dayOfWeek
            dayChip.isChecked = days.contains(dayOfWeek)
        }
    }
}

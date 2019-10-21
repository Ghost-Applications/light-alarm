package cash.andrew.lightalarm

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
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

class AlarmListItemView(
    context: Context?,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

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

    fun bind(alarm: Alarm) = with(alarm) {
        // todo inject with dagger
        alarm_text.text = DateFormat.getTimeFormat(context).format(
            Date.from(ZonedDateTime.now().withHour(time.hour).withMinute(time.minute).toInstant())
        )
        enable_alarm_switch.isChecked = enabled
        strobe_checkbox.isChecked = strobe
        repeat_checkbox.isChecked = repeat

        dayViews.forEach { it.isEnabled = repeat }
        days.forEach { dayOfWeek ->
            when (dayOfWeek) {
                MONDAY -> chip_monday
                TUESDAY -> chip_tuesday
                WEDNESDAY -> chip_wednesday
                THURSDAY -> chip_thursday
                FRIDAY -> chip_friday
                SATURDAY -> chip_saturday
                SUNDAY -> chip_sunday
            }.also { tag = dayOfWeek }.isChecked = true
        }

        enable_alarm_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            // disable all the things and delete the alarm for the alarm
            strobe_checkbox.isEnabled = isChecked
            repeat_checkbox.isEnabled = isChecked
            alarm_day_group.children.forEach { it.isEnabled = isChecked }
        }

        strobe_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            // update alarm on disk...
        }

        repeat_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            // deal with alarm being one time or schedule for each day...
            alarm_day_group.children.forEach { it.isEnabled = isChecked }

            if (isChecked) {
                // schedule based on each day...

            } else {
                // schedule for next alarm
            }
        }

        dayViews.forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                // update and schedule
                val dayOfWeek = buttonView.tag as DayOfWeek

                if (isChecked) {
                    copy(days = days.toMutableSet().apply { add(dayOfWeek) })
                    return@setOnCheckedChangeListener
                }

                val updated = copy(days = days.toMutableSet().apply { remove(dayOfWeek) })
            }
        }
    }
}

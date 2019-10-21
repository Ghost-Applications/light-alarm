package cash.andrew.lightalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.time.LocalTime
import java.time.ZonedDateTime
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ComponentContainer<ActivityComponent> {

    override val component: ActivityComponent by lazy { makeComponent() }

    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var alarmAdapter: AlarmRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        component.inject(this)

        main_activity_container.displayedChildId = if (alarmKeeper.hasAlarms) alarms_list.id else main_no_alarms_setup.id

        alarms_list.layoutManager = LinearLayoutManager(this)
        alarms_list.adapter = alarmAdapter

        fab.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->

                val alarm = Alarm(
                    time = LocalTime.of(hour, minute)
                )

                alarmKeeper.addAlarm(alarm)
                alarmAdapter.addAlarm(alarm)

                createAlarm(selectedTime = LocalTime.of(hour, minute))
            }, 12, 0, DateFormat.is24HourFormat(this)).show()
        }
    }

    private fun createAlarm(selectedTime: LocalTime) {
        var alarmTime = ZonedDateTime.now()
            .withSecond(0)
            .withHour(selectedTime.hour)
            .withMinute(selectedTime.minute)

        if (alarmTime.isBefore(ZonedDateTime.now())) {
           alarmTime = alarmTime.plusDays(1)
        }

        val showIntent = PendingIntent.getActivity(applicationContext, 1338, Intent(applicationContext, MainActivity::class.java), PendingIntent.FLAG_CANCEL_CURRENT)
        val operation = PendingIntent.getBroadcast(applicationContext, 1337, Intent(applicationContext, AlarmReceiver::class.java), PendingIntent.FLAG_CANCEL_CURRENT)

        Timber.d("Setting alarm to $alarmTime")
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(alarmTime.toInstant().toEpochMilli(), showIntent),
            operation
        )
    }
}

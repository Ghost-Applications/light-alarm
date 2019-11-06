package cash.andrew.lightalarm.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.reciever.AlarmReceiver
import cash.andrew.lightalarm.ComponentContainer
import cash.andrew.lightalarm.R
import cash.andrew.lightalarm.R.layout
import cash.andrew.lightalarm.data.ALARM_ID_KEY
import cash.andrew.lightalarm.data.AlarmKeeper
import cash.andrew.lightalarm.data.AlarmScheduler
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ComponentContainer<ActivityComponent> {

    override val component: ActivityComponent by lazy { makeComponent() }

    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var alarmAdapter: AlarmRecyclerAdapter
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layout.activity_main)
        setSupportActionBar(toolbar)
        component.inject(this)

        main_activity_container.displayedChildId = if (alarmKeeper.hasAlarms) alarms_list.id else main_no_alarms_setup.id

        alarms_list.layoutManager = LinearLayoutManager(this)
        alarms_list.adapter = alarmAdapter

        val swipeToRemoveAlarmHelper = SwipeToRemoveAlarmHelper(
            view = alarms_list,
            alarmKeeper = alarmKeeper,
            alarmAdapter = alarmAdapter,
            alarmScheduler = alarmScheduler
        )
        ItemTouchHelper(swipeToRemoveAlarmHelper).attachToRecyclerView(alarms_list)

        fab.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->

                val alarm = Alarm(
                    time = LocalTime.of(hour, minute)
                )

                alarmKeeper.addAlarm(alarm)
                alarmAdapter.addAlarm(alarm)

                // un-schedule and reschedule alarms
                alarmScheduler.scheduleNextAlarm()

            }, 12, 0, DateFormat.is24HourFormat(this)).show()
        }
    }
}

class SwipeToRemoveAlarmHelper(
    private val view: View,
    private val alarmKeeper: AlarmKeeper,
    private val alarmAdapter: AlarmRecyclerAdapter,
    private val alarmScheduler: AlarmScheduler
) : ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val removedAlarm = alarmAdapter.getItem(position)
        alarmKeeper.removeAlarm(removedAlarm)
        alarmAdapter.removeAlarm(position)
        alarmScheduler.scheduleNextAlarm()

        Snackbar.make(view, view.context.getString(R.string.alarm_removed), Snackbar.LENGTH_LONG)
            .setAction(view.context.getString(R.string.undo)) {
                alarmKeeper.addAlarm(removedAlarm)
                alarmScheduler.scheduleNextAlarm()
            }
            .show()
    }
}

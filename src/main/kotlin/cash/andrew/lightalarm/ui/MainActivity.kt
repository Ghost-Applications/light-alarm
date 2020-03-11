package cash.andrew.lightalarm.ui

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cash.andrew.lightalarm.data.Alarm
import cash.andrew.lightalarm.ComponentContainer
import cash.andrew.lightalarm.R
import cash.andrew.lightalarm.data.AlarmKeeper
import cash.andrew.lightalarm.data.AlarmScheduler
import cash.andrew.lightalarm.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.time.LocalTime
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ComponentContainer<ActivityComponent> {

    override val component: ActivityComponent by lazy { makeComponent() }

    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var alarmAdapter: AlarmRecyclerAdapter
    @Inject lateinit var alarmScheduler: AlarmScheduler

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)
        component.inject(this)

        showAlarmList()

        binding.alarmsList.layoutManager = LinearLayoutManager(this)
        binding.alarmsList.adapter = alarmAdapter

        val swipeToRemoveAlarmHelper = SwipeToRemoveAlarmHelper(
            view = binding.alarmsList,
            alarmKeeper = alarmKeeper,
            alarmAdapter = alarmAdapter,
            alarmScheduler = alarmScheduler,
            refreshView = ::showAlarmList
        )
        ItemTouchHelper(swipeToRemoveAlarmHelper).attachToRecyclerView(binding.alarmsList)

        binding.fab.debounceClickListener {
            TimePickerDialog(this, { _, hour, minute ->

                val alarm = Alarm(
                    time = LocalTime.of(hour, minute)
                )

                alarmKeeper.addAlarm(alarm)
                alarmAdapter.addAlarm(alarm)

                // un-schedule and reschedule alarms
                alarmScheduler.scheduleNextAlarm()

                showAlarmList()

            }, 12, 0, DateFormat.is24HourFormat(this)).show()
        }
    }

    /** Shows alarm list or the no alarms set up message depending on if there are alarms. */
    private fun showAlarmList() {
        binding.mainActivityContainer.displayedChildId = if (alarmKeeper.hasAlarms) {
            binding.alarmsList.id
        }
        else binding.mainNoAlarmsSetup.id
    }
}

class SwipeToRemoveAlarmHelper(
    private val view: View,
    private val alarmKeeper: AlarmKeeper,
    private val alarmAdapter: AlarmRecyclerAdapter,
    private val alarmScheduler: AlarmScheduler,
    private val refreshView: () -> Unit
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
        refreshView()

        Snackbar.make(view, view.context.getString(R.string.alarm_removed), Snackbar.LENGTH_LONG)
            .setAction(view.context.getString(R.string.undo)) {
                alarmKeeper.addAlarm(removedAlarm)
                alarmAdapter.addAlarm(removedAlarm)
                alarmScheduler.scheduleNextAlarm()

                refreshView()
            }
            .show()
    }
}

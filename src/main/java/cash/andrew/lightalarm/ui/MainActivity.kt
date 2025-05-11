package cash.andrew.lightalarm.ui

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cash.andrew.lightalarm.BuildConfig
import cash.andrew.lightalarm.R
import cash.andrew.lightalarm.data.*
import cash.andrew.lightalarm.databinding.ActivityMainBinding
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var alarmManager: AlarmManager
    @Inject lateinit var alarmKeeper: AlarmKeeper
    @Inject lateinit var alarmAdapter: AlarmRecyclerAdapter
    @Inject lateinit var alarmScheduler: AlarmScheduler
    @Inject lateinit var lightController: LightController
    @Inject lateinit var notificationManager: NotificationManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        if (!lightController.hasFlashLight && !BuildConfig.DEBUG) {
            binding.mainActivityContainer.displayedChildId = binding.mainNoLights.id
            binding.fab.hide()
            return
        }

        handleNotificationPermission()
        handleFullScreenIntent()
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_required))
                    .setMessage(getString(R.string.schedule_alarm_permanently_denied_message))
                    .setPositiveButton(R.string.action_settings) { _, _ ->
                        // Open the app's settings.
                        val intent = Intent().apply {
                            action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            data = Uri.fromParts("package", packageName, null)
                        }
                        startActivity(intent)
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            } else {
                val localTime = LocalTime.now()
                TimePickerDialog(this, { _, hour, minute ->

                    val alarm = Alarm(
                        time = LocalTime.of(hour, minute)
                    )

                    alarmKeeper.addAlarm(alarm)
                    alarmScheduler.scheduleNextAlarm()
                    alarmAdapter.addAlarm(alarm)

                    showAlarmList()

                }, localTime.hour, localTime.minute, DateFormat.is24HourFormat(this)).show()
            }
        }
    }

    /** Shows alarm list or the no alarms set up message depending on if there are alarms. */
    private fun showAlarmList() {
        binding.mainActivityContainer.displayedChildId = if (alarmKeeper.hasAlarms) {
            binding.alarmsList.id
        } else binding.mainNoAlarmsSetup.id
    }

    private fun handleNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val request = permissionsBuilder(Manifest.permission.POST_NOTIFICATIONS).build()

        request.send { result ->
            when {
                result.anyPermanentlyDenied() -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_required))
                        .setMessage(getString(R.string.notification_permanently_denied_message))
                        .setPositiveButton(R.string.action_settings) { _, _ ->
                            // Open the app's settings.
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", packageName, null)
                            }
                            startActivity(intent)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
                result.anyShouldShowRationale() -> {
                    AlertDialog.Builder(this)
                        .setTitle(R.string.permission_required)
                        .setMessage(getString(R.string.notification_permission_request_message))
                        .setPositiveButton(getString(R.string.request_again)) { _, _ ->
                            request.send()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
                }
            }
        }
    }

    private fun handleFullScreenIntent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE || notificationManager.canUseFullScreenIntent()) return

        AlertDialog.Builder(this)
            .setTitle(R.string.permission_required)
            .setMessage(getString(R.string.notification_full_screen_message_request))
            .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                startActivity(Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    .addFlags(FLAG_ACTIVITY_NEW_TASK))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
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

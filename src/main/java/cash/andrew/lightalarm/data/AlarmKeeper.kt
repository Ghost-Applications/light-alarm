package cash.andrew.lightalarm.data

import androidx.datastore.core.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface AlarmKeeper {
    suspend fun getAlarmById(id: UUID): Alarm?
    suspend fun addAlarm(alarm: Alarm)
    suspend fun updateAlarm(newAlarm: Alarm)
    suspend fun removeAlarm(alarm: Alarm)
    suspend fun alarms(): List<Alarm>
    suspend fun hasAlarms(): Boolean
}

@Singleton
class DefaultAlarmKeeper @Inject constructor(
    private val alarmStore: DataStore<List<Alarm>>
): AlarmKeeper {

    override suspend fun getAlarmById(id: UUID): Alarm? = this.alarms().find { it.id == id }

    override suspend fun addAlarm(alarm: Alarm) {
        val alarms = this.alarms().toMutableList()
        alarms.add(alarm)
        updateAlarms(alarms)
    }

    override suspend fun updateAlarm(newAlarm: Alarm) {
        val alarms = this.alarms().toMutableList()
        alarms.removeIf { newAlarm.id == it.id }
        alarms.add(newAlarm)
        updateAlarms(alarms)
    }

    override suspend fun removeAlarm(alarm: Alarm) {
        val alarms = this.alarms().toMutableList()
        alarms.removeIf { alarm.id == it.id }
        updateAlarms(alarms)
    }

    override suspend fun alarms(): List<Alarm> {
        return withContext(Dispatchers.IO) {
            alarmStore.data.firstOrNull() ?: emptyList()
        }
    }

    override suspend fun hasAlarms(): Boolean {
        return this.alarms().isNotEmpty()
    }

    private suspend fun updateAlarms(alarms: List<Alarm>) {
        val sortedList = alarms.sortedBy { it.time }
        alarmStore.updateData {
            sortedList
        }
    }
}

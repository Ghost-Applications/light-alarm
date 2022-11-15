package cash.andrew.lightalarm.data

import io.paperdb.Book
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_KEY = "alarms"

interface AlarmKeeper {
    fun getAlarmById(id: UUID): Alarm?
    fun addAlarm(alarm: Alarm)
    fun updateAlarm(newAlarm: Alarm)
    fun removeAlarm(alarm: Alarm)
    val alarms: List<Alarm>
    val hasAlarms: Boolean
}

@Singleton
class DefaultAlarmKeeper @Inject constructor(
    private val alarmBook: Book
): AlarmKeeper {

    override fun getAlarmById(id: UUID): Alarm? = alarms.find { it.id == id }

    override fun addAlarm(alarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        alarms.add(alarm)
        updateAlarms(alarms)
    }

    override fun updateAlarm(newAlarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        alarms.removeIf { newAlarm.id == it.id }
        alarms.add(newAlarm)
        updateAlarms(alarms)
    }

    override fun removeAlarm(alarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        alarms.removeIf { alarm.id == it.id }
        updateAlarms(alarms)
    }

    override val alarms: List<Alarm> get() = alarmBook.read(COLLECTION_KEY, listOf()) ?: listOf()

    override val hasAlarms: Boolean get() = alarms.isNotEmpty()

    private fun updateAlarms(alarms: List<Alarm>) {
        val sortedList = alarms.sortedBy { it.time }
        alarmBook.write(COLLECTION_KEY, sortedList)
    }
}

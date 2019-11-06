package cash.andrew.lightalarm.data

import io.paperdb.Book
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_KEY = "alarms"

interface AlarmKeeper {
    fun addAlarm(alarm: Alarm)
    fun updateAlarm(newAlarm: Alarm)
    fun removeAlarm(alarm: Alarm)
    val alarms: List<Alarm>
    val hasAlarms: Boolean
}

@Singleton
class DefaultAlarmKeeper @Inject constructor(private val alarmBook: Book): AlarmKeeper {

    override fun addAlarm(alarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        alarms.add(alarm)
        updateAlarms(alarms)
    }

    override fun updateAlarm(newAlarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        val originalAlarm = alarms.find { it.id == newAlarm.id }
        alarms.remove(originalAlarm)
        alarms.add(newAlarm)
        updateAlarms(alarms)
    }

    override fun removeAlarm(alarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        alarms.remove(alarm)
        updateAlarms(alarms)
    }

    override val alarms: List<Alarm> get() = alarmBook.read(COLLECTION_KEY, listOf())

    override val hasAlarms: Boolean get() = alarms.isNotEmpty()

    private fun updateAlarms(alarms: List<Alarm>) {
        val sortedList = alarms.sortedBy { it.time }
        alarmBook.write(COLLECTION_KEY, sortedList)
    }
}

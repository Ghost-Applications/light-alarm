package cash.andrew.lightalarm

import io.paperdb.Book
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_KEY = "alarms"

@Singleton
class AlarmKeeper @Inject constructor(private val alarmBook: Book) {

    fun addAlarm(alarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        alarms.add(alarm)
        alarmBook.write(COLLECTION_KEY, alarms)
    }

    fun removeAlarm(alarm: Alarm) {
        val alarms = this.alarms.toMutableList()
        alarms.remove(alarm)
        alarmBook.write(COLLECTION_KEY, alarms)
    }

    val alarms: List<Alarm> get() = alarmBook.read(COLLECTION_KEY, listOf())

    val hasAlarms: Boolean get() = alarms.isNotEmpty()
}

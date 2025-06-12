package cash.andrew.lightalarm.data

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.*
import java.util.*

// Tuesday Jan 1 2019 and 12:00:30pm
private val DEFAULT_CLOCK = Clock.fixed(
        LocalDateTime.of(2019, 1, 1, 12, 0, 30).toInstant(ZoneOffset.UTC),
        ZoneOffset.UTC
)

class AlarmSchedulerTest {
    @Test
    fun `should return null when there are no alarms`() = runBlocking {
        val alarmManager = object : LightAlarmAlarmManager by stub() { }

        val alarmKeeper = object : AlarmKeeper by stub() {
            override suspend fun alarms(): List<Alarm> = emptyList()
        }

        val classUnderTest = AlarmScheduler(
                application = mock(),
                alarmManager = alarmManager,
                alarmKeeper = alarmKeeper,
                clock = DEFAULT_CLOCK
        )

        val result = classUnderTest.nextAlarmTime()

        assertThat(result).isNull()
    }

    @Test
    fun `should return null when no alarms enabled`() = runBlocking {
        val alarms = listOf(
                Alarm(time = LocalTime.of(10, 0), enabled = false),
                Alarm(time = LocalTime.MIDNIGHT, enabled = false),
                Alarm(time = LocalTime.of(18, 0), enabled = false)
        )

        val alarmKeeper = object : AlarmKeeper by stub() {
            override suspend fun alarms(): List<Alarm> {
                return alarms
            }
        }

        val alarmManager = object : LightAlarmAlarmManager by stub() { }

        val classUnderTest = AlarmScheduler(
                application = mock(),
                alarmManager = alarmManager,
                alarmKeeper = alarmKeeper,
                clock = DEFAULT_CLOCK
        )

        val result = classUnderTest.nextAlarmTime()

        assertThat(result).isNull()
    }

    @Test
    fun `should return the closest date to clock time of non-repeating alarms`() = runBlocking {
        val expectedId = UUID.fromString("e7679438-c77d-4aed-aeb0-1f876c2599f5")

        val alarms = listOf(
                Alarm(time = LocalTime.of(10, 0)),
                Alarm(time = LocalTime.MIDNIGHT),
                Alarm(
                    time = LocalTime.of(18, 0),
                    id = expectedId
                )
        )

        val alarmKeeper = object : AlarmKeeper by stub() {
            override suspend fun alarms(): List<Alarm> = alarms
        }

        val classUnderTest = AlarmScheduler(
                application = mock(),
                alarmManager = stub(),
                alarmKeeper = alarmKeeper,
                clock = DEFAULT_CLOCK
        )

        val (resultTime, resultId) = checkNotNull(classUnderTest.nextAlarmTime())

        assertThat(resultTime).isEqualTo(
            ZonedDateTime.of(2019, 1, 1, 18, 0, 0, 0, ZoneOffset.UTC)
        )

        assertThat(resultId).isEqualTo(expectedId)
    }

    @Test
    fun `should return the closest date to clock time of repeating alarms`() = runBlocking {
        val expectedId = UUID.fromString("e7679438-c77d-4aed-aeb0-1f876c2599f5")

        val weekDays = EnumSet.complementOf(EnumSet.of(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY))

        val alarms = listOf(
            Alarm(
                time = LocalTime.of(10, 0),
                repeat = true,
                days = weekDays
            ),
            Alarm(
                time = LocalTime.MIDNIGHT,
                repeat = true,
                days = weekDays
            ),
            Alarm(
                repeat = true,
                time = LocalTime.of(18, 0),
                id = expectedId,
                days = weekDays
            )
        )

        val alarmKeeper = object : AlarmKeeper by stub() {
            override suspend fun alarms(): List<Alarm> = alarms
        }

        val classUnderTest = AlarmScheduler(
            application = mock(),
            alarmManager = stub(),
            alarmKeeper = alarmKeeper,
            clock = DEFAULT_CLOCK
        )

        val (resultTime, resultId) = checkNotNull(classUnderTest.nextAlarmTime())

        assertThat(resultTime).isEqualTo(
            ZonedDateTime.of(2019, 1, 1, 18, 0, 0, 0, ZoneOffset.UTC)
        )

        assertThat(resultId).isEqualTo(expectedId)
    }
}

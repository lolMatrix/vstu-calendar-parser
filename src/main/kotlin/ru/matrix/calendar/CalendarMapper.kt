package ru.matrix.calendar

import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.TimeZone
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.component.VTimeZone
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.ProdId
import net.fortuna.ical4j.model.property.RRule
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion
import net.fortuna.ical4j.transform.recurrence.Frequency
import net.fortuna.ical4j.util.RandomUidGenerator
import net.fortuna.ical4j.util.UidGenerator
import ru.matrix.entity.Group
import ru.matrix.entity.Lesson
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class CalendarMapper(
    private val group: Group
) {
    fun mapToICal(): Calendar {
        val year = LocalDate.now().year
        val calendar = Calendar().apply {
            add<PropertyContainer>(ProdId("-//vsru//v0.1-snapshot"))
            add<PropertyContainer>(ImmutableVersion.VERSION_2_0)
            add<PropertyContainer>(ImmutableCalScale.GREGORIAN)
        }
        val registry = TimeZoneRegistryFactory.getInstance().createRegistry()
        val timezone: TimeZone = registry.getTimeZone("Europe/Moscow")
        val tz: VTimeZone = TimeZoneRegistryFactory.getInstance()
            .createRegistry()
            .getTimeZone("Europe/Moscow").vTimeZone
        val ug = RandomUidGenerator()
        group.lessons.forEach { day ->
            val date = LocalDate.of(year, day.day.firstMonth, day.day.firstDay)
            day.lessonsList.forEach { lesson: Lesson ->
                calendar.add<ComponentContainer<CalendarComponent>>(
                    lesson.toEvent(date, tz, ug, timezone.toZoneId())
                )
            }
        }
        return calendar
    }

    private fun Lesson.toEvent(
        date: LocalDate,
        timeZone: VTimeZone,
        uidGenerator: UidGenerator,
        zoneId: ZoneId
    ): VEvent {
        val times = lessonTime.map {
            listOf(it.startTime, it.endTime)
        }.flatten()
        val start = LocalDateTime.of(date, times.first())
        val end = LocalDateTime.of(date, times.last())
        return VEvent(start, end, name)
            .withProperty(
                Description("Ведет $teacher в аудитории $auditory")
            )
            .withProperty(timeZone.timeZoneId.get())
            .withProperty(uidGenerator.generateUid())
            .withProperty(
                RRule(
                    Recur.Builder<Instant>().frequency(Frequency.WEEKLY)
                        .interval(2)
                        .dayList(WeekDay.getWeekDay(GregorianCalendar.from(start.atZone(zoneId))))
                        .build()
                )
            )
            .getFluentTarget()
    }
}
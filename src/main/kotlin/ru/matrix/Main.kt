package ru.matrix

import net.fortuna.ical4j.data.CalendarOutputter
import ru.matrix.calendar.CalendarMapper
import ru.matrix.files.WorkbookProcessor
import ru.matrix.mapper.ContentMapper
import java.io.FileOutputStream

fun main(vararg args: String) {
    val (header, content) = WorkbookProcessor(args.first()).open()
    val groups = ContentMapper(header, content).map()
    println("Выбирай:")
    groups.forEachIndexed { index, group ->
      println("$index: ${group.name}")
    }
    val groupIndex = readln().let {
        Integer.parseInt(it)
    }
    val fileOutputStream = FileOutputStream(args.last() + "/${groups[groupIndex].name}.ics")
    val calendarOutputter = CalendarOutputter()
    calendarOutputter.output(CalendarMapper(groups[groupIndex]).mapToICal(), fileOutputStream)
}
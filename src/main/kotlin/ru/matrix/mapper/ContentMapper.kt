package ru.matrix.mapper

import ru.matrix.entity.*
import ru.matrix.util.isUpperCase
import ru.matrix.util.toNumber

class ContentMapper(
    private val header: List<Pair<Int, String>>,
    private val content: Array<Array<Pair<Pair<Int, Int>, String>>>,
    private val dateTimeParser: DateTimeParser = DateTimeParser(content)
) {
    fun map(): List<Group> {
        val (firstMonthIndex, firstMonthName) = header.first { it.first <= 5 }
        val firstMonthNumber = firstMonthName.toNumber()
        val startDates = dateTimeParser.getDates(firstMonthIndex)
        val days = content.windowByLesson(startDates, firstMonthNumber)
        val times = dateTimeParser.getTimes()
        return header.asSequence().filter { it.first > 5 }.map { group ->
            Group(
                name = group.second,
                lessons = days.map { (day, lessonsRow) ->
                    LessonDay(
                        day = day,
                        lessonsList = lessonsRow.map { lessons ->
                            lessons.filter { lesson ->
                                lesson.inRowRange(group.first)
                            }.map { lesson ->
                                lesson.apply {
                                    lessonTime.addAll(
                                        times.filter {
                                            lesson.inColRange(it.first)
                                        }.mapNotNull { AcademicTime.build(it.second) }
                                    )
                                }
                            }
                        }.flatten()
                    )
                }
            )
        }.toList()
    }

    private fun Array<Array<Pair<Pair<Int, Int>, String>>>.windowByLesson(
        firstDates: List<List<Int>>,
        firstMonthNumber: Int,
    ): MutableMap<DayOfWeek, List<List<Lesson>>> {
        val mutableContents = toMutableList()
        val dayNames = dateTimeParser.getDays()
        val lessonByDay = mutableMapOf<DayOfWeek, List<List<Lesson>>>()

        for (i in 1 .. dayNames.size) {
            val contentByDay =
                if(i != dayNames.size) mutableContents.takeWhile { content -> content[0].second != dayNames[i] }.toMutableList()
                else mutableContents
            val weekIndex = (i - 1) / 6
            val dayIndex = (i - 1) % 6
            val day = DayOfWeek(dayNames[i - 1], weekIndex + 1, firstDates[weekIndex][dayIndex], firstMonthNumber)
            val otherContent = contentByDay.getOtherContent()
            val rawLessons = contentByDay.getLessons().map {
                it.filter { !dayNames.contains(it.second) }
            }
            lessonByDay[day] = rawLessons.map {
                it.filter { (index, lessonName) -> index.first > 0 && !lessonName.contains(NUMERIC_REGEX) }
            }.filter { lessons ->
                lessons.isNotEmpty()
            }.toList().let {
                LessonParser(it).parseLessons(otherContent)
            }
            mutableContents.removeAll(
                contentByDay
            )
        }
        return lessonByDay
    }

    private fun MutableList<Array<Pair<Pair<Int, Int>, String>>>.getOtherContent() =
            filter { lessons -> !lessons.all { (_, lessonName) -> lessonName.isUpperCase() } }
            .toMutableList()

    private fun MutableList<Array<Pair<Pair<Int, Int>, String>>>.getLessons() = asSequence()
        .map { it.filter { (_, ln) -> !ln.contains(NUMERIC_REGEX) } }
        .filter { lessons ->
            lessons.isNotEmpty() && lessons.all { (_, lessonName) ->
                lessonName.isUpperCase() && !lessonName.contains(HANDWRITE_SIGN_SYMBOLS_REGEX)
            }
        }.toList()


    companion object {
        private val NUMERIC_REGEX = "[0-9]".toRegex()
        private val HANDWRITE_SIGN_SYMBOLS_REGEX = "[_:\"']".toRegex()
    }
}

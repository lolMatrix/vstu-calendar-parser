package ru.matrix.mapper

import ru.matrix.util.isUpperCase

class DateTimeParser(
    private val content: Array<Array<Pair<Pair<Int, Int>, String>>>
) {

    fun getDates(firstMonth: Int): List<List<Int>> = content
        .asSequence().map {
            it.filter {
                it.first.first == firstMonth
            }
        }.flatten().mapNotNull {
            try {
                Integer.parseInt(it.second)
            } catch (e: Exception) {
                null
            }
        }.filter {
            it in 1..14
        }.windowed(6, 6).toList()

    fun getTimes() = content.map { row ->
        row.map { content ->
            content.first.second to content.second.replace(SPACE_REGEX, "")
        }.filter { time ->
            time.second.matches(ACADEMIC_TIME_REGEX)
        }
    }.filter { time -> time.isNotEmpty() }.flatten()

    fun getDays() = content.filter { row ->
        row.any { (_, content) ->
            content.isUpperCase() && content.replace(SPACE_REGEX, "").matches(ONLY_LETTER_REGEX)
                    && content.length > 2
        }
    }.map { row -> row[0].second }.filter { name ->
        name.matches(ONLY_LETTER_REGEX) && name.isUpperCase()
    }

    companion object {
        private val ONLY_LETTER_REGEX = "^[А-я]*$".toRegex()
        private val ACADEMIC_TIME_REGEX = "^[0-9]?[0-9]-[0-9]?[0-9]\$".toRegex()
        private val SPACE_REGEX = "\\s".toRegex()
    }
}
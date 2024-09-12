package ru.matrix.mapper

import ru.matrix.entity.Lesson

class LessonParser(
    private val content: List<List<Pair<Pair<Int, Int>, String>>>
) {

    fun parseLessons(
        otherContent: List<Array<Pair<Pair<Int, Int>, String>>>,
    ): List<List<Lesson>> {
        val otherMutableContent = otherContent.toMutableList()
        return content.map { lessonsRow ->
            val parsedLessons = mutableListOf<Lesson>()
            val removeItemIndexes = mutableSetOf<Int>()
            val lessons = lessonsRow.map { (index, lessonName) ->
                val alreadyParsedLesson = parsedLessons.firstOrNull {
                    it.inRowRange(index.first)
                }
                if (alreadyParsedLesson == null) {
                    val rowWithTeacher = otherMutableContent.first { row ->
                        val entry = row.find { entry -> index.first == entry.first.first }
                        entry?.first?.first == index.first && !entry.second.contains(NUMERIC_REGEX)
                                && entry.second != lessonName
                    }
                    val rowIndex = otherMutableContent.indexOf(rowWithTeacher)
                    val teacherIndex = rowWithTeacher.indexOfFirst { entry -> index.first == entry.first.first }
                    val teacher = rowWithTeacher[teacherIndex].second
                    val auditory = rowWithTeacher[teacherIndex + 1].second
                    otherMutableContent.removeAt(rowIndex)
                    rowWithTeacher.toMutableList().also {
                        it.removeAt(teacherIndex + 1)
                        it.removeAt(teacherIndex)
                        otherMutableContent.add(rowIndex, it.toTypedArray())
                    }
                    val (firstColumIndex, lastColumIndex) = sortIndexes(
                        firstIndex = index.second,
                        lastIndex = rowWithTeacher[teacherIndex + 1].first.second
                    )

                    val lesson = Lesson(
                        name = lessonName,
                        teacher = teacher,
                        auditory = auditory,
                        rowIndexes = index.first until rowWithTeacher[teacherIndex + 1].first.first,
                        columnIndexes = firstColumIndex..lastColumIndex,
                    )
                    parsedLessons.add(0, lesson)
                    lesson
                } else {
                    alreadyParsedLesson.copy(name = lessonName)
                }
            }
            removeItemIndexes
                .filter { i -> i >= otherMutableContent.size }
                .forEach { i -> otherMutableContent.removeAt(i) }
            lessons
        }
    }

    private fun sortIndexes(firstIndex: Int, lastIndex: Int): Pair<Int, Int> {
        if (firstIndex > lastIndex) {
            return lastIndex to firstIndex
        }
        return firstIndex to lastIndex
    }

    companion object {
        private val NUMERIC_REGEX = "[0-9]".toRegex()
    }
}
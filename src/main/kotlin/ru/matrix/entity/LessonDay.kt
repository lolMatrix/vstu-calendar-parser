package ru.matrix.entity

data class LessonDay(
    val day: DayOfWeek,
    val lessonsList: List<Lesson>
)
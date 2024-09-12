package ru.matrix.entity

data class Group(
    val name: String,
    val lessons: List<LessonDay>
)
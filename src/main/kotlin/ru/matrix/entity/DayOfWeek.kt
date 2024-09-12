package ru.matrix.entity

data class DayOfWeek(
    val dayName: String,
    val week: Int,
    val firstDay: Int,
    val firstMonth: Int = 9
)
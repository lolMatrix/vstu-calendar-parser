package ru.matrix.entity

data class Lesson(
    val name: String,
    val teacher: String,
    val auditory: String,
    val lessonTime: MutableSet<AcademicTime> = mutableSetOf(),
    private val rowIndexes: IntRange,
    private val columnIndexes: IntRange
) {
    fun inRowRange(i: Int) = rowIndexes.contains(i)
    fun inColRange(i: Int) = columnIndexes.contains(i)
}

package ru.matrix.entity

import java.time.LocalTime

data class AcademicTime private constructor(
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    companion object {
        fun build(time: String) = TIME_MAP[time]?.let { (start, end) ->
            AcademicTime(
                startTime = start,
                endTime = end,
            )
        }

        private val TIME_MAP = mapOf(
            "1-2" to (LocalTime.of(8, 30) to LocalTime.of(10, 0)),
            "3-4" to (LocalTime.of(10, 10) to LocalTime.of(11, 40)),
            "5-6" to (LocalTime.of(11, 50) to LocalTime.of(13, 20)),
            "7-8" to (LocalTime.of(13, 40) to LocalTime.of(15, 10)),
            "9-10" to (LocalTime.of(15, 20) to LocalTime.of(16, 50)),
            "11-12" to (LocalTime.of(17, 0) to LocalTime.of(18, 30)),
            "13-14" to (LocalTime.of(18, 35) to LocalTime.of(20, 0)),
            "15-16" to (LocalTime.of(20, 5) to LocalTime.of(21, 30)),
        )
    }
}

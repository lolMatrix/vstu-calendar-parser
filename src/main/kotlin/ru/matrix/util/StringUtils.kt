package ru.matrix.util

fun String.isUpperCase() = this == uppercase()
fun String.toNumber() = when (this.lowercase()) {
    "сентябрь" -> 9
    else -> 1
}
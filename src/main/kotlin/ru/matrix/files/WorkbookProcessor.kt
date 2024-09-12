package ru.matrix.files

import jxl.Sheet
import jxl.read.biff.WorkbookParser
import ru.matrix.util.isUpperCase
import java.io.File

class WorkbookProcessor(
    private val path: String
) {
    fun open() = File(path).inputStream().let { inputStream ->
        WorkbookParser.getWorkbook(inputStream).getSheet(0)
    }.let {
        getHeader(it) to getContent(it)
    }

    private fun getHeader(sheet: Sheet) = (0 .. HEADER_ROW_INDEX).map {
        sheet.getRow(it).mapIndexed { i, cell ->
            i to cell.contents
        }.filter { (_, cell) -> cell.isNotBlank() }
    }.last { it.isNotEmpty() && it.any { it.second.replace(SPACE_REGEX, "").matches(LETTER_REGEX) && !it.second.isUpperCase()} }

    private fun getContent(sheet: Sheet) = (NAME_COL_INDEX  until sheet.rows).filter { i ->
        sheet.getRow(i) != null
    }.map { row ->
        sheet.getRow(row).mapIndexed { col, cell ->
            col to row to cell.contents
        }.filter { (_, content) ->
            content.isNotBlank()
        }.toTypedArray()
    }.filter { it.isNotEmpty() }.toTypedArray()

    private companion object {
        private const val HEADER_ROW_INDEX = 8
        private const val NAME_COL_INDEX = 5
        private val SPACE_REGEX = "\\s".toRegex()
        private val LETTER_REGEX = "[А-я]*".toRegex()
    }
}
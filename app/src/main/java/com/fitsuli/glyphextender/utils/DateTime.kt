package com.fitsuli.glyphextender.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Created by Dmitry Danilyuk at 21.08.2024
 */
fun transformDate(
    millis: Long,
    pattern: String = "dd.MM.yyyy HH:mm",
): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(pattern))
}

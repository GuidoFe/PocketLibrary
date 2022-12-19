package com.guidofe.pocketlibrary.data.local.library_db.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

object LocalDateConverter {
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(dateString) }
    }

    @TypeConverter
    fun fromDate(date: LocalDate?): String? {
        return date?.toString()
    }
}

object InstantConverter {
    @TypeConverter
    fun toInstant(milli: Long?): Instant? {
        return milli?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}
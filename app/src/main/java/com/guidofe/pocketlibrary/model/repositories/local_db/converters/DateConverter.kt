package com.guidofe.pocketlibrary.model.repositories.local_db.converters

import androidx.room.TypeConverter
import java.sql.Date

object DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}
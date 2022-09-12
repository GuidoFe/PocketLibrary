package com.guidofe.pocketlibrary.model.repositories.local_db.converters

import android.net.Uri
import androidx.room.TypeConverter
import java.sql.Date

object UriConverter {
    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(uriString) }
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }
}
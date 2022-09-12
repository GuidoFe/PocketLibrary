package com.guidofe.pocketlibrary.model.repositories.local_db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(indices = [Index(value = ["name"], unique = true)])
@Parcelize
data class Author(
    @PrimaryKey(autoGenerate = true) val authorId: Long,
    @ColumnInfo val name: String,
): Parcelable {
    override fun toString(): String {
        return name
    }
}
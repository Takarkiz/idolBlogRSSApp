package com.takhaki.idolbloglistener.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    var id: String,
    val title: String,
    val content: String,
    val date: Date,
    val url: String,
    val listKey: Int,
    var isRead: Boolean,
    var isFavorite: Boolean
)

class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}
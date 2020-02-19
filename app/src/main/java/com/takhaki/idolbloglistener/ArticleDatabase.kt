package com.takhaki.idolbloglistener

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.takhaki.idolbloglistener.Model.Article
import com.takhaki.idolbloglistener.Model.Converters

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile
        private var instance: ArticleDatabase? = null

        fun getInstance(context: Context): ArticleDatabase =
            instance ?: synchronized(this) {
                Room.databaseBuilder(context, ArticleDatabase::class.java, "idolblogs.db").build()
            }
    }
}
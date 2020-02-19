package com.takhaki.idolbloglistener

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.takhaki.idolbloglistener.Model.Article

@Dao
interface ArticleDao {
    @Query("select * from articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("select * from articles where id = :id")
    fun getArticle(id: String): Article

    @Query("select * from articles where url = :url")
    fun getArticles(url: String): Article?

    @Query("select * from articles where listKey = :listKey")
    fun getArticlesFromListKey(listKey: Int): LiveData<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(article: Article)

    @Update
    fun update(article: Article)

    @Delete
    fun delete(article: Article)

    @Query("delete from articles")
    fun deleteAll()
}
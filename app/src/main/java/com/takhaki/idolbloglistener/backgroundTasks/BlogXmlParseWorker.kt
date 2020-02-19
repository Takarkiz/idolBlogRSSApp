package com.takhaki.idolbloglistener.backgroundTasks

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.takhaki.idolbloglistener.ArticleDatabase
import com.takhaki.idolbloglistener.Model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.mcsoxford.rss.RSSFeed
import org.mcsoxford.rss.RSSReader
import java.util.*

class BlogXmlParseWorker(val context: Context, param: WorkerParameters) : Worker(context, param) {

    override fun doWork(): Result {

        val xmlUri = inputData.getString("Xml")

        val reader = RSSReader()
        val feed: RSSFeed = reader.load(xmlUri)
        feed.items.forEach { item ->
            val uuid = UUID.randomUUID().toString()
            val article = Article(
                id = uuid,
                title = item.title,
                content = removeNewLine(item.description),
                url = item.link.toString(),
                date = item.pubDate,
                listKey = 0,
                isRead = false,
                isFavorite = false
            )
            GlobalScope.launch(Dispatchers.IO) {
                saveArticle(article)
            }
        }

        val outputData = Data.Builder()
            .putBoolean("fetchState", true)
            .build()

        return Result.success(outputData)
    }

    private fun removeNewLine(value: String): String {
        return value.replace("\n", "")
    }

    private fun saveArticle(article: Article) {
        val oldItem: Article? =
            ArticleDatabase.getInstance(context).articleDao().getArticles(article.url)
        if (oldItem == null) {
            // まだ存在していない場合
            ArticleDatabase.getInstance(context).articleDao().insert(article)
        }
    }

//    private fun rssFeedToArticle(feed: RSSFeed) : Single<List<Article>> {
//        val items = feed.items
//
//        return Single.create<List<Article>> { emitter ->
//            val articles = mutableListOf<Article>()
//
//            items.forEach { item ->
//                articles.add(Article(title = item.title, content = item.content, date = item.pubDate, isRead = false))
//            }
//
//            emitter.onSuccess(articles)
//        }
//    }

}
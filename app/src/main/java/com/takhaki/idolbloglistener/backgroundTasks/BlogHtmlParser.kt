package com.takhaki.idolbloglistener.backgroundTasks

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class BlogHtmlParser {

    companion object {
        fun getImgUrl(url: String): Single<String> {
            return Single.create<Document> {
                val document = Jsoup.connect(url).get()
                it.onSuccess(document)
            }.flatMap {
                val img = it.select("_25ofiX1u._2RD6rU2G._1ri7IG53")
                val imgUrl = img.select("_x98XiaJp._20tRE5Kv._2SgAzCXP").attr("abs:href")
                Single.just(imgUrl)
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }

}
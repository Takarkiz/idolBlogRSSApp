package com.takhaki.idolbloglistener

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.takhaki.idolbloglistener.Model.Article
import com.takhaki.idolbloglistener.backgroundTasks.BlogXmlParseWorker
import kotlinx.coroutines.flow.callbackFlow

class BlogListViewModel(application: Application) : AndroidViewModel(application) {

    val articleList: LiveData<List<Article>> =
        ArticleDatabase.getInstance(appContext).articleDao().getArticlesFromListKey(0)

    val loadingState: LiveData<Boolean>
        get() = _loadingState
    private var _loadingState = MutableLiveData<Boolean>()

    fun startXmlWorker() {
        _loadingState.value = true

        val data = Data.Builder()
            .putString("Xml", "http://rssblog.ameba.jp/beyooooonds-rfro/rss20.xml")
            .build()

        val request = OneTimeWorkRequestBuilder<BlogXmlParseWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).setInputData(data)
            .build()

        val workManager = WorkManager.getInstance()
        workManager.beginUniqueWork("XmlParser", ExistingWorkPolicy.REPLACE, request).enqueue()
        workManager.getWorkInfoByIdLiveData(request.id).observeForever {
            it ?: return@observeForever
            if (it.state.isFinished) {
                _loadingState.value = false
            }
        }
    }

    fun goToBlog(context: Context, url: Uri) {
        val customTabIntent = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            addDefaultShareMenuItem()
            setShowTitle(true)
        }.build()

        customTabIntent.launchUrl(context, url)
    }

    private val appContext: Context get() = getApplication()

}

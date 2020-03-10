package com.takhaki.idolbloglistener.screen.main.view_model

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.work.*
import com.takhaki.idolbloglistener.ArticleDatabase
import com.takhaki.idolbloglistener.Model.Article
import com.takhaki.idolbloglistener.R
import com.takhaki.idolbloglistener.backgroundTasks.BlogXmlParseWorker
import com.takhaki.idolbloglistener.screen.main.BlogListNavigator
import com.takhaki.idolbloglistener.screen.main.BlogListViewModelBase
import java.lang.ref.WeakReference

class BlogListViewModel(
    application: Application,
    private val navigator: BlogListNavigator
) : BlogListViewModelBase(application) {

    override fun activity(activity: AppCompatActivity) {
        navigator.weakActivity = WeakReference(activity)
    }

    override fun didTapBlogItem(url: String) {
        navigator.toWebBrowser(url)
    }

    override fun didTapPlusButton() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val articleList: LiveData<List<Article>> =
        ArticleDatabase.getInstance(
            appContext
        ).articleDao().getArticlesFromListKey(0)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {

    }

    fun startXmlWorker() {
        _isLoadingBlogList.value = true

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
                _isLoadingBlogList.value = false
            }
        }
    }

    fun goToBlog(context: Context, url: Uri) {
        val customTabIntent = CustomTabsIntent.Builder().apply {
            setToolbarColor(ContextCompat.getColor(context,
                R.color.colorPrimary
            ))
            addDefaultShareMenuItem()
            setShowTitle(true)
        }.build()

        customTabIntent.launchUrl(context, url)
    }

    override val isLoadingBlogList: LiveData<Boolean> get() = _isLoadingBlogList
    override val blogItems: LiveData<List<Article>>
        get() = ArticleDatabase.getInstance(
            appContext
        ).articleDao().getArticlesFromListKey(0)

    // Private
    private val appContext: Context get() = getApplication()
    private val _isLoadingBlogList: MutableLiveData<Boolean> = MutableLiveData()
    private val _blogItems: MutableLiveData<List<Article>> = MutableLiveData()

}

package com.takhaki.idolbloglistener.screen

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import com.takhaki.idolbloglistener.Model.Article

abstract class BlogListViewModelBase(
    application: Application
) : AndroidViewModel(application),
    LifecycleObserver,
    BlogListViewModelContract


interface BlogListViewModelContract {

    /**
     * 実行中インスタンス
     */
    fun activity(activity: AppCompatActivity)


    /**
     * ブログ読み込みローディング
     */
    val isLoadingBlogList: LiveData<Boolean>


    /**
     *ブログのアイテム
     */
    val blogItems: LiveData<List<Article>>


    /**
     * ブログアイテムタップ時
     */
    fun didTapBlogItem()


    /**
     *FABタップ時
     */
    fun didTapPlusButton()
}
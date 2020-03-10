package com.takhaki.idolbloglistener.module

import com.takhaki.idolbloglistener.screen.main.BlogListViewModelBase
import com.takhaki.idolbloglistener.screen.main.view_model.BlogListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {

    viewModel<BlogListViewModelBase> { BlogListViewModel(androidApplication(), get()) }
}
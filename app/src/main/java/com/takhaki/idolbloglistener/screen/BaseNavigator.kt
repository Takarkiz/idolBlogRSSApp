package com.takhaki.idolbloglistener.screen

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.takhaki.idolbloglistener.R
import java.lang.ref.WeakReference

open class BaseNavigator {

    var weakActivity: WeakReference<AppCompatActivity>? = null

    fun toWebBrowser(url: String) {
        weakActivity?.get()?.let { activity ->
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(
                ContextCompat.getColor(activity, R.color.colorPrimary))
                .build()
                .launchUrl(activity, Uri.parse(url))
        }
    }

    fun finish() {
        weakActivity?.get()?.finish()
    }

}
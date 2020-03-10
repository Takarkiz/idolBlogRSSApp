package com.takhaki.idolbloglistener.screen.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takhaki.idolbloglistener.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle("ホーム")

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(
            R.id.container,
            BlogListFragment.newInstance()
        )
        transaction.commit()

    }
}

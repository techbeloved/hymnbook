package com.techbeloved.hymnbook.hymnlisting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.techbeloved.hymnbook.R

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        supportFragmentManager.beginTransaction()
                .add(R.id.containerMain, HymnListingFragment())
                .commit()
    }
}

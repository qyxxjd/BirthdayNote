package com.classic.birthday.ui

import android.os.Bundle
import androidx.core.view.WindowCompat
import com.classic.birthday.R
import com.classic.birthday.databinding.ActivityMainBinding
import com.classic.birthday.ui.app.AppActivity
import com.classic.birthday.ui.edit.UserEditFragment
import com.classic.core.ui.vb.viewBinding

class MainActivity : AppActivity() {


    private val viewBinding by viewBinding(ActivityMainBinding::bind)
    override fun layout() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setSupportActionBar(viewBinding.toolbar)

        viewBinding.fab.setOnClickListener {
            UserEditFragment.start(fm())
        }
    }

    // override fun onCreateOptionsMenu(menu: Menu): Boolean {
    //     menuInflater.inflate(R.menu.menu_main, menu)
    //     return true
    // }
    // override fun onOptionsItemSelected(item: MenuItem): Boolean {
    //     return when (item.itemId) {
    //         R.id.action_settings -> true
    //         else -> super.onOptionsItemSelected(item)
    //     }
    // }
}
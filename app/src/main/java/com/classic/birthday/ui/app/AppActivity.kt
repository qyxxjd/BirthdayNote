@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.classic.birthday.ui.app

import android.os.Bundle
import android.view.MenuItem
import com.classic.core.ui.CoreActivity

/**
 * ViewBinding activity
 *
 * @author Classic
 * @version 2023/4/21 15:07
 */
abstract class AppActivity : CoreActivity() {
    open fun layout(): Int = 0
    // fun toolbar(): Toolbar? = findViewById(R.id.toolbar)
    override fun autoHideKeyboard(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout())
        // initToolbar()
    }
    // open fun canBack(): Boolean = false
    // open fun initToolbar() {
    //     toolbar()?.apply {
    //         setSupportActionBar(this)
    //         if (canBack()) {
    //             val actionBar = supportActionBar
    //             actionBar?.setDisplayHomeAsUpEnabled(true)
    //         }
    //     }
    // }
    open fun onBackMenuClick() { finish() }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (/*canBack() && */item.itemId == android.R.id.home) {
            onBackMenuClick()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
package com.classic.birthday.ui.main

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import com.classic.birthday.R
import com.classic.birthday.data.local.LocalDataSource
import com.classic.birthday.data.local.User
import com.classic.birthday.databinding.ActivityMainBinding
import com.classic.birthday.ui.app.AppActivity
import com.classic.birthday.ui.edit.UserEditActivity
import com.classic.birthday.util.DialogUtil
import com.classic.core.ext.ItemClickListener
import com.classic.core.ext.ItemLongClickListener
import com.classic.core.ext.STORAGE_PERMISSION
import com.classic.core.ext.applyLinearConfig
import com.classic.core.ext.async
import com.classic.core.ext.hasExternalPermission
import com.classic.core.ext.hasManageExternalPermission
import com.classic.core.ext.longToast
import com.classic.core.ui.vb.viewBinding

class MainActivity : AppActivity() {

    private var dataSource: LocalDataSource? = null
    private val list = mutableListOf<User>()
    private val userAdapter = UserAdapter()
    private val viewBinding by viewBinding(ActivityMainBinding::bind)
    override fun layout() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        dataSource = LocalDataSource.get(appContext)
        viewBinding.recyclerView.applyLinearConfig(adapter = userAdapter)
        viewBinding.fab.setOnClickListener { editUser() }
        userAdapter.itemClickListener = object : ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                editUser(list[position])
            }
        }
        userAdapter.itemLongClickListener = object : ItemLongClickListener {
            override fun onItemLongClick(view: View, position: Int): Boolean {
                deleteUser(list[position])
                return true
            }
        }
        checkManageExternalPermissions()

        // val nextBirthday = CalendarUtils.lunarToSolar(1990, 9, 12, false)
        // val nextBirthdayDate = LocalDate.of(nextBirthday[0], nextBirthday[1], nextBirthday[2])
        // Timber.tag("Birthday").e("阳历生日：$nextBirthdayDate")
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun editUser(user: User? = null) {
        UserEditActivity.start(this, user)
    }

    private fun deleteUser(user: User) {
        DialogUtil.custom(this, "友情提示", "是否删除本条数据?",
            "取消", "删除",
            listener = { _, which ->
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    dataSource?.delete(user)
                }
            })
    }

    private fun loadData() {
        async {
            // if (LocalApi.get().isFirst()) {
            //     val list = mutableListOf<User>()
            //     // list.add(User().apply {
            //     //     name = "续写经典"
            //     //     birthday = "1990-10-29 00:00:00".parse().time
            //     // })
            //     list.add(User().apply {
            //         name = "刘佳宜"
            //         birthday = "2016-01-19 19:06:00".parse().time
            //         photoResId = R.drawable.ic_photo_jy
            //     })
            //     list.add(User().apply {
            //         name = "刘若泠"
            //         birthday = "2022-11-08 10:58:00".parse().time
            //         photoResId = R.drawable.ic_photo_rl
            //     })
            //     dataSource?.addAll(list)
            //     LocalApi.get().saveIsFirst(false)
            // }
            dataSource?.queryAll() ?: mutableListOf()
        } ui {
            list.clear()
            list.addAll(it)
            userAdapter.replaceAll(list)
            viewBinding.recyclerView.adapter = userAdapter
        }
    }

    // 申请所有文件访问权限(Android 11及以上版本)
    private val requestAllFilesAccessLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (hasManageExternalPermission()) {
            checkExternalPermissions()
        } else {
            onUserDisagreeManageExternal()
        }
    }
    // Android 11以下版本使用普通存储权限
    private val storagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (appContext.hasExternalPermission()) {
            loadData()
        } else {
            onUserDisagreeManageExternal()
        }
    }
    // 检查管理存储设备权限，受影响的功能为：数据备份、数据恢复
    private fun checkManageExternalPermissions() {
        if (hasManageExternalPermission()) {
            checkExternalPermissions()
        } else {
            showManageExternalPermissionsDialog()
        }
    }
    // 检查存储权限，受影响的功能为：数据备份、数据恢复
    private fun checkExternalPermissions() {
        if (hasExternalPermission()) {
            loadData()
        } else {
            requestExternalPermissions()
        }
    }
    private fun showManageExternalPermissionsDialog() {
        DialogUtil.custom(this, "温馨提示", "相册功能需要使用存储卡管理权限", "取消", "去授权",
            listener = { _, which ->
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    requestManageExternalPermissions()
                } else {
                    onUserDisagreeManageExternal()
                }
            })
    }
    private fun requestManageExternalPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                requestAllFilesAccessLauncher.launch(
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                        it.data = Uri.parse("package:$packageName")
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun requestExternalPermissions() {
        storagePermissionLauncher.launch(STORAGE_PERMISSION)
    }
    // 用户拒绝管理存储设备权限
    private fun onUserDisagreeManageExternal() {
        longToast("用户拒绝管理存储设备权限，部分功能受限！")
    }
}
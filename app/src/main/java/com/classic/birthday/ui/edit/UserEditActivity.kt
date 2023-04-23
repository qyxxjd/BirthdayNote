package com.classic.birthday.ui.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import coil.load
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.classic.birthday.R
import com.classic.birthday.data.local.LocalDataSource
import com.classic.birthday.data.local.User
import com.classic.birthday.databinding.FragmentUserEditBinding
import com.classic.birthday.ui.app.AppActivity
import com.classic.birthday.ui.gallery.GalleryManage
import com.classic.core.ext.EMPTY
import com.classic.core.ext.KEY_SERIAL
import com.classic.core.ext.PATTERN_DATE
import com.classic.core.ext.applyFocus
import com.classic.core.ext.format
import com.classic.core.ext.ioTask
import com.classic.core.ext.onClick
import com.classic.core.ext.serializable
import com.classic.core.ext.text
import com.classic.core.ext.toast
import com.classic.core.ext.withUI
import com.classic.core.interfaces.SingleCallback
import com.classic.core.ui.vb.viewBinding
import java.io.File
import java.util.Calendar

/**
 * TODO
 *
 * @author LiuBin
 * @version 2023/4/23 10:55
 */
class UserEditActivity : AppActivity() {
    companion object {
        fun start(context: Context, user: User? = null) {
            val intent = Intent(context, UserEditActivity::class.java)
            if (null != user) intent.putExtra(KEY_SERIAL, user)
            context.startActivity(intent)
        }
    }

    private var isAdd = true
    private var user = User()
    private var dataSource: LocalDataSource? = null
    private val viewBinding by viewBinding(FragmentUserEditBinding::bind)
    override fun layout() = R.layout.fragment_user_edit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAdd = true
        dataSource = LocalDataSource.get(appContext)
        viewBinding.apply {
            dateLayout.editText?.onClick { onDatePicker() }
            dateLayout.setEndIconOnClickListener { onDatePicker() }
            menuChooseCover.onClick { onChooseCover() }
            menuResetCover.onClick { onResetCover() }
            menuSave.onClick { if (checkParams()) onSave() }
        }
        intent?.serializable<User>(KEY_SERIAL)?.let {
            user = it
            isAdd = false
            setupName(user.name)
            setupCover(user.photo)
        }
        setupDate(user.birthday)
    }

    private fun onDatePicker() {
        hideKeyboard()
        create(user.birthday, { date, _ ->
            val time = date.time
            user.birthday = time
            setupDate(time)
        }, false).show()
    }
    private fun setupDate(time: Long) {
        (viewBinding.dateLayout.editText)?.setText(time.format(PATTERN_DATE))
    }

    private fun setupName(name: String) {
        (viewBinding.nameLayout.editText)?.setText(name)
    }

    private fun onChooseCover() {
        GalleryManage.get().single(this, object : SingleCallback<String> {
            override fun onSingleResult(t: String) {
                user.photo = t
                setupCover(t)
            }
        })
    }
    private val cover = R.drawable.ic_default_photo
    private fun setupCover(path: String?) {
        if (path.isNullOrEmpty()) {
            viewBinding.cover.setImageResource(cover)
        } else {
            try {
                viewBinding.cover.load(File(path))
                // viewBinding.cover.load(File(path).toBitmap())
            } catch (e: Exception) {
                e.printStackTrace()
                toast("加载图片异常：${e.message}")
                viewBinding.cover.setImageResource(cover)
            }
        }
    }

    private fun onResetCover() {
        user.photo = EMPTY
        setupCover(EMPTY)
    }

    private fun checkParams(): Boolean {
        viewBinding.apply {
            user.name = nameLayout.editText.text()
            if (user.name.isEmpty()) {
                toast("请输入姓名")
                nameLayout.editText?.applyFocus()
                return false
            }
        }
        return true
    }
    private fun onSave() {
        ioTask {
            if (isAdd) dataSource?.add(user) else dataSource?.modify(user)
            withUI {
                toast("保存成功")
                onBackMenuClick()
            }
        }
    }

    /**
     * 创建日期选择控件
     *
     * @param time 默认时间
     * @param listener 监听器
     * @param useTime true:显示日期和时间，false:仅显示日期
     */
    private fun create(
        time: Long, listener: OnTimeSelectListener,
        @Suppress("SameParameterValue") useTime: Boolean = false
    ): TimePickerView {
        val activity = this
        val decorView: ViewGroup = activity.window.decorView.findViewById(android.R.id.content) as ViewGroup
        val type: BooleanArray = if (useTime)
            booleanArrayOf(true, true, true, true, true, false)
        else
            booleanArrayOf(true, true, true, false, false, false)
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = time
        return TimePickerBuilder(activity, listener)
            .setDecorView(decorView)
            .isCyclic(false)
            .setOutSideCancelable(false)
            .setType(type)
            .setDate(calendar)
            .build()
    }
}
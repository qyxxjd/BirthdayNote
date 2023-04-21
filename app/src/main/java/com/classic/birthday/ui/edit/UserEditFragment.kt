package com.classic.birthday.ui.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import coil.load
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.classic.birthday.R
import com.classic.birthday.data.local.User
import com.classic.birthday.databinding.FragmentUserEditBinding
import com.classic.birthday.ui.app.AppBottomSheetDialogFragment
import com.classic.birthday.ui.gallery.GalleryManage
import com.classic.core.ext.PATTERN_DATE
import com.classic.core.ext.format
import com.classic.core.ext.hideKeyboard
import com.classic.core.ext.onClick
import com.classic.core.ext.toast
import com.classic.core.interfaces.SingleCallback
import com.classic.core.ui.vb.viewBinding
import java.io.File
import java.util.Calendar


/**
 * 联系作者弹窗
 *
 * @author LiuBin
 * @date 2021/12/14 20:54
 */
class UserEditFragment : AppBottomSheetDialogFragment() {
    companion object {
        fun start(fm: FragmentManager) {
            UserEditFragment().showDialog(fm)
        }
    }

    private var user = User()
    private val viewBinding by viewBinding(FragmentUserEditBinding::bind)
    override fun layout() = R.layout.fragment_user_edit

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            dateLayout.editText?.onClick { onDatePicker() }
            dateLayout.setEndIconOnClickListener { onDatePicker() }
            menuChooseCover.onClick { onChooseCover() }
            menuResetCover.onClick { onResetCover() }
        }
    }

    // private fun loadData() {
    //     async {
    //         carName = api.getCarName()
    //         carCover = api.getCarCover()
    //         carBuyDate = api.getCarBuyDate()
    //     } ui {
    //         setupName(carName)
    //         setupDate(carBuyDate)
    //         setupCover(carCover)
    //     }
    // }

    private fun onDatePicker() {
        requireActivity().hideKeyboard()
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
        GalleryManage.get().single(requireActivity(), object : SingleCallback<String> {
            override fun onSingleResult(t: String) {
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
        // onCoverChanged(EMPTY)
    }


    /**
     * 创建日期选择控件
     *
     * @param time 默认时间
     * @param listener 监听器
     * @param useTime true:显示日期和时间，false:仅显示日期
     */
    private fun create(time: Long, listener: OnTimeSelectListener, useTime: Boolean = false): TimePickerView {
        val activity = requireActivity()
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
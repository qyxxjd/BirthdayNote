package com.classic.birthday.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.classic.birthday.R
import com.classic.birthday.data.local.User
import com.classic.birthday.databinding.ItemUserBinding
import com.classic.core.ext.PATTERN_DATE
import com.classic.core.ext.calculateDateDiff
import com.classic.core.ext.calendar
import com.classic.core.ext.day
import com.classic.core.ext.format
import com.classic.core.ext.month
import com.classic.core.ext.toast
import com.classic.core.ext.year
import com.classic.core.ui.adapter.ViewBindingAdapter
import com.classic.core.ui.adapter.ViewBindingHolder
import dev.utils.common.CalendarUtils
import java.io.File

/**
 * TODO
 *
 * @author LiuBin
 * @version 2023/4/23 10:07
 */
class UserAdapter : ViewBindingAdapter<User, ItemUserBinding>() {
    override fun createHolder(parent: ViewGroup, viewType: Int): ViewBindingHolder<ItemUserBinding> {
        val inflater = LayoutInflater.from(parent.context)
        return ViewBindingHolder(
            ItemUserBinding.inflate(inflater, parent, false)
        )
    }
    private val cover = R.drawable.ic_default_photo
    @SuppressLint("SetTextI18n")
    override fun onBind(binding: ItemUserBinding, item: User, position: Int) {
        binding.apply {
            title.text = item.name
            val ca = calendar(item.birthday)
            // 农历
            val lunar = CalendarUtils.solarToLunar(ca.year(), ca.month() + 1, ca.day())
            val month = CalendarUtils.getLunarMonthChinese(lunar[1], false)
            val day = CalendarUtils.getLunarDayChinese(lunar[2])
            birthday.text = "${item.birthday.format(PATTERN_DATE)}  农历$month$day"
            desc.text = "已出生${calculateDateDiff(item.birthday)}"

            if (item.photo.isEmpty()) {
                photo.setImageResource(cover)
            } else {
                try {
                    photo.load(File(item.photo))
                } catch (e: Exception) {
                    e.printStackTrace()
                    photo.context.toast("加载图片异常：${e.message} \n${item.photo}")
                    photo.setImageResource(cover)
                }
            }
        }
    }

}

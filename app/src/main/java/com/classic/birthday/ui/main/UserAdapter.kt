package com.classic.birthday.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.classic.birthday.R
import com.classic.birthday.data.local.User
import com.classic.birthday.databinding.ItemUserBinding
import com.classic.core.ext.PATTERN_DATE_TIME_SHORT
import com.classic.core.ext.applyHtml
import com.classic.core.ext.calculateDateDiff
import com.classic.core.ext.calendar
import com.classic.core.ext.day
import com.classic.core.ext.format
import com.classic.core.ext.month
import com.classic.core.ext.toHtmlColorText
import com.classic.core.ext.toast
import com.classic.core.ext.year
import com.classic.core.ui.adapter.ViewBindingAdapter
import com.classic.core.ui.adapter.ViewBindingHolder
import dev.utils.common.CalendarUtils
import timber.log.Timber
import java.io.File
import java.time.LocalDate

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
            val year = CalendarUtils.getLunarGanZhi(lunar[0])
            val month = CalendarUtils.getLunarMonthChinese(lunar[1], false)
            val day = CalendarUtils.getLunarDayChinese(lunar[2])
            birthday.text = "${item.birthday.format(PATTERN_DATE_TIME_SHORT)}  农历${year}年$month$day"
            val days = nextBirthdayDays(lunar)
            if (days > 0) {
                desc.applyHtml(
                    "已出生${calculateDateDiff(item.birthday).toString().toHtmlColorText("#00C0E4")}，距离下次生日还有${days.toString().toHtmlColorText("#00C0E4")}天"
                )
            } else {
                desc.applyHtml(
                    "已出生${calculateDateDiff(item.birthday).toString().toHtmlColorText("#00C0E4")}，生日快乐！"
                )
            }

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

    /**
     * 计算下一个生日距离当前的天数
     */
    private fun nextBirthdayDays(lunarBirthday: IntArray): Long {
        val now = LocalDate.now()
        // 今年阳历生日
        val nowBirthday = CalendarUtils.lunarToSolar(now.year, lunarBirthday[1], lunarBirthday[2], false)
        val nowBirthdayDate = LocalDate.of(nowBirthday[0], nowBirthday[1], nowBirthday[2])
        Timber.tag("Birthday").e("今年阳历生日：$nowBirthdayDate")
        // 今年阳历生日和当前时间差异的天数
        val diff = nowBirthdayDate.toEpochDay() - now.toEpochDay()
        return when {
            // 等于0，代表今天就是生日
            diff == 0L -> 0L
            // 小于0，代表今年的生日已经过了，计算下一年的差值
            diff < 0 -> {
                // 明年阳历生日
                val nextBirthday = CalendarUtils.lunarToSolar(now.year + 1, lunarBirthday[1], lunarBirthday[2], false)
                val nextBirthdayDate = LocalDate.of(nextBirthday[0], nextBirthday[1], nextBirthday[2])
                Timber.tag("Birthday").e("明年阳历生日：$nextBirthdayDate")
                nextBirthdayDate.toEpochDay() - now.toEpochDay()
            }
            // 大于0，代表今年的生日未过
            else -> diff
        }
    }

}

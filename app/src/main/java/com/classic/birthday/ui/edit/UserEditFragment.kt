package com.classic.birthday.ui.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.classic.birthday.R
import com.classic.birthday.databinding.FragmentUserEditBinding
import com.classic.birthday.ui.app.AppBottomSheetDialogFragment
import com.classic.core.ext.onClick
import com.classic.core.ui.vb.viewBinding


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

    private val viewBinding by viewBinding(FragmentUserEditBinding::bind)
    override fun layout() = R.layout.fragment_user_edit

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.apply {
            val act = requireActivity()
            menuChooseCover.onClick {

            }
            menuResetCover.onClick {

            }
        }
    }
}
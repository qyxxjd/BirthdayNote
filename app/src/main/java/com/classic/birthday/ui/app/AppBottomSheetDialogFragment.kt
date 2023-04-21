@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.classic.birthday.ui.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.classic.core.ext.hideKeyboard
import com.classic.core.ui.CoreBottomSheetDialogFragment

/**
 * ViewBinding dialog fragment
 *
 * > 注意事项：该弹窗设置圆角背景失效
 *
 * @author Classic
 * @version 2023/4/21 15:07
 */
abstract class AppBottomSheetDialogFragment : CoreBottomSheetDialogFragment() {
    open fun layout(): Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout(), container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().hideKeyboard()
    }

    fun isDestroy(): Boolean = !isAdded

    open fun showDialog(manager: FragmentManager) {
        dismissDialog()
        show(manager)
    }

    open fun dismissDialog() {
        try {
            if (isAdded) dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
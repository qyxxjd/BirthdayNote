package com.classic.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * ViewHolder
 *
 * @author LiuBin
 * @date 2021/12/31 10:45
 */
open class ViewBindingHolder<VB : ViewBinding>(binding: VB) : RecyclerView.ViewHolder(binding.root) {
    var viewBinding: VB? = binding
}

class EmptyHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

inline fun <reified T : ViewBinding> createViewBindingHolder(parent: ViewGroup): ViewBindingHolder<T> {
    val method = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    val binding = method.invoke(null, LayoutInflater.from(parent.context), parent, false) as T
    return ViewBindingHolder(binding)
}
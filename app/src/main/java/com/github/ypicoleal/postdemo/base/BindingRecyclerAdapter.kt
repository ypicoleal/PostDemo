package com.github.ypicoleal.postdemo.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BindingRecyclerAdapter<in B : ViewDataBinding, T>(@LayoutRes private val layoutId: Int) : RecyclerView.Adapter<BindingViewHolder>() {

    val data: MutableList<T> = mutableListOf()

    abstract fun bindOnCreateViewHolder(binding: B)
    abstract fun bindOnUpdateItem(binding: B, item: T, position: Int)

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            DataBindingUtil.inflate<B>(LayoutInflater.from(parent.context), layoutId, parent, false).let {
                bindOnCreateViewHolder(it)
                BindingViewHolder(it.root)
            }

    final override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        DataBindingUtil.getBinding<B>(holder.itemView)?.let { bindOnUpdateItem(it, data[position], position) }
    }

    override fun getItemCount() = data.size

    open fun updateData(data: List<T>) {
        val oldData = ArrayList(this.data)
        this.data.run {
            clear()
            addAll(data)
        }

        computeDataDifference(oldData)
    }

    protected open fun computeDataDifference(oldData: List<T>) {
        notifyDataSetChanged()
    }
}
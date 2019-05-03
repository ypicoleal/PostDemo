package com.github.ypicoleal.postdemo.ui.main

import com.github.ypicoleal.postdemo.R
import com.github.ypicoleal.postdemo.base.BindingRecyclerAdapter
import com.github.ypicoleal.postdemo.model.dto.Post
import com.github.ypicoleal.postdemo.databinding.ItemPostBinding

class PostAdapter(val listener: Listener):BindingRecyclerAdapter<ItemPostBinding, Post>(R.layout.item_post) {

    interface Listener {
        fun onPostClicked(post:Post)
    }

    override fun bindOnCreateViewHolder(binding: ItemPostBinding) {
        binding.listener = listener
    }

    override fun bindOnUpdateItem(binding: ItemPostBinding, item: Post, position: Int) {
        binding.item = item
    }

    fun markAsFavorite(postId: Int): Post? {
        val post = data.find { it.id == postId }?.apply { favorite = true }
        notifyDataSetChanged()
        return post
    }

    fun markAsRead(postId: Int) {
        data.find { it.id == postId }?.new = false
        notifyDataSetChanged()
    }
}
package com.github.ypicoleal.postdemo.ui.main

import com.github.ypicoleal.postdemo.R
import com.github.ypicoleal.postdemo.base.BindingRecyclerAdapter
import com.github.ypicoleal.postdemo.databinding.ItemCommentBinding
import com.github.ypicoleal.postdemo.model.dto.Comment

class CommentAdapter:BindingRecyclerAdapter<ItemCommentBinding, Comment>(R.layout.item_comment) {

    override fun bindOnCreateViewHolder(binding: ItemCommentBinding) = Unit

    override fun bindOnUpdateItem(binding: ItemCommentBinding, item: Comment, position: Int) {
        binding.item = item
    }

}
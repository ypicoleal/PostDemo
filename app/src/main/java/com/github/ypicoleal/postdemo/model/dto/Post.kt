package com.github.ypicoleal.postdemo.model.dto

data class Post(
    val id: Int = 0,
    val userId: Int = 0,
    var new: Boolean = false,
    val title: String = "",
    val body: String = "",
    var favorite: Boolean = false,
    var user: User? = null,
    var comments: List<Comment> = listOf())
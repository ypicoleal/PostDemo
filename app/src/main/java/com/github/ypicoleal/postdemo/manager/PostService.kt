package com.github.ypicoleal.postdemo.manager

import com.github.ypicoleal.postdemo.model.dto.Comment
import com.github.ypicoleal.postdemo.model.dto.Post
import com.github.ypicoleal.postdemo.model.dto.User
import io.reactivex.Flowable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostService {
    
    @GET("/posts")
    fun getPosts(): Flowable<Response<List<Post>>>

    @GET("/posts/{postId}")
    fun getPost(@Path("postId") postId: Int): Flowable<Response<Post>>

    @GET("/posts/{postId}/comments")
    fun getPostComments(@Path("postId") postId: Int): Flowable<Response<List<Comment>>>

    @GET("/users/{userId}")
    fun getUser(@Path("userId") userId: Int): Flowable<Response<User>>

    @PUT("/posts/{postId}")
    fun markAsFavorite(@Path("postId") postId: Int, @Body post: Post): Flowable<Response<Post>>
}

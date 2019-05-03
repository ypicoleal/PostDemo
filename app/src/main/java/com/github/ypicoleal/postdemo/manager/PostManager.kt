package com.github.ypicoleal.postdemo.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.github.ypicoleal.postdemo.model.dto.Comment
import com.github.ypicoleal.postdemo.model.dto.Post
import com.github.ypicoleal.postdemo.model.dto.User
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostManager @Inject constructor(val context: Context) :
    Function3<Response<Post>, Response<User>, Response<List<Comment>>, Post> {

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com"
        private const val CONNECTION_TIMEOUT_IN_SECONDS = 10L
        private const val READ_TIMEOUT_IN_SECONDS = 10L
    }

    private val cacheSize = (5 * 1024 * 1024).toLong()
    private val myCache = Cache(context.cacheDir, cacheSize)

    private val postService by lazy { retrofit.create(PostService::class.java) }

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val requestInterceptor = Interceptor {
            val request = it.request()
                .newBuilder()
                .build()
            it.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .cache(myCache)
            .addInterceptor(logging)
            .addInterceptor(requestInterceptor)
            .addInterceptor { chain ->
                var request = chain.request()

                request = if (hasNetwork()) {
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                } else {
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    ).build()
                }
                return@addInterceptor chain.proceed(request)
            }
            .connectTimeout(CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    var favoritePosts = mutableListOf<Post>()

    private fun hasNetwork(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun getPosts(): Flowable<List<Post>?> {
        return postService.getPosts().map { response ->
            val body = response.body()
            if (!response.isSuccessful || body == null) return@map null

            return@map body.mapIndexed { index, post -> post.apply { if (index < 20) new = true } }
        }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }


    fun getPost(postId: Int, userId: Int): Flowable<Post> {
        return Flowable.combineLatest(
            postService.getPost(postId),
            postService.getUser(userId),
            postService.getPostComments(postId),
            this
        ).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }

    fun markAsFavorite(post: Post) {
        val newPost = Post(id = post.id, title = post.title, userId = post.userId, body = post.body, favorite = true)
        postService.markAsFavorite(post.id, newPost).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    override fun apply(
        postResponse: Response<Post>,
        userResponse: Response<User>,
        commentResponse: Response<List<Comment>>
    ): Post {
        val post = postResponse.body()
        val user = userResponse.body()
        val comments = commentResponse.body()

        if (post == null || user == null || comments == null) return Post()

        return post.apply {
            this.user = user
            this.comments = comments
        }
    }
}
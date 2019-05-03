package com.github.ypicoleal.postdemo

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import com.github.ypicoleal.postdemo.databinding.ActivityPostBinding
import com.github.ypicoleal.postdemo.manager.PostManager
import com.github.ypicoleal.postdemo.ui.main.CommentAdapter
import io.reactivex.disposables.Disposable

import kotlinx.android.synthetic.main.content_post.*
import javax.inject.Inject
import android.view.MenuItem
import com.github.ypicoleal.postdemo.model.dto.Post
import android.app.Activity
import android.content.Intent

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var post: Post

    @Inject
    lateinit var manager: PostManager

    private val adapter = CommentAdapter()

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate<ActivityPostBinding>(
            LayoutInflater.from(this),
            R.layout.activity_post,
            null,
            false
        ).apply {
            setContentView(root)
            setSupportActionBar(toolbar)
            comments.isNestedScrollingEnabled = false
            comments.adapter  = adapter
        }

        (application as PostApplication).applicationComponent.inject(this)

        val postId = intent.getIntExtra("postId", 1)
        val userId = intent.getIntExtra("userId", 1)

        disposable = manager.getPost(postId, userId).subscribe {
            binding.post = it
            post = it
            adapter.updateData(it?.comments ?: return@subscribe)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.post_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.fav -> {
                manager.markAsFavorite(post)
                val resultIntent = Intent()
                resultIntent.putExtra("favorite_marked", post.id)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()

        disposable?.dispose()
        disposable = null
    }

}

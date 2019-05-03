package com.github.ypicoleal.postdemo.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ypicoleal.postdemo.PostActivity
import com.github.ypicoleal.postdemo.PostApplication
import com.github.ypicoleal.postdemo.R
import com.github.ypicoleal.postdemo.manager.PostManager
import com.github.ypicoleal.postdemo.model.dto.Post
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_main.view.*
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class PostFragment : Fragment(), PostAdapter.Listener {

    private var page: Int = 1
    private val adapter = PostAdapter(this)

    @Inject
    lateinit var manager: PostManager

    private var disposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.postContainer.adapter = adapter

        if (page == 1) loadPost() else adapter.updateData(manager.favoritePosts)
    }

    override fun onStop() {
        super.onStop()

        disposable?.dispose()
        disposable = null
    }

    override fun onResume() {
        super.onResume()
        if (page != 1) adapter.updateData(manager.favoritePosts)
    }

    private fun loadPost() {
        disposable = manager.getPosts().subscribe {
            if (it != null) adapter.updateData(it)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        (activity?.application as PostApplication).applicationComponent.inject(this)
    }

    override fun onPostClicked(post: Post) {
        startActivityForResult(Intent(context, PostActivity::class.java).apply {
            putExtra("postId", post.id)
            putExtra("userId", post.userId)
        }, POST_ACTIVITY_RESULT)

        adapter.markAsRead(post.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == POST_ACTIVITY_RESULT && resultCode == Activity.RESULT_OK && data != null) {
            val updatedPost = data.getIntExtra("favorite_marked", -1)
            adapter.markAsFavorite(updatedPost)?.let { manager.favoritePosts.add(it) }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"


        private const val POST_ACTIVITY_RESULT = 1000

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PostFragment {
            return PostFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
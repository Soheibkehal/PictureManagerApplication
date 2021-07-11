package com.example.picturemanagersoheib.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.viewmodel.FeedPageViewModel
import com.example.picturemanagersoheib.ui.adapters.MyImageFeedRecyclerViewAdapter

class ImageFeedFragment : Fragment() {

    private lateinit var mView: View
    private lateinit var viewModel: FeedPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_image_feed_list, container, false)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FeedPageViewModel::class.java)
        viewModel.feed.observe(viewLifecycleOwner){ feed ->
            // Set the adapter
            if (mView is RecyclerView) {
                with(mView as RecyclerView) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = MyImageFeedRecyclerViewAdapter(feed)
                }
            }
        }
    }
}
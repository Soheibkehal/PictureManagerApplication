package com.example.picturemanagersoheib.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.models.ImageFeed

import com.example.picturemanagersoheib.utils.RetrofitClient
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso


class MyImageFeedRecyclerViewAdapter(
    private val values: List<ImageFeed>
) : RecyclerView.Adapter<MyImageFeedRecyclerViewAdapter.ViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_image_feed, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picasso = Picasso.Builder(context)
            .downloader(OkHttp3Downloader(RetrofitClient.client))
            .build()
        val item = values[position]
        picasso.load(RetrofitClient.BASE_URL + "media/" + item.name)
            .fit()
            .into(holder.imageFeedView);

        holder.userName.text = item.user.login
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val imageFeedView: ImageView = view.findViewById(R.id.imageFeedView)

    }
}
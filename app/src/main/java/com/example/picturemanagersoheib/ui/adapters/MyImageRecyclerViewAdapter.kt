package com.example.picturemanagersoheib.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.picturemanagersoheib.ui.activities.PhotoDetailsActivity
import com.example.picturemanagersoheib.databinding.FragmentImageBinding
import com.example.picturemanagersoheib.data.models.Image
import com.example.picturemanagersoheib.utils.RetrofitClient
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso


class MyImageRecyclerViewAdapter(
    private val values: List<Image>
) : RecyclerView.Adapter<MyImageRecyclerViewAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            FragmentImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picasso = Picasso.Builder(context)
            .downloader(OkHttp3Downloader(RetrofitClient.client))
            .build()
        val item = values[position]
        picasso.load(RetrofitClient.BASE_URL + "media/" + item.name)
            .resize(350, 350)
            .centerCrop()
            .into(holder.imageView);
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentImageBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        val imageView = binding.galleryImageView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val photo = values[position]
                val intent = Intent(context, PhotoDetailsActivity::class.java).apply {
                    putExtra(PhotoDetailsActivity.EXTRA_PHOTO, photo)
                }
                startActivity(context, intent, null)
            }
        }
    }

}
package com.example.picturemanagersoheib.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.ui.fragments.AlbumDetailsFragment
import com.example.picturemanagersoheib.ui.fragments.ImageFragment
import com.example.picturemanagersoheib.utils.CategoryContentType

class AlbumDetailsActivity : AppCompatActivity() {


    companion object {
        const val ALBUM_ID = "album-id"
    }

    private var albumId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumId = intent.getIntExtra(ALBUM_ID, -1)
        setContentView(R.layout.activity_album_details)

        val fragment = AlbumDetailsFragment.newInstance(albumId!!)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentAlbumGallery, fragment).commit()
    }
}
package com.example.picturemanagersoheib.ui.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.database.UserDatabase
import com.example.picturemanagersoheib.data.models.Image
import com.example.picturemanagersoheib.ui.fragments.PhotoDetailsFragment


class PhotoDetailsActivity :  AppCompatActivity() {
    companion object {
        const val EXTRA_PHOTO = "extra-photo"
    }

    private lateinit var image: Image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_photo_details)

        image = intent.getParcelableExtra(EXTRA_PHOTO)!!

        val fragment = PhotoDetailsFragment.newInstance(image)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentDetails, fragment).commit()
    }
}
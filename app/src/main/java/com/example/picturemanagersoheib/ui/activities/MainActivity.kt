package com.example.picturemanagersoheib.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.PictureManagerApplication
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.database.UserDatabase
import com.example.picturemanagersoheib.utils.SessionManager


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val isUserLogged: Boolean  = SessionManager().fetchUserId() !== null

        if(isUserLogged){
            val intent = Intent(context , SecondActivity::class.java)
            startActivity(intent)
        }

        return super.onCreateView(name, context, attrs)
    }

}
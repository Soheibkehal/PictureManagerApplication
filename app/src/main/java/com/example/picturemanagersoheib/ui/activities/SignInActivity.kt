package com.example.picturemanagersoheib.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.models.DefaultResponses
import com.example.picturemanagersoheib.data.models.RegisterCredentials
import com.example.picturemanagersoheib.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {


    private var authRepository : AuthRepository = AuthRepository()
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val signUpButton = findViewById<Button>(R.id.enregistrer)
        val nameId = findViewById<TextView>(R.id.name)
        val emailId = findViewById<TextView>(R.id.email)
        val passwordId = findViewById<TextView>(R.id.password)

        mContext = this

        signUpButton.setOnClickListener {
            val login = nameId.text.toString().trim()
            val mail = emailId.text.toString().trim()
            val password = passwordId.text.toString().trim()

            if(login.isEmpty()){
                nameId.error = "Name required"
                nameId.requestFocus()
                return@setOnClickListener
            }

            if(mail.isEmpty()){
                emailId.error = "Email required"
                emailId.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                passwordId.error = "Password required"
                passwordId.requestFocus()
                return@setOnClickListener
            }
            register(mail,login,password)
        }
    }

    private fun register(mail:String,login:String,password:String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response : DefaultResponses = authRepository.register(RegisterCredentials(mail,login,password))
                Toast.makeText(
                    mContext,
                    response.message,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    mContext,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        val intent = Intent(mContext , MainActivity::class.java)
        startActivity(intent);
    }


}


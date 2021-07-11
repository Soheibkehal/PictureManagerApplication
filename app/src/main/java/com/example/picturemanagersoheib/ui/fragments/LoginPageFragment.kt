package com.example.picturemanagersoheib.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.ui.activities.SecondActivity
import com.example.picturemanagersoheib.data.models.AccessTokenResponse
import com.example.picturemanagersoheib.data.models.LoginCredentials
import com.example.picturemanagersoheib.data.repository.AuthRepository
import com.example.picturemanagersoheib.ui.activities.SignInActivity
import com.example.picturemanagersoheib.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginPageFragment : Fragment() {

    private var authRepository : AuthRepository = AuthRepository()
    private var mContext: Context? = null
    private val sessionManager: SessionManager  = SessionManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.mContext = activity
        return inflater.inflate(R.layout.fragment_login_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.login)

        val usernameId = view.findViewById<TextView>(R.id.username)
        val passwordId = view.findViewById<TextView>(R.id.password)


        loginButton.setOnClickListener{

            val username = usernameId.text.toString().trim()
            val password = passwordId.text.toString().trim()

            if(username.isEmpty()){
                usernameId.error = "Email required"
                usernameId.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                passwordId.error = "Password required"
                passwordId.requestFocus()
                return@setOnClickListener
            }
            getToken(username, password)

        }

        val signInButton = view.findViewById<Button>(R.id.signin)

        signInButton.setOnClickListener {

            val intent = Intent(mContext , SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getToken(username: String, password: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response : AccessTokenResponse = authRepository.login(LoginCredentials(username, password))
                sessionManager.saveAuthTokenAndUserId(response.accessToken, response.userId)
            } catch (e: Exception) {
                Toast.makeText(
                    mContext,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if(sessionManager.fetchAuthToken() != null){
            val intent = Intent(mContext , SecondActivity::class.java)
            startActivity(intent);
        }

    }



}

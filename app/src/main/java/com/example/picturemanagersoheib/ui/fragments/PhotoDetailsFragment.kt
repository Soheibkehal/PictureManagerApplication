package com.example.picturemanagersoheib.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.room.Room
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.database.UserDatabase
import com.example.picturemanagersoheib.data.models.DetailedImage
import com.example.picturemanagersoheib.data.models.Image
import com.example.picturemanagersoheib.data.models.User
import com.example.picturemanagersoheib.data.repository.ImageRepository
import com.example.picturemanagersoheib.data.repository.UserRepository
import com.example.picturemanagersoheib.data.viewmodel.DetailedViewModel
import com.example.picturemanagersoheib.databinding.FragmentPhotoDetailsBinding
import com.example.picturemanagersoheib.utils.RetrofitClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PhotoDetailsFragment : Fragment() {


    private var mContext: Context? = null

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var image: Image
    private var detailedImage: DetailedImage = DetailedImage(0, "init", "init", "init", User(0, "init", "init"), listOf())

    private lateinit var imageView: ImageView
    private var imageRepository: ImageRepository = ImageRepository()
    private lateinit var userRepository: UserRepository

    companion object {
        const val EXTRA_PHOTO = "extra-photo"

        @JvmStatic
        fun newInstance(image : Image) =
            PhotoDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_PHOTO, image)
                }

            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mContext = activity
        arguments?.let {
            image = it.getParcelable(EXTRA_PHOTO)!!
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imageView = binding.photoContainer

        val userDatabase: UserDatabase = Room.databaseBuilder(
            requireActivity().applicationContext,
            UserDatabase::class.java, "users"
        ).allowMainThreadQueries().build()

        userRepository = UserRepository(userDatabase.userDao())
        val viewModel: DetailedViewModel by viewModels()

        getUsers(viewModel)
        if(this::image.isInitialized){
            GlobalScope.launch {
                val response = imageRepository.getImageById(image.id)
                detailedImage = response
            }
            displayImage()
        }

        viewModel.users.observe(viewLifecycleOwner)  {users ->

            val usernames = users.map { it.login }
            val checkedUsersList = users.map { (id) -> detailedImage.imagePermissions.find { it.user.id == id } != null }
            var checkedUsers = checkedUsersList.toBooleanArray()

            val permissionBtn = binding.permissionImageButton

            permissionBtn.setOnClickListener {
                MaterialAlertDialogBuilder(mContext!!)
                    .setTitle("Permissions")
                    .setMultiChoiceItems(usernames.toTypedArray(), checkedUsers) { dialog, which, checked ->
                        // Respond to item chosen
                        checkedUsers[which] = checked
                    }
                    .setPositiveButton("Finish") { dialog, which ->
                        // Respond to positive button press
                        for((index, value) in users.withIndex()) {
                            val wasAuthorized = detailedImage.imagePermissions.find { it.user.id == value.id } != null
                            if(checkedUsers[index] && !wasAuthorized){
                                addPermission(image.id, value.id)
                            }
                            if(!checkedUsers[index] && wasAuthorized){
                                removePermission(image.id, value.id)
                            }
                        }
                    }
                    .show()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getUsers(viewModel : DetailedViewModel) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                var usersResponse : List<User> = userRepository.getUsers()
                viewModel.setUsers(usersResponse)
            } catch (e: Exception) {
                Toast.makeText(
                    mContext,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun addPermission(imageId: Int, userId: Int){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                imageRepository.addPermission(imageId, userId)
            } catch (e: Exception) {
                Toast.makeText(
                    mContext,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun removePermission(imageId: Int, userId: Int){
        GlobalScope.launch(Dispatchers.Main) {
            try {
                imageRepository.removePermission(imageId, userId)
            } catch (e: Exception) {
                Toast.makeText(
                    mContext,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun displayImage() {
        val picasso = Picasso.Builder(mContext!!)
            .downloader(OkHttp3Downloader(RetrofitClient.client))
            .build()

        picasso
            .load(RetrofitClient.BASE_URL + "media/" + image.name)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .into(imageView)
    }
}
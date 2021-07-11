package com.example.picturemanagersoheib.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.room.Room
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.database.UserDatabase
import com.example.picturemanagersoheib.data.models.DetailedAlbum
import com.example.picturemanagersoheib.data.models.DetailedImage
import com.example.picturemanagersoheib.data.models.User
import com.example.picturemanagersoheib.data.repository.AlbumRepository
import com.example.picturemanagersoheib.data.repository.ImageRepository
import com.example.picturemanagersoheib.data.repository.UserRepository
import com.example.picturemanagersoheib.data.viewmodel.DetailedViewModel
import com.example.picturemanagersoheib.databinding.FragmentAlbumDetailsBinding
import com.example.picturemanagersoheib.databinding.FragmentPhotoDetailsBinding
import com.example.picturemanagersoheib.utils.CategoryContentType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlbumDetailsFragment : Fragment() {
    private var mContext: Context? = null

    private var _binding: FragmentAlbumDetailsBinding? = null
    private val binding get() = _binding!!

    private var albumId: Int = -1
    private var detailedAlbum: DetailedAlbum = DetailedAlbum(0, "init", "init", listOf(), listOf())

    private var albumRepository: AlbumRepository = AlbumRepository()
    private lateinit var userRepository: UserRepository


    companion object {
        private const val ALBUM_ID = "album-id"

        @JvmStatic
        fun newInstance(albumId : Int) =
            AlbumDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ALBUM_ID, albumId)
                }

            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mContext = activity
        arguments?.let {
            albumId = it.getInt(ALBUM_ID)
        }
        if(albumId != -1){
            val fragment = ImageFragment.newInstance(albumId, CategoryContentType.ALBUM)
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentAlbumGalleryChild, fragment).commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlbumDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDatabase: UserDatabase = Room.databaseBuilder(
            requireActivity().applicationContext,
            UserDatabase::class.java, "users"
        ).allowMainThreadQueries().build()

        userRepository = UserRepository(userDatabase.userDao())
        val viewModel: DetailedViewModel by viewModels()
        getUsers(viewModel)

        if(albumId != -1){
            GlobalScope.launch {
                val response = albumRepository.getAlbumById(albumId)
                detailedAlbum = response
            }
        }

        viewModel.users.observe(viewLifecycleOwner)  {users ->

            val usernames = users.map { it.login }
            val checkedUsersList = users.map { (id) -> detailedAlbum.albumPermissions.find { it.user.id == id } != null }
            var checkedUsers = checkedUsersList.toBooleanArray()

            val permissionBtn = binding.permissionAlbumButton

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
                            val wasAuthorized = detailedAlbum.albumPermissions.find { it.user.id == value.id } != null
                            if(checkedUsers[index] && !wasAuthorized){
                                addPermission(albumId, value.id)
                            }
                            if(!checkedUsers[index] && wasAuthorized){
                                removePermission(albumId, value.id)
                            }
                        }
                    }
                    .show()
            }
        }
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
                albumRepository.addPermission(imageId, userId)
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
                albumRepository.removePermission(imageId, userId)
            } catch (e: Exception) {
                Toast.makeText(
                    mContext,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}
package com.example.picturemanagersoheib.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.models.Album
import com.example.picturemanagersoheib.data.repository.AlbumRepository
import com.example.picturemanagersoheib.ui.adapters.MyAlbumRecyclerViewAdapter
import com.example.picturemanagersoheib.utils.SessionManager
import com.example.picturemanagersoheib.data.viewmodel.AlbumViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class AlbumFragment : Fragment() {

    private var userId : Int = SessionManager().fetchUserId()!!.toInt()
    private var mContext: Context? = null
    private lateinit var mView : View
    private var albumRepository : AlbumRepository = AlbumRepository()
    private lateinit var viewModel: AlbumViewModel

    companion object {
        const val ARG_USER_ID = "column-count"
        @JvmStatic
        fun newInstance(userId: Int) =
            AlbumFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mContext = activity
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AlbumViewModel::class.java)
        getAlbumsByUser()
        viewModel.albums.observe(viewLifecycleOwner) {albums ->
            if (mView is RecyclerView) {
                with(mView as RecyclerView) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = MyAlbumRecyclerViewAdapter(albums)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_image_list, container, false)
        return mView
    }


    private fun getAlbumsByUser() {

        GlobalScope.launch(Dispatchers.Main) {
            try {
                var albumResponse : List<Album> = albumRepository.getAlbumByUserId(userId)
                viewModel.setAlbums(albumResponse)
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
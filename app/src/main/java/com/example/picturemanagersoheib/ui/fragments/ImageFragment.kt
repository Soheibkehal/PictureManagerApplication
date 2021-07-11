package com.example.picturemanagersoheib.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.models.Image
import com.example.picturemanagersoheib.data.repository.ImageRepository
import com.example.picturemanagersoheib.data.viewmodel.ImageViewModel
import com.example.picturemanagersoheib.ui.adapters.MyImageRecyclerViewAdapter
import com.example.picturemanagersoheib.utils.CategoryContentType
import com.example.picturemanagersoheib.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class ImageFragment() : Fragment() {

    private var selectedId : Int = SessionManager().fetchUserId()!!.toInt()
    private var categoryContentType : CategoryContentType = CategoryContentType.IMAGE
    private var mContext: Context? = null
    private lateinit var mView : View
    private var imageRepository : ImageRepository = ImageRepository()
    private lateinit var viewModel: ImageViewModel

    companion object {
        const val SELECTED_ID = "selected-id"
        const val CATEGORY_CONTENT_TYPE = "category-content-type"
        @JvmStatic
        fun newInstance(selectedId: Int, categoryContentType: CategoryContentType) =
            ImageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(CATEGORY_CONTENT_TYPE, categoryContentType)
                    putInt(SELECTED_ID, selectedId)
                }

            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.mContext = activity
        arguments?.let {
            selectedId = it.getInt(SELECTED_ID)
            categoryContentType = it.getSerializable(CATEGORY_CONTENT_TYPE) as CategoryContentType
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        when (categoryContentType) {
            CategoryContentType.IMAGE -> getImagesByUser()
            CategoryContentType.ALBUM -> getImagesByAlbum()
        }


        viewModel.images.observe(viewLifecycleOwner) {images ->
            if (mView is RecyclerView) {
                with(mView as RecyclerView) {
                    layoutManager = GridLayoutManager(context, 3)
                    adapter = MyImageRecyclerViewAdapter(images)
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



    private fun getImagesByUser() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                var imageResponse : List<Image> = imageRepository.getImageByUserId(selectedId)
                viewModel.setImages(imageResponse)
            } catch (e: Exception) {
                Toast.makeText(
                    mContext,
                    "Error Occurred: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getImagesByAlbum() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                var imageResponse : List<Image> = imageRepository.getImageByAlbumId(selectedId)
                viewModel.setImages(imageResponse)
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
package com.example.picturemanagersoheib.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.models.DefaultResponses
import com.example.picturemanagersoheib.data.repository.ImageRepository
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import smile.clustering.KModes
import smile.clustering.kmodes
import java.io.*


class PhotoPageFragment : Fragment() {

    companion object{
        const val MY_CAMERA_REQUEST_CODE = 1234
    }

    private var mContext: Context? = null

    var imageUri: Uri = Uri.EMPTY
    override fun onCreate(savedInstanceState: Bundle?) {
        this.mContext = activity
        super.onCreate(savedInstanceState)
    }

    var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            var bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            val imgPreview = view?.findViewById<ImageView>(R.id.img_preview)
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    bitmap = setCompressedBitmap(bitmap)
                }catch (e: Exception){
                    Log.e("test", "ERROR")
                }
                imgPreview?.setImageBitmap(bitmap)

                uploadImage(bitmap)
            }
        }
    }

    suspend private fun setCompressedBitmap(bitmap : Bitmap) : Bitmap{
        var bm = getResizedBitmap(bitmap, 500, 500)!!
        val pixels: Array<IntArray> = Array(bm.width){ IntArray(bm.height) }
        for (x in 0 until bm.width){
            for(y in 0 until bm.height){
                pixels[x][y] = bm.getPixel(x, y)
            }
        }
        var clusteredPixels = kmodes(pixels, 2)
        var compressedPixels = IntArray(bm.height * bm.width)

        for (x in 0 until bm.width){
            for(y in 0 until bm.height){
                compressedPixels[bm.width * x + y] = searchValueColor(clusteredPixels, pixels[x][y])
            }
        }
        bm.setPixels(compressedPixels,0, bm.width, 0, 0, bm.width, bm.height)
        return bm
    }

    private fun searchValueColor(pixelsMap: KModes, input: Int) : Int{
        var value  = 0
        var diffValue = Int.MAX_VALUE
        for(pixels in pixelsMap.centroids){
            for(centroidValue in pixels){
                val diff = kotlin.math.abs(centroidValue - input)
                if(diff < diffValue){
                    value = centroidValue
                    diffValue = diff
                }
            }
        }
        return value
    }

    private fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        // create a matrix for the manipulation
        val matrix = Matrix()

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)

        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(context?.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun uploadImage(bitmap: Bitmap){
        var f = convertBitmapToFile("upload", bitmap)
        var requestBody = f.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        GlobalScope.launch(Dispatchers.Main) {
            try {
                ImageRepository().uploadImage(requestBody)
                Toast.makeText(mContext, "Image uploaded !", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Toast.makeText(mContext, e.message, Toast.LENGTH_LONG).show()
            }

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.photo_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cameraBtn = view.findViewById<ImageView>(R.id.btn_camera)

        cameraBtn.setOnClickListener{
            Dexter.withContext(mContext)
                .withPermissions(listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )).withListener(object:MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if(p0!!.areAllPermissionsGranted()) {
                            val values = ContentValues()
                            values.put(Images.Media.TITLE,"New picture")
                            values.put(Images.Media.DESCRIPTION,"From your camera")
                            imageUri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)!!
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
                            resultLauncher.launch(intent)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        TODO("Not a need")
                    }

                }).check()
        }
        super.onViewCreated(view, savedInstanceState)
    }

}
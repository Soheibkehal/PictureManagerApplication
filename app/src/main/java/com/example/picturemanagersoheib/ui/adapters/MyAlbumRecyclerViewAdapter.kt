package com.example.picturemanagersoheib.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.example.picturemanagersoheib.R
import com.example.picturemanagersoheib.data.models.Album
import com.example.picturemanagersoheib.databinding.FragmentAlbumBinding
import com.example.picturemanagersoheib.ui.activities.AlbumDetailsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout


/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyAlbumRecyclerViewAdapter(
    private val values: List<Album>
) : RecyclerView.Adapter<MyAlbumRecyclerViewAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            FragmentAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = "#" + item.id.toString()
        holder.contentView.text = item.name
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentAlbumBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val idView: TextView = binding.itemAlbumId
        val contentView: TextView = binding.albumName
        val editButton: ImageView = binding.editButton


        init {
            itemView.setOnClickListener(this)
            editButton.setOnClickListener(showEditDialog())
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val album = values[position]
                val intent = Intent(context, AlbumDetailsActivity::class.java).apply {
                    putExtra(AlbumDetailsActivity.ALBUM_ID, album.id)
                }
                startActivity(context, intent, null)
            }
        }
    }

    fun showEditDialog(): View.OnClickListener  = View.OnClickListener() {
        val textInputLayout = TextInputLayout(context)
        textInputLayout.setPadding(19, 19, 19, 19)
        val input = EditText(context)
        input.setPadding(50)
        input.hint = "New name"
        textInputLayout.addView(input)

        MaterialAlertDialogBuilder(
            context,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
        )

            .setView(textInputLayout)
            .setNegativeButton("Cancel") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("Modify") { dialog, which ->
                // Respond to positive button press
            }
            .show()
    }



}
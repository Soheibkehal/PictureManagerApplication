package com.example.picturemanagersoheib.data.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parceler


@Parcelize
data class Image(val id: Int, val name: String?, val timestamp: String?, val metadata: String?):
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    companion object : Parceler<Image> {

        override fun Image.write(parcel: Parcel, flags: Int) {
            parcel.writeInt(id)
            parcel.writeString(name)
            parcel.writeString(timestamp)
            parcel.writeString(metadata)
        }

        override fun create(parcel: Parcel): Image {
            return Image(parcel)
        }
    }
}
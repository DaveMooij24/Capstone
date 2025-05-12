package nl.hva.capstone.data.model

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class Purchase(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double?,
    @SerializedName("dateTime") val dateTime: Timestamp,
    @SerializedName("taxes") val taxes: Int?,
    @SerializedName("image") val image: Uri?
)

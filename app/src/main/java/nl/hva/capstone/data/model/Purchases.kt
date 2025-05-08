package nl.hva.capstone.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class Purchases(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("price") val salePrice: Double?,
    @SerializedName("taxes") val taxes: Int?,
    @SerializedName("image") val image: Uri?
)

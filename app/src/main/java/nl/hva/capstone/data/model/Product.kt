package nl.hva.capstone.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("salePrice") val estimatedTimeMinutes: Int?,
    @SerializedName("purchasePrice") val price: Double?,
    @SerializedName("taxes") val taxes: Int?,
    @SerializedName("image") val image: Uri?
)

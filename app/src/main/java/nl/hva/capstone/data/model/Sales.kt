package nl.hva.capstone.data.model

import com.google.gson.annotations.SerializedName

data class Sales(
    @SerializedName("id") val id: Long,
    @SerializedName("price") val name: String,
    @SerializedName("date") val salePrice: Double?,
    @SerializedName("taxes") val taxes: Int?,
)

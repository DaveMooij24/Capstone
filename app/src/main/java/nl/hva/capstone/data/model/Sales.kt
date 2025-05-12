package nl.hva.capstone.data.model

import com.google.firebase.Timestamp
import com.google.gson.annotations.SerializedName

data class Sales(
    @SerializedName("id") val id: Long,
    @SerializedName("price") val name: String,
    @SerializedName("dateTime") val dateTime: Timestamp,
    @SerializedName("taxes") val taxes: Int?,
)

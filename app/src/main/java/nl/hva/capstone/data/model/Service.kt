package nl.hva.capstone.data.model

import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("estimatedTimeMinutes") val estimatedTimeMinutes: Int?,
    @SerializedName("price") val price: Double?,
    @SerializedName("taxes") val taxes: Int?
)

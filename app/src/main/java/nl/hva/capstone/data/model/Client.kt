package nl.hva.capstone.data.model

import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("color") val color: String,
    @SerializedName("notes") val notes: String
)

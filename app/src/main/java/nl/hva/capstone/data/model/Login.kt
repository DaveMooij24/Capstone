package nl.hva.capstone.data.model

import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
)

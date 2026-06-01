package com.example.myapplication.domain.model

import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("createdAt", alternate = ["created_at"]) val createdAt: String,
    @SerializedName("avatarUrl", alternate = ["avatar_url"]) val avatarUrl: String? = null,
    @SerializedName("colorHex", alternate = ["color_hex"]) val colorHex: String? = null,
    @SerializedName("elo") val elo: Int = 1500
)

package com.example.myapplication.domain.model

import com.google.gson.annotations.SerializedName

data class Asado(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String,
    @SerializedName("playerIds", alternate = ["player_ids"]) val playerIds: List<String>,
    @SerializedName("comment") val comment: String? = null
)

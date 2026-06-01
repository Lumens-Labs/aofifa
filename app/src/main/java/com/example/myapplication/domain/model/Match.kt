package com.example.myapplication.domain.model

import com.google.gson.annotations.SerializedName

data class Match(
    @SerializedName("id") val id: String,
    @SerializedName("asadoId", alternate = ["asado_id"]) val asadoId: String,
    @SerializedName("winnerId", alternate = ["winner_id"]) val winnerId: String,
    @SerializedName("loserId", alternate = ["loser_id"]) val loserId: String,
    @SerializedName("winnerGoles", alternate = ["winner_goles"]) val winnerGoles: Int = 0,
    @SerializedName("loserGoles", alternate = ["loser_goles"]) val loserGoles: Int = 0,
    @SerializedName("photoUrl", alternate = ["photo_url"]) val photoUrl: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

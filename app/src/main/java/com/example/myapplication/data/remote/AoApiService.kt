package com.example.myapplication.data.remote

import com.example.myapplication.domain.model.Snapshot
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AoApiService {
    @GET("api/ao")
    suspend fun getSnapshot(): Snapshot

    @POST("api/ao")
    suspend fun postSnapshot(@Body snapshot: Snapshot)

    @GET("api/ao/teams")
    suspend fun getTeams(): TeamResponse
}

data class TeamResponse(
    val status: String,
    val count: Int,
    val teams: List<TeamBadge>
)

data class TeamBadge(
    val id: String,
    val name: String,
    val logoUrl: String
)

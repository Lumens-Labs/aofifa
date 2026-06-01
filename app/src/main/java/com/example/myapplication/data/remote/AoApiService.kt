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
}

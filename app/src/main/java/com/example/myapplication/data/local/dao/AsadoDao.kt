package com.example.myapplication.data.local.dao

import androidx.room.*
import com.example.myapplication.data.local.entity.AsadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsadoDao {
    @Query("SELECT * FROM asados")
    fun getAllAsados(): Flow<List<AsadoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsados(asados: List<AsadoEntity>)

    @Query("DELETE FROM asados")
    suspend fun deleteAllAsados()
}

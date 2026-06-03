package com.example.myapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.dao.AsadoDao
import com.example.myapplication.data.local.dao.MatchDao
import com.example.myapplication.data.local.dao.PlayerDao
import com.example.myapplication.data.local.entity.AsadoEntity
import com.example.myapplication.data.local.entity.MatchEntity
import com.example.myapplication.data.local.entity.PlayerEntity

@Database(
    entities = [PlayerEntity::class, AsadoEntity::class, MatchEntity::class],
    version = 6, // Added tournamentConfigJson to AsadoEntity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun asadoDao(): AsadoDao
}

package com.example.myapplication.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.notifications.NotificationReceiver
import com.example.myapplication.domain.model.Match
import com.example.myapplication.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class AsadoViewModel : ViewModel() {

    private val _liveMatches = MutableStateFlow<List<Match>>(emptyList())
    val liveMatches: StateFlow<List<Match>> = _liveMatches.asStateFlow()

    private val _currentAsadoId = MutableStateFlow<String?>(null)
    val currentAsadoId: StateFlow<String?> = _currentAsadoId.asStateFlow()

    fun startAsado(context: Context) {
        _currentAsadoId.value = UUID.randomUUID().toString()
        _liveMatches.value = emptyList()
        scheduleNotification(context)
    }

    private fun scheduleNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
        // Schedule every 15 minutes
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 15 * 60 * 1000,
            15 * 60 * 1000,
            pendingIntent
        )
    }

    fun addMatch(winnerId: String, loserId: String, winnerGoles: Int, loserGoles: Int, photoUrl: String?) {
        val asadoId = _currentAsadoId.value ?: return
        val newMatch = Match(
            id = UUID.randomUUID().toString(),
            asadoId = asadoId,
            winnerId = winnerId,
            loserId = loserId,
            winnerGoles = winnerGoles,
            loserGoles = loserGoles,
            photoUrl = photoUrl
        )
        _liveMatches.value = _liveMatches.value + newMatch
    }
}

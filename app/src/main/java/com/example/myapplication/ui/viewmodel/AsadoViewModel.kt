package com.example.myapplication.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.notifications.NotificationReceiver
import com.example.myapplication.data.repository.AoRepository
import com.example.myapplication.domain.model.Asado
import com.example.myapplication.domain.model.Match
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AsadoViewModel(private val repository: AoRepository) : ViewModel() {

    private val _liveMatches = MutableStateFlow<List<Match>>(emptyList())
    val liveMatches: StateFlow<List<Match>> = _liveMatches.asStateFlow()

    private val _currentAsadoId = MutableStateFlow<String?>(null)
    val currentAsadoId: StateFlow<String?> = _currentAsadoId.asStateFlow()

    private val _activeAsado = repository.activeAsado.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    init {
        viewModelScope.launch {
            _activeAsado.collect { asado ->
                _currentAsadoId.value = asado?.id
                if (asado != null) {
                    repository.matches.collect { allMatches ->
                        _liveMatches.value = allMatches.filter { it.asadoId == asado.id }
                    }
                } else {
                    _liveMatches.value = emptyList()
                }
            }
        }
    }

    fun startAsado(context: Context, date: String, playerIds: List<String>, comment: String?) {
        val asadoId = UUID.randomUUID().toString()
        val newAsado = Asado(
            id = asadoId,
            date = date,
            playerIds = playerIds,
            comment = comment,
            isActive = true
        )
        
        viewModelScope.launch {
            repository.insertAsado(newAsado)
            scheduleNotification(context)
        }
    }

    fun finalizeAsado() {
        val asadoId = _currentAsadoId.value ?: return
        viewModelScope.launch {
            _activeAsado.value?.let { current ->
                if (current.id == asadoId) {
                    repository.updateAsado(current.copy(isActive = false))
                }
            }
        }
    }

    private fun scheduleNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        
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
            photoUrl = photoUrl,
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
        
        viewModelScope.launch {
            repository.insertMatch(newMatch)
        }
    }
}

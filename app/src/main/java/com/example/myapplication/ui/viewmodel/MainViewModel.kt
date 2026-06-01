package com.example.myapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.AoRepository
import com.example.myapplication.domain.model.Snapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AoRepository) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val snapshot: StateFlow<Snapshot?> = repository.snapshot
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refreshData() {
        _error.value = null
        viewModelScope.launch {
            try {
                repository.refreshData()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error refreshing data", e)
                _error.value = "Error al bajar datos: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun uploadData() {
        viewModelScope.launch {
            try {
                snapshot.value?.let {
                    repository.syncSnapshot(it)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

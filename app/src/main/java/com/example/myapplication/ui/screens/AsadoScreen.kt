package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.viewmodel.AsadoViewModel
import com.example.myapplication.ui.viewmodel.MainViewModel

@Composable
fun AsadoScreen(mainViewModel: MainViewModel, asadoViewModel: AsadoViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentAsadoId by asadoViewModel.currentAsadoId.collectAsState()
    val liveMatches by asadoViewModel.liveMatches.collectAsState()

    if (currentAsadoId == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { asadoViewModel.startAsado(context) }) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Nuevo Asado")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Asado en Curso", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(liveMatches) { match ->
                    Text("Partido: ${match.winnerId} vs ${match.loserId} (${match.winnerGoles}-${match.loserGoles})")
                }
            }

            Button(
                onClick = { /* TODO: Open Result Capture Dialog */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cargar Resultado")
            }
        }
    }
}

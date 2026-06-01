package com.example.myapplication.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.remote.CloudinaryManager
import com.example.myapplication.domain.model.Player
import com.example.myapplication.ui.theme.AoOrange
import com.example.myapplication.ui.theme.PlayerIcons
import com.example.myapplication.ui.viewmodel.AsadoViewModel
import com.example.myapplication.ui.viewmodel.MainViewModel

@Composable
fun ActiveAsadoScreen(
    asadoId: String,
    mainViewModel: MainViewModel,
    asadoViewModel: AsadoViewModel,
    onNavigateBack: () -> Unit
) {
    val snapshot by mainViewModel.snapshot.collectAsState()
    val liveMatches by asadoViewModel.liveMatches.collectAsState()
    val context = LocalContext.current
    
    var winnerId by remember { mutableStateOf<String?>(null) }
    var loserId by remember { mutableStateOf<String?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var showFinalizeDialog by remember { mutableStateOf(false) }

    snapshot?.let { data ->
        val asado = data.asados.find { it.id == asadoId } ?: return@let
        val participatingPlayers = data.players.filter { player -> 
            asado.playerIds.contains(player.id) 
        }

        if (showCamera) {
            CameraScreen(onPhotoCaptured = { uri ->
                showCamera = false
                isUploading = true
                CloudinaryManager.uploadPhoto(uri, "asados") { url ->
                    isUploading = false
                    if (url != null) {
                        asadoViewModel.addMatch(
                            winnerId!!,
                            loserId!!,
                            1,
                            0,
                            url
                        )
                        winnerId = null
                        loserId = null
                        Toast.makeText(context, "Resultado cargado con éxito!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { showFinalizeDialog = true }) {
                            Icon(Icons.Default.Close, contentDescription = "Finalizar Asado", tint = AoOrange)
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Asado Activo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AoOrange,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = asado.date,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Seleccionar Ganador:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    PlayerGrid(
                        players = participatingPlayers,
                        selectedId = winnerId,
                        onPlayerSelected = { winnerId = it },
                        disabledId = loserId
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Seleccionar Perdedor:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    PlayerGrid(
                        players = participatingPlayers,
                        selectedId = loserId,
                        onPlayerSelected = { loserId = it },
                        disabledId = winnerId
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Partidos de hoy:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    val playersMap = data.players.associateBy { it.id }
                    
                    Box(modifier = Modifier.weight(1f)) {
                        if (liveMatches.isEmpty()) {
                            Text(
                                text = "No hay partidos registrados aún.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(1),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)
                            ) {
                                items(liveMatches.reversed()) { match ->
                                    MatchItem(match = match, playersMap = playersMap)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isUploading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AoOrange)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    asadoViewModel.addMatch(
                                        winnerId!!,
                                        loserId!!,
                                        1,
                                        0,
                                        null
                                    )
                                    winnerId = null
                                    loserId = null
                                    Toast.makeText(context, "Resultado guardado (sin foto)", Toast.LENGTH_SHORT).show()
                                },
                                enabled = winnerId != null && loserId != null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AoOrange),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AoOrange),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text("Sin Foto", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showCamera = true },
                                enabled = winnerId != null && loserId != null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AoOrange),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text("Con Foto", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AoOrange)
    }

    if (showFinalizeDialog) {
        AlertDialog(
            onDismissRequest = { showFinalizeDialog = false },
            title = { Text("Finalizar Asado") },
            text = { Text("¿Estás seguro de que deseas finalizar este asado? Ya no podrás registrar más partidos para esta sesión.") },
            confirmButton = {
                TextButton(onClick = {
                    asadoViewModel.finalizeAsado()
                    showFinalizeDialog = false
                    onNavigateBack()
                }) {
                    Text("Finalizar", color = AoOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinalizeDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun PlayerGrid(
    players: List<Player>,
    selectedId: String?,
    onPlayerSelected: (String) -> Unit,
    disabledId: String?
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(players) { player ->
            val isSelected = player.id == selectedId
            val isDisabled = player.id == disabledId

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        if (isSelected) AoOrange.copy(alpha = 0.2f) 
                        else Color.Transparent
                    )
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) AoOrange else Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    )
                    .clickable(enabled = !isDisabled) { onPlayerSelected(player.id) }
                    .padding(8.dp)
                    .graphicsLayer { alpha = if (isDisabled) 0.3f else 1.0f }
            ) {
                Image(
                    painter = painterResource(id = PlayerIcons.getAvatar(player.id)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = player.name,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

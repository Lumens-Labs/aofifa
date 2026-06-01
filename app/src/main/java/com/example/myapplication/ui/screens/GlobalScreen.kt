package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.myapplication.domain.model.Player
import com.example.myapplication.ui.theme.DeepBlue
import com.example.myapplication.ui.theme.NeonGreen
import com.example.myapplication.ui.theme.PlayerIcons
import com.example.myapplication.ui.viewmodel.MainViewModel

@Composable
fun GlobalScreen(viewModel: MainViewModel) {
    val snapshot by viewModel.snapshot.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val error by viewModel.error.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    snapshot?.let { data ->
        val sortedPlayers = data.players.sortedByDescending { it.elo }
        val allStats = com.example.myapplication.domain.logic.RankingEngine.calculateAsadoRanking(
            data.players.map { it.id }, data.matches
        ).associateBy { it.playerId }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AO&FIFA",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = NeonGreen
                    )
                    Text(
                        text = "by Nejca",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBlue
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.refreshData() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bajar Datos")
                    }
                    Button(
                        onClick = { viewModel.uploadData() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Subir Datos")
                    }
                }
            }

            item {
                Text(
                    text = "🏆 MVP Histórico",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (sortedPlayers.isNotEmpty()) {
                    MvpCard(sortedPlayers[0])
                }
            }

            item {
                Text(
                    text = "Ranking por ELO",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            itemsIndexed(sortedPlayers) { index, player ->
                PlayerCard(index + 1, player, allStats[player.id])
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MvpCard(player: Player) {
    val playerColor = try { Color((player.colorHex ?: "#00E676").toColorInt()) } catch (e: Exception) { Color.Green }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = playerColor.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = PlayerIcons.getAvatar(player.id)),
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(CircleShape).background(playerColor.copy(alpha = 0.4f)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = player.name, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Text(text = "Dominando con ${player.elo} ELO", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun PlayerCard(rank: Int, player: Player, stats: com.example.myapplication.domain.logic.PlayerStats? = null) {
    val playerColor = try { Color((player.colorHex ?: "#00E676").toColorInt()) } catch (e: Exception) { Color.Green }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "#$rank", fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
            Image(
                painter = painterResource(id = PlayerIcons.getAvatar(player.id)),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(playerColor.copy(alpha = 0.2f)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = player.name, fontWeight = FontWeight.Bold)
                Text(
                    text = "Record: ${stats?.wins ?: 0}W - ${stats?.losses ?: 0}L | WinRate: ${stats?.winRate ?: 0.0}%",
                    fontSize = 12.sp
                )
            }
            Text(text = "${player.elo}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

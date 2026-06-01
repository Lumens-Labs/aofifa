package com.example.myapplication.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.domain.model.Match
import com.example.myapplication.ui.theme.AoOrange
import com.example.myapplication.ui.theme.PlayerIcons
import com.example.myapplication.ui.viewmodel.MainViewModel

@Composable
fun MatchDetailsScreen(asadoId: String, viewModel: MainViewModel) {
    val snapshot by viewModel.snapshot.collectAsState()
    
    snapshot?.let { data ->
        val asado = data.asados.find { it.id == asadoId }
        val matches = data.matches.filter { it.asadoId == asadoId }
        val playersMap = data.players.associateBy { it.id }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = asado?.date ?: "Fecha desconocida",
                style = MaterialTheme.typography.headlineSmall,
                color = AoOrange,
                fontWeight = FontWeight.Bold
            )
            asado?.comment?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Este asado ha finalizado. No se pueden registrar más partidos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Partidos de hoy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(matches) { match ->
                    MatchItem(match, playersMap)
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AoOrange)
    }
}

@Composable
fun MatchItem(match: Match, playersMap: Map<String, com.example.myapplication.domain.model.Player>) {
    val winner = playersMap[match.winnerId]
    val loser = playersMap[match.loserId]

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Winner
            Image(
                painter = painterResource(id = PlayerIcons.getAvatar(match.winnerId)),
                contentDescription = null,
                modifier = Modifier.size(24.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = winner?.name ?: "???",
                fontWeight = FontWeight.Bold,
                color = AoOrange,
                fontSize = 14.sp
            )
            
            Text(
                text = " vs ",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            // Loser
            Image(
                painter = painterResource(id = PlayerIcons.getAvatar(match.loserId)),
                contentDescription = null,
                modifier = Modifier.size(24.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = loser?.name ?: "???",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}

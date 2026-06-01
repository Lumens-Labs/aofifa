package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AoOrange
import com.example.myapplication.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SyncScreen(viewModel: MainViewModel) {
    val scope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sincronización Manual",
            style = MaterialTheme.typography.headlineSmall,
            color = AoOrange,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Gestiona tus datos con la nube. Esta operación requiere conexión a Internet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = { 
                scope.launch {
                    isUploading = true
                    viewModel.uploadData()
                    isUploading = false
                }
            },
            enabled = !isUploading && !isDownloading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AoOrange),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Subir datos a la nube", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { 
                scope.launch {
                    isDownloading = true
                    viewModel.refreshData()
                    isDownloading = false
                }
            },
            enabled = !isUploading && !isDownloading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AoOrange),
            border = androidx.compose.foundation.BorderStroke(1.dp, AoOrange),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isDownloading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AoOrange)
            } else {
                Text("Bajar datos de la nube", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

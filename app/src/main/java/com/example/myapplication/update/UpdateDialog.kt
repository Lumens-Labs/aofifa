package com.example.myapplication.update

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UpdateDialog(
    release: GithubRelease,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualización disponible") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Versión: ${release.tagName ?: "Desconocida"}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Novedades:")
                Text(release.changelog ?: "Sin descripción de novedades.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Actualizar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Más tarde") }
        }
    )
}

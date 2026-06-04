package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.CustomDialog
import com.example.myapplication.ui.theme.AoOrange
import com.example.myapplication.ui.viewmodel.MainViewModel
import androidx.compose.ui.graphics.Color


@Composable
fun SyncScreen(viewModel: MainViewModel) {
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var showDownloadConfirm by remember { mutableStateOf(false) }
    var showUploadConfirm by remember { mutableStateOf(false) }
    var uploadPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(success) {
        success?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearSuccess()
        }
    }

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
            onClick = { showUploadConfirm = true },
            enabled = !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AoOrange),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Subir datos a la nube", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = { showDownloadConfirm = true },
            enabled = !loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AoOrange),
            border = androidx.compose.foundation.BorderStroke(1.dp, AoOrange),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Bajar datos de la nube", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }

    if (loading) {
        androidx.compose.ui.window.Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AoOrange)
            }
        }
    }

    if (showDownloadConfirm) {
        CustomDialog(
            onDismissRequest = { showDownloadConfirm = false },
            title = "¿Bajar Datos?",
            content = { Text("Se sobreescribirán todos los datos locales con la versión de la nube. Esta acción no se puede deshacer.", color = Color.White) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.refreshData()
                        showDownloadConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AoOrange)
                ) {
                    Text("Bajar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDownloadConfirm = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    if (showUploadConfirm) {
        CustomDialog(
            onDismissRequest = { 
                showUploadConfirm = false
                uploadPassword = ""
                passwordError = false
            },
            title = "Subir Datos a la Nube",
            content = {
                Column {
                    Text("Ingresa la contraseña para confirmar la subida.", color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uploadPassword,
                        onValueChange = { 
                            uploadPassword = it
                            passwordError = false
                        },
                        label = { Text("Contraseña") },
                        isError = passwordError,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AoOrange,
                            focusedLabelColor = AoOrange,
                            unfocusedBorderColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    if (passwordError) {
                        Text(
                            text = "Contraseña incorrecta",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uploadPassword == "mumbongopro") {
                            viewModel.uploadData()
                            showUploadConfirm = false
                            uploadPassword = ""
                        } else {
                            passwordError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AoOrange)
                ) {
                    Text("Confirmar Subida", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showUploadConfirm = false
                    uploadPassword = ""
                    passwordError = false
                }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

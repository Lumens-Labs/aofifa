package com.example.myapplication.update

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class UpdateManager(private val context: Context) {

    private var isDownloading = false
    var pendingInstallApkName: String? = null

    fun checkPendingInstall() {
        pendingInstallApkName?.let { fileName ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context.packageManager.canRequestPackageInstalls()) {
                    pendingInstallApkName = null
                    installApk(fileName)
                }
            } else {
                pendingInstallApkName = null
                installApk(fileName)
            }
        }
    }

    fun downloadAndInstall(url: String, version: String) {
        val fileName = "update_${version}.apk"
        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, fileName)

        // 1. Limpiar APKs viejos de actualizaciones anteriores
        downloadDir?.listFiles()
            ?.filter { it.name.startsWith("update_") && it.name != fileName }
            ?.forEach { it.delete() }

        // 2. Si el archivo actual ya existe, saltamos la descarga y vamos directo a instalar
        if (file.exists()) {
            installApk(fileName)
            return
        }
        
        if (isDownloading) {
            Toast.makeText(context, "Ya hay una descarga en curso...", Toast.LENGTH_SHORT).show()
            return
        }
        isDownloading = true
        
        Toast.makeText(context, "Descargando actualización en segundo plano...", Toast.LENGTH_LONG).show()

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Actualizando App")
            .setDescription("Nueva versión $version")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType("application/vnd.android.package-archive")

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = dm.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                if (id == downloadId) {
                    isDownloading = false
                    installApk(fileName)
                    context.unregisterReceiver(this)
                }
            }
        }
        
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Context.RECEIVER_EXPORTED else 0
        context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), flag)
    }

    private fun installApk(fileName: String) {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            if (!file.exists()) {
                return
            }

            // Validar que el APK sea válido y corresponda a nuestra app
            val packageInfo = context.packageManager.getPackageArchiveInfo(file.absolutePath, 0)
            if (packageInfo == null || packageInfo.packageName != context.packageName) {
                // El APK está corrupto o es de otra app, lo borramos
                file.delete()
                Toast.makeText(context, "El archivo de actualización es inválido.", Toast.LENGTH_LONG).show()
                return
            }

            // Validar que el versionCode del APK sea estrictamente mayor al actual
            val currentVersionInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val currentCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) currentVersionInfo.longVersionCode else currentVersionInfo.versionCode.toLong()
            val apkCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode.toLong()

            if (apkCode <= currentCode) {
                file.delete()
                Toast.makeText(context, "La actualización descargada no es más reciente.", Toast.LENGTH_LONG).show()
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    pendingInstallApkName = fileName
                    Toast.makeText(context, "Por favor, otorga el permiso para continuar.", Toast.LENGTH_LONG).show()
                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                    intent.data = Uri.parse("package:${context.packageName}")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return // El usuario debe dar permiso y volver a intentar
                }
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

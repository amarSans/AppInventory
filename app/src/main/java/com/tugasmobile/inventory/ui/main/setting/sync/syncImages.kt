package com.tugasmobile.inventory.ui.main.setting.sync

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import java.io.File

fun syncImagesWithProgress(context: Context, databaseImageUris: List<String>) {
    val progressDialog = ProgressDialog(context)
    progressDialog.setMessage("Menyinkronkan gambar...")
    progressDialog.setCancelable(false)
    progressDialog.show()

    Thread {
        val validImageNames = mutableSetOf<String>()

        // Ambil nama file dari URI database (kecuali android.resource)
        for (uriString in databaseImageUris) {
            if (!uriString.startsWith("android.resource://")) {
                val uri = Uri.parse(uriString)
                val name = getFileNameFromUri(context, uri)
                if (name != null) {
                    validImageNames.add(name)
                }
            }
        }

        val imageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "InventoryApp")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }

        val allFiles = imageDir.listFiles()
        val deletedFiles = mutableListOf<String>()

        allFiles?.forEach { file ->
            if (file.isFile && !validImageNames.contains(file.name)) {
                if (file.delete()) {
                    deletedFiles.add(file.name)
                    Log.d("Sync", "File dihapus: ${file.name}")
                }
            }
        }

        (context as Activity).runOnUiThread {
            progressDialog.dismiss()
            Toast.makeText(context, "Sinkronisasi selesai. Dihapus: ${deletedFiles.size} file.", Toast.LENGTH_LONG).show()
        }
    }.start()
}
fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    } else if (uri.scheme == "file") {
        name = File(uri.path!!).name
    }
    return name
}


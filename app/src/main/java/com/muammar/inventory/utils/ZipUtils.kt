package com.muammar.inventory.utils

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ZipUtils {


    fun zipFolder(sourceFolder: File, zipFile: File) {
        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            sourceFolder.listFiles()?.forEach { file ->
                zipFiles(file, zos, file.name)
            }
        }
    }

    private fun zipFiles(file: File, zos: ZipOutputStream, basePath: String) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                zipFiles(child, zos, "$basePath/${child.name}")
            }
        } else {
            FileInputStream(file).use { fis ->
                val entry = ZipEntry(basePath)
                zos.putNextEntry(entry)
                fis.copyTo(zos)
                zos.closeEntry()
            }
        }
    }


    fun unzipToFolder(context: Context, zipUri: Uri, destinationFolder: File): Boolean {
        return try {
            context.contentResolver.openInputStream(zipUri)?.use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zis ->
                    var ze: ZipEntry? = zis.nextEntry
                    while (ze != null) {
                        val file = File(destinationFolder, ze.name)
                        if (ze.isDirectory) {
                            file.mkdirs()
                        } else {
                            file.parentFile?.mkdirs()
                            FileOutputStream(file).use { fos ->
                                zis.copyTo(fos)
                            }
                        }
                        zis.closeEntry()
                        ze = zis.nextEntry
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}

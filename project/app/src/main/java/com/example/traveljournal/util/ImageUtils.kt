package com.example.traveljournal.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    
    /**
     * 将URI指向的图片复制到应用私有目录中
     * @param context 上下文
     * @param uri 图片的URI
     * @return 保存后的图片文件路径，如果保存失败则返回null
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, "images/$fileName")
            
            // 创建images目录（如果不存在）
            file.parentFile?.let { parentFile ->
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
            }
            
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 从内部存储中删除图片
     * @param context 上下文
     * @param imagePath 图片路径
     */
    fun deleteImageFromInternalStorage(context: Context, imagePath: String?) {
        imagePath?.let { path ->
            try {
                val file = File(path)
                if (file.exists() && file.parent == "${context.filesDir}/images") {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 获取内部存储中的图片文件
     * @param context 上下文
     * @param fileName 图片文件名
     * @return 图片文件，如果不存在则返回null
     */
    fun getImageFile(context: Context, fileName: String): File? {
        val file = File(context.filesDir, "images/$fileName")
        return if (file.exists()) file else null
    }
    
    /**
     * 清理内部存储中的图片目录
     * @param context 上下文
     */
    fun cleanupImages(context: Context) {
        try {
            val imagesDir = File(context.filesDir, "images")
            if (imagesDir.exists() && imagesDir.isDirectory) {
                imagesDir.deleteRecursively()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
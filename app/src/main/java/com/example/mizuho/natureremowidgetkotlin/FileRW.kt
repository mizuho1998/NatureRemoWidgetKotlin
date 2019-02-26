package com.example.mizuho.natureremowidgetkotlin

import android.content.Context
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class FileRW(private val context: Context) {
    private val TAG = "T_FILERW"

    // ファイルの削除
    fun clearFile(file_name: String) {
        // ファイル削除
        context.deleteFile(file_name)
        Log.d(TAG, "clearFile()")
    }

    // 時間とともにmagをファイルを保存
    fun writeLog(file_name: String, msg: String) {
        val stringBuffer  = StringBuffer()
        val currentTime   = System.currentTimeMillis()
        val dataFormat    = SimpleDateFormat("yyyy/MM/dd/HH:mm:ss", Locale.US)
        val cTime: String = dataFormat.format(currentTime)
        Log.d("debug", cTime)

        stringBuffer.append("$cTime: $msg")
        stringBuffer.append(System.getProperty("line.separator")) // 改行

        val fileOutputStream: FileOutputStream = context.openFileOutput(file_name, Context.MODE_APPEND)
        fileOutputStream.write(stringBuffer.toString().toByteArray())
    }

    // 文字列をファイルに保存
    fun writeFileMessage(file_name: String, msg: String) {
        val stringBuffer = StringBuffer()
        stringBuffer.append(msg)

        val fileOutputStream: FileOutputStream = context.openFileOutput(file_name, Context.MODE_APPEND)
        fileOutputStream.write(stringBuffer.toString().toByteArray())
    }

    // ファイルを読み出し
    fun readFile(file_name: String): String {

        val stringBuffer = StringBuffer()

        //　ファイルの中身の読み出し
        try {
            val fileInputStream: FileInputStream = context.openFileInput(file_name)
            val reader = BufferedReader(InputStreamReader(fileInputStream, "UTF-8"))

            var lineBuffer: String? = reader.readLine()

            while ( lineBuffer  != null) {
                stringBuffer.append(lineBuffer)
//                stringBuffer.append(System.getProperty("line.separator"))
                lineBuffer = reader.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return stringBuffer.toString()
    }
}
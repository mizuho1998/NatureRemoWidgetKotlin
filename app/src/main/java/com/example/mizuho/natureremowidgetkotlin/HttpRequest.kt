package com.example.mizuho.natureremowidgetkotlin

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL



class HttpRequest {

    companion object {
        const val TAG = "T_HTTPREQUEST"

        // HTTP の POST リクエストの送信
        fun postHttpRequest(url: URL, token: String, body: String = ""): Pair<Int, String> {
            Log.d(TAG, "start SentHttpRequest")
            Log.d(TAG, "\nURL: $url\ntoken: $token\nbosy: $body")

            try {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "POST"
                // 接続タイムアウト時間
                urlConnection.connectTimeout = 100000
                // レスポンスのデータの読み取りタイムアウト時間
                urlConnection.readTimeout = 100000
                // 一般要求プロパティーを設定
                urlConnection.setRequestProperty("Authorization", "Bearer $token")
                urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                //リクエストのボディ送信の可否
                urlConnection.doOutput = true
                //レスポンスのボディ受信の可否
                urlConnection.doInput = true
                // リダイレクトの可否
                urlConnection.instanceFollowRedirects = true

                // 送信するデータの設定
                // データを送信するためにはbyte配列に変換する必要がある
                val sendData = body.toByteArray(Charsets.UTF_8)
                urlConnection.outputStream.write(sendData)
                urlConnection.outputStream.flush()
                urlConnection.outputStream.close()

                urlConnection.connect()


                // レスピンスコードの取得
                val responseCode = urlConnection.responseCode
                Log.d(TAG, "HttpStatusCode:$responseCode")

                // レスポンスをstringに変換
                val responseMessage = if(responseCode == 200) {
                        val br = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        val sb = StringBuilder()

                        for (line: String? in br.readLines()) {
                            line?.let { sb.append(line) }
                        }
                        br.close()

                        sb.toString()
                    } else {
                        "[{}]"
                    }

                return Pair(responseCode, responseMessage)
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                return Pair(-1, "[{}]")
            }
        }


        // HTTPのGETリクエストの送信
        fun getHttpRequest(url: URL, token: String): Pair<Int, String> {
            Log.d(TAG, "start SentHttpRequest")

            try {
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                // 接続タイムアウト時間
                urlConnection.connectTimeout = 100000
                // レスポンスのデータの読み取りタイムアウト時間
                urlConnection.readTimeout = 100000
                urlConnection.setRequestProperty("Authorization", "Bearer $token")
                urlConnection.setRequestProperty("accept", "application/json")
                urlConnection.addRequestProperty("Content-Type", "Raw; charset=UTF-8")
                //リクエストのボディ送信の可否
                urlConnection.doOutput = false
                //レスポンスのボディ受信の可否
                urlConnection.doInput = true
                // リダイレクトの可否
                urlConnection.instanceFollowRedirects = true

                urlConnection.connect()



                val responseCode = urlConnection.responseCode
                Log.d(TAG, "HttpStatusCode: $responseCode")

                val responseMessage = if(responseCode == 200) {
                        val br = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        val sb = StringBuilder()

                        for (line: String? in br.readLines()) {
                            line?.let { sb.append(line) }
                        }
                        br.close()

                        sb.toString()
                    } else {
                        "[{}]"
                    }

                return Pair(responseCode, responseMessage)

            } catch (e: Exception) {
                Log.d(TAG, e.toString())
                return Pair(-1, "[{}]")
            }
        }


        // http の GET メソッドの結果を受け取りレスポンスコードによってメッセージを表示
        fun displayResponseMessage(context: Context, code: Int) {
            val message = when(code) {
                -1   -> context.getString(R.string.message_1)
                401  -> context.getString(R.string.message_401)
                402  -> context.getString(R.string.message_402)
                403  -> context.getString(R.string.message_403)
                404  -> context.getString(R.string.message_404)
                else -> context.getString(R.string.message_else)
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
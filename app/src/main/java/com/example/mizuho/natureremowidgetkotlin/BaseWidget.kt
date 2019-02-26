package com.example.mizuho.natureremowidgetkotlin

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.Toast
import android.os.Looper
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler


open class BaseWidget: AppWidgetProvider() {

    private val TAG = "T_BASEACTIVITY"

    override fun onUpdate(context:Context, appWidgetManager:AppWidgetManager, appWidgetIds:IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
        }
    }

    override fun onEnabled(context:Context) {
        // Enter relevant functionality for when the first widget is created
        }

    override fun onDisabled(context:Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    // ウィジェットIDを取得する関数
    fun getWidgetId(intent: Intent): Int {
        val extras = intent.extras
        var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }
        return appWidgetId
    }

    // load token from internal storage
    fun loadToken(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences("TOKEN", Context.MODE_PRIVATE)
        return prefs.getString("TOKEN", "nan") as String
    }

    // toastでメッセージを表示する関数
    private fun displayToast(context: Context, message: String) {
        Log.d(TAG, "displayToast()!!!!")

        val h = Handler(Looper.getMainLooper())
        Thread({
            Log.d(TAG, "thread run() called. thread name:" + Thread.currentThread().name)
            h.post{ Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
        }, "ToastThread").start()
    }

    // http の GET メソッドの結果を受け取りレスポンスコードによってメッセージを表示
    // ウィジェットであるため HttpRequest の displayResponseMessage() が使えないため
    fun displayResponseToastMessage(context: Context, code: Int) {
        val message = when(code) {
            -1   -> context.getString(R.string.message_1)
            401  -> context.getString(R.string.message_401)
            402  -> context.getString(R.string.message_402)
            403  -> context.getString(R.string.message_403)
            404  -> context.getString(R.string.message_404)
            else -> context.getString(R.string.message_else)
        }

        displayToast(context, message)
    }
}


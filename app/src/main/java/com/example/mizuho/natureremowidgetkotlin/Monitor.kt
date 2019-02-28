package com.example.mizuho.natureremowidgetkotlin

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL
import android.app.PendingIntent
import android.graphics.Color
import com.beust.klaxon.*


/**
 * Implementation of App Widget functionality.
 */
class Monitor : BaseWidget() {

    private val TAG = "T_MONITOR"
    private val BUTTON_PUSHED_ACTION = "android.appwidget.action.BUTTON_PUSHED"


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        Log.d(TAG, "onUpdate()")

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetId,0.0,0.0,0.0)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        private const val BUTTON_PUSHED_ACTION = "android.appwidget.action.BUTTON_PUSHED"

        internal fun updateAppWidget(
            context: Context,
            appWidgetId: Int, te: Double, il: Double, hu: Double
        ) {

            Log.d("T_MONITOR", "Monitor updateAppWidget()")

            val views = RemoteViews(
                context.packageName,
                R.layout.monitor
            )

            // ボタンの再設定
            val btnIntent = Intent(context, Monitor::class.java)
            btnIntent.action = BUTTON_PUSHED_ACTION
            btnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent =
                PendingIntent.getBroadcast(context, appWidgetId, btnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.roomMonitorWidgetBtn, pendingIntent)
            views.setTextColor(R.id.roomMonitorWidgetBtn, Color.BLACK)
            views.setTextViewText(R.id.roomMonitorWidgetBtn, "")


            // TextViewの設定
            views.setTextViewText(R.id.textViewTm, "te: %.1f".format(te) )
            views.setTextViewText(R.id.textViewIl, "il: %.1f".format(il))
            views.setTextViewText(R.id.textViewHu, "hu: %.1f".format(hu))

            views.setTextColor(R.id.textViewTm, Color.BLACK)
            views.setTextColor(R.id.textViewIl, Color.BLACK)
            views.setTextColor(R.id.textViewHu, Color.BLACK)



            // ウェジェットの更新
            val manager = AppWidgetManager.getInstance(context)
            manager.updateAppWidget(appWidgetId, views)

        }
    }


    // jsonのパース
    // エアコンの必要なデータのみ json で取得
    private fun parseJson(str: String) : Status {
        Log.d(TAG, "parseJson(): $str")

        // use Object binding API (Klaxon)
//        val result = Klaxon().parseArray<Devices>(str)
//
//        Log.d(TAG, result.toString())
//
//        val newestEvents = result?.get(0)?.newest_events
//        val hu = newestEvents?.hu
//        val il = newestEvents?.il
//        val te = newestEvents?.te
//
//        val huVal = hu?.`val`.asTypeDouble()
//        val ilVal = il?.`val`.asTypeDouble()
//        val teVal = te?.`val`.asTypeDouble()

        // use Low level API (Klaxon)
        val parser: Parser = Parser.default()
        val stringBuilder  = StringBuilder(str)
        val parseStr       = parser.parse(stringBuilder)
        val jsonArrayAny   = parseStr.asType<JsonArray<Any>>()
        val jsonArray      = jsonArrayAny?.asJsonArrayOfType<JsonObject>()
        val newestEvents   = jsonArray?.get(0)?.obj("newest_events")

        val hu = newestEvents?.obj("hu")
        val il = newestEvents?.obj("il")
        val te = newestEvents?.obj("te")

        val huDouble = hu?.filter { (k, v) -> k == "val" }
                         ?.map { it.value.asTypeDouble() }
                         ?.get(0) ?: 0.0
        val ilDouble = il?.filter { (k, v) -> k == "val" }
                         ?.map { it.value.asTypeDouble() }
                         ?.get(0) ?: 0.0
        val teDouble = te?.filter { (k, v) -> k == "val" }
                         ?.map { it.value.asTypeDouble() }
                         ?.get(0) ?: 0.0

//        Log.d(TAG, newestEvents.toString())
//        Log.d(TAG, "huDouble: $huDouble")
//        Log.d(TAG, "ilDouble: $ilDouble")
//        Log.d(TAG, "teDouble: $teDouble")

        return Status(hu=huDouble, il=ilDouble, te=teDouble)
    }

    private fun getJsonStr(context: Context): String {
        val token = loadToken(context)

        val response = runBlocking(Dispatchers.Default) {
            // デバイスの状態(温度、湿度、明るさなど)を取得
            val url = URL("https://api.nature.global/1/devices")
            HttpRequest.getHttpRequest(url, token)
        }
        Log.d(TAG, "$response")

        displayResponseToastMessage(context, response.first)

        return response.second
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive")

        val appWidgetId = getWidgetId(intent)

        // どのボタンが押されたのか
        val action = intent.action
        Log.d(TAG, "ACTION: " + action!!)

        if (action == BUTTON_PUSHED_ACTION) {
            val str = getJsonStr(context)
            Log.d(TAG, "str: $str")

            val status = parseJson(str)
            Log.d(TAG, status.toString())

            updateAppWidget(context, appWidgetId,status.te, status.il, status.hu)
        }
    }

//    private fun init(context: Context, appWidgetId: Int) {
//        Log.d(TAG, "init()")
//
//        val views = RemoteViews(
//            context.packageName,
//            R.layout.monitor
//        )
//
//        // ボタンの再設定
//        val btnIntent = Intent(context, Monitor::class.java)
//        btnIntent.action = BUTTON_PUSHED_ACTION
//        btnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//        val pendingIntent =
//            PendingIntent.getBroadcast(context, appWidgetId, btnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
//        views.setOnClickPendingIntent(R.id.roomMonitorWidgetBtn, pendingIntent)
//        views.setTextColor(R.id.roomMonitorWidgetBtn, Color.BLACK)
//        views.setTextViewText(R.id.roomMonitorWidgetBtn, "GET")
//
//
//        // TextViewの設定
//        views.setTextViewText(R.id.textViewTm, "")
//        views.setTextViewText(R.id.textViewIl, "")
//        views.setTextViewText(R.id.textViewHu, "")
//
//
//        // ウェジェットの更新
//        val manager = AppWidgetManager.getInstance(context)
//        manager.updateAppWidget(appWidgetId, views)
//    }
}


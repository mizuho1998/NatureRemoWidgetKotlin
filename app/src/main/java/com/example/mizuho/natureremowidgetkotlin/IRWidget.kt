package com.example.mizuho.natureremowidgetkotlin

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL


/**
 * Implementation of App Widget functionality.
 */
class IRWidget : BaseWidget() {
    private val TAG = "T_SW"


    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            Log.d(TAG, "onUpdate $appWidgetId")
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled")
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled")
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d(TAG, "onDelete " + appWidgetIds[0].toString())

        // 削除されたウィジェットの設定を削除
        val fileRW = FileRW(context)
        fileRW.clearFile("setting${appWidgetIds[0]}.txt")
    }

    companion object {
        val TAG = "T_companion_object"
        private val BUTTON_PUSHED_ACTION = arrayOf(
            "android.appwidget.action.BUTTON1_PUSHED",
            "android.appwidget.action.BUTTON2_PUSHED",
            "android.appwidget.action.BUTTON3_PUSHED",
            "android.appwidget.action.BUTTON4_PUSHED"
        )

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            Log.d("T_SW", "updateAppWidget: $appWidgetId")

            // get widget layout
            val views = RemoteViews(context.packageName, R.layout.ir_widget)

            // ボタンの設定データの読み込み
            val fileRW = FileRW(context)
            val data = fileRW.readFile("setting$appWidgetId.txt")

            if ( data.isEmpty()) { return }

            Log.d(TAG, "data: $data")

            // stringをjsonに変換
            val parser: Parser = Parser.default()
            val stringBuilder  = StringBuilder(data)
            val settingJson: JsonObject = parser.parse(stringBuilder) as JsonObject

            // アイコンの再設定
            val icon     = settingJson.string("icon")
//            val iconName = settingJson.string("name")

            Log.d(TAG, "icon: $icon")
//            Log.d(TAG, "iconName: $iconName")
            Log.d(TAG, "settingJson: $settingJson")

            val res = context.resources
            val iconResourceId = res.getIdentifier(icon, "drawable", context.packageName)
            views.setImageViewResource(R.id.image_view, iconResourceId)
//            views.setTextViewText(R.id.appwidget_text, iconName)
//            views.setTextColor(R.id.appwidget_text, Color.BLACK)

            // paddingの設定
            val paddingDp = 10.0f
            val scale = context.resources.displayMetrics.density
            val paddingPx = (paddingDp * scale + 0.5f).toInt()
            views.setViewPadding(R.id.image_view, paddingPx, paddingPx, paddingPx, paddingPx)


            // ボタンの再設定
            for (i in 0..3) {
                Log.d(TAG, "i: $i" )

                val btnData = settingJson.obj("button$i")
                Log.d(TAG, "btnData: $btnData")

                val buttonIconName = btnData!!.string("buttonIcon")
                Log.d(TAG, "buttonIconName: $buttonIconName")

                // ボタンの再設定
                val btnIntent = Intent(context, IRWidget::class.java)
                btnIntent.action = BUTTON_PUSHED_ACTION[i]
                btnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

                val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, btnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
                val buttonId = res.getIdentifier("button" + (i + 1), "id", context.packageName)  // ボタンIDを取得
                views.setOnClickPendingIntent(buttonId, pendingIntent)

                // ボタンに画像をセット
                val resourceId = res.getIdentifier(buttonIconName, "drawable", context.packageName)
                views.setImageViewResource(buttonId, resourceId)

                // ボタンの画像のpaddingの設定
                views.setViewPadding(buttonId, paddingPx, paddingPx, paddingPx, paddingPx)
            }

            // ウェジェットの更新
            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d("TEST_WIDGET", "onReceive")

        val appWidgetId = getWidgetId(intent)
        val action = intent.action
        Log.d(TAG, "appWidgetId: $appWidgetId, ACTION: " + action!!)


        // ボタンが押された場合のみ処理を実行
        for (i in 0..3) {
            if (BUTTON_PUSHED_ACTION[i] != action) continue

            // ボタンの設定データの読み込み
            val fileRW = FileRW(context)
            val data = fileRW.readFile("setting$appWidgetId.txt")
            Log.d(TAG, "data: $data")

            if (data.isEmpty()) { break }

            // get setting info
            val parser: Parser = Parser.default()
            val stringBuilder = StringBuilder(data)
            val settingJson: JsonObject = parser.parse(stringBuilder) as JsonObject
            Log.d(TAG, "settingJson: $settingJson")
            val btnData = settingJson.obj("button$i")
            Log.d(TAG, "btnData: $btnData")
            val signalId = btnData!!.string("signalId")
            Log.d(TAG, "signalId: $signalId")


            // nature remo で信号の送信
            if (signalId == "") {
                Log.d(TAG, "id is \"\". not sent request")
//                displayToast(context, "")
                break
            }

            // post HTTP request
            val url = URL("https://api.nature.global/1/signals/$signalId/send")
            val token = loadToken(context)
            val response = runBlocking(Dispatchers.Default) {
                Log.d(TAG, "postHttpRequest from IRWidget")
                HttpRequest.postHttpRequest(url, token)
            }

            // 結果を Toast で表示
            displayResponseToastMessage(context, response.first)

            // ボタンの再設定
            val manager = AppWidgetManager.getInstance(context)
            updateAppWidget(context, manager, appWidgetId)

            break
        }

    }
}


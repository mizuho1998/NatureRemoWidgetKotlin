package com.example.mizuho.natureremowidgetkotlin

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL
import android.app.PendingIntent
import android.widget.RemoteViews
import android.graphics.Color
import android.view.View
import com.beust.klaxon.*
import java.lang.Exception


/**
 * Implementation of App Widget functionality.
 */
class ACWidget : BaseWidget() {
    private val TAG = "T_AC_WIDGET"

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            Log.d(TAG, "onUpdate(): appWidgetId: $appWidgetId")

            // ウィジェットの　View を現在の設定と同期させる
            initView(context, appWidgetId)
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
        fileRW.clearFile("acSetting${appWidgetIds[0]}.txt")
    }

    companion object {
        private const val TAG = "T_AC_WIDGET_COM"
        private const val plusBtnAction   = "android.appwidget.action.PLUS_BUTTON_ACTION"
        private const val minusBtnAction  = "android.appwidget.action.MINUS_BUTTON_ACTION"
        private const val airVolumeAction = "android.appwidget.action.AIR_VOLUME_BUTTON_ACTION"
        private const val onOffAction     = "android.appwidget.action.ON_OFF_BUTTON_ACTION"
        private const val modeAction      = "android.appwidget.action.MODE_BUTTON_ACTION"


        internal fun updateAppWidget(
            context: Context, appWidgetId: Int, settingData: SettingData
        ) {
            // 設定データ
            val temp = settingData.temp
            val vol  = settingData.vol
            val mode = settingData.mode
            val button = settingData.button

            Log.d(TAG, "SetWidget")

            val views = RemoteViews(
                context.packageName,
                R.layout.ac_widget
            )

            // airVolumeボタンの設定
            val aieVolumeBtnIntent = Intent(context, ACWidget::class.java)
            aieVolumeBtnIntent.action = airVolumeAction
            aieVolumeBtnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val airVolumeBtnPendingIntent =
                PendingIntent.getBroadcast(context, appWidgetId, aieVolumeBtnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.airVolumeBtn, airVolumeBtnPendingIntent)

//            views.setImageViewResource(R.id.airVolumeBtn, R.drawable.button_icon_pm_puls)

            views.setTextViewText(R.id.airVolumeTextView, vol)
            views.setTextColor(R.id.airVolumeTextView, Color.BLACK)


            // +,-ボタンの設定
            val plusBtnIntent = Intent(context, ACWidget::class.java)
            plusBtnIntent.action = plusBtnAction
            plusBtnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val plusBtnPendingIntent =
                PendingIntent.getBroadcast(context, appWidgetId, plusBtnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.plusBtn, plusBtnPendingIntent)
            views.setImageViewResource(R.id.plusBtn, R.drawable.button_icon_pm_puls)

            val minusBtnIntent = Intent(context, ACWidget::class.java)
            minusBtnIntent.action = minusBtnAction
            minusBtnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val minusBtnPendingIntent =
                PendingIntent.getBroadcast(context, appWidgetId, minusBtnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.minusBtn, minusBtnPendingIntent)
            views.setImageViewResource(R.id.minusBtn, R.drawable.button_icon_pm_minus)


            // 温度表示のTextViewの設定
            views.setTextViewText(R.id.textView, temp)
            views.setTextColor(R.id.textView, Color.BLACK)

            // モード表示のTextViewの設定
            views.setTextViewText(R.id.modeTextView, mode)
            views.setTextColor(R.id.modeTextView, Color.BLACK)


            // on/offボタンの設定
            val onOffBtnIntent = Intent(context, ACWidget::class.java)
            onOffBtnIntent.action = onOffAction
            onOffBtnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val onOffBtnPendingIntent =
                PendingIntent.getBroadcast(context, appWidgetId, onOffBtnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.onOffBtn, onOffBtnPendingIntent)
            if (button == "") {
                views.setTextViewText(R.id.onOffBtn, "ON")
            } else {
                views.setTextViewText(R.id.onOffBtn, "OFF")
            }
            views.setTextColor(R.id.onOffBtn, Color.BLACK)


            // モード変更ボタン（アイコンが表示されているimageButton）の設定
            val modeBtnIntent = Intent(context, ACWidget::class.java)
            modeBtnIntent.action = modeAction
            modeBtnIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val modeBtnPendingIntent =
                PendingIntent.getBroadcast(context, appWidgetId, modeBtnIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            views.setOnClickPendingIntent(R.id.iconBtn, modeBtnPendingIntent)
            views.setImageViewResource(R.id.iconBtn, R.drawable.device_icon_airconditioner_black)

            // paddingの設定
            val paddingDp = 10.0f
            val scale = context.resources.displayMetrics.density
            val paddingPx = (paddingDp * scale + 0.5f).toInt()
            views.setViewPadding(R.id.iconBtn, paddingPx, paddingPx, paddingPx, paddingPx)


            views.setViewVisibility(R.id.widget_background_cool, View.INVISIBLE)
            views.setViewVisibility(R.id.widget_background_warm, View.INVISIBLE)
            views.setViewVisibility(R.id.widget_background_auto, View.INVISIBLE)
            views.setViewVisibility(R.id.widget_background_blow, View.INVISIBLE)
            views.setViewVisibility(R.id.widget_background_dry, View.INVISIBLE)

            // 背景色の変更
            if (button == "") {
                when (mode) {
                    "cool" -> views.setViewVisibility(R.id.widget_background_cool, View.VISIBLE)
                    "warm" -> views.setViewVisibility(R.id.widget_background_warm, View.VISIBLE)
                    "auto" -> views.setViewVisibility(R.id.widget_background_auto, View.VISIBLE)
                    "blow" -> views.setViewVisibility(R.id.widget_background_blow, View.VISIBLE)
                    "dry" -> views.setViewVisibility(R.id.widget_background_dry, View.VISIBLE)
                }
            }

            // ウェジェットの更新
            val manager = AppWidgetManager.getInstance(context)
            manager.updateAppWidget(appWidgetId, views)
        }
    }



    // http get リクエストを実行し、レスポンスを取得
    private fun getJsonStr(context: Context) : String {
//        var response: Pair<Int, String> = Pair(0, "")
        val token = loadToken(context)

        val response = runBlocking(Dispatchers.Default) {
            val url = URL("https://api.nature.global/1/appliances")
            HttpRequest.getHttpRequest(url, token)
//            Log.d(TAG, "getJsonStr(): $response")
        }

        displayResponseToastMessage(context, response.first)

        return response.second
    }


    // jsonのパース
    // エアコンの必要なデータのみ json で取得
    private fun parseJson(str: String, id: String): JsonObject {
        Log.d(TAG, "parseJson(): str:  $str")

        if(str == "") return json { obj() }

        try {
            // stringをjsonに変換
            val parser: Parser = Parser.default()
            val stringBuilder  = StringBuilder(str)
            val parseStr       = parser.parse(stringBuilder)
            val jsonArrayAny   = parseStr.asType<JsonArray<Any>>()
            val jsonArray      = jsonArrayAny?.asJsonArrayOfType<JsonObject>()
            val acJsonArray    = jsonArray?.filter { it.string("id").equals(id) }   // id が id のものだけ取得

            Log.d(TAG, "acJsonArray: $acJsonArray")

            val ac = acJsonArray?.map { json { obj   ( "nickname" to it.string("nickname"),
                "id" to it.string("id"),
                "settings" to it.obj("settings"),
                "modes" to it.obj("aircon")?.obj("range")?.obj("modes")
            ) } }
            Log.d(TAG, "ac: $ac")

            return ac?.get(0) ?: json { obj() }
        } catch (e: Exception) {
            return json { obj() }
        }

    }


    // 設定されたエアコンのjsonデータのみ取得
    private fun getJson(context: Context, appWidgetId: Int): JsonObject {
        val jsonStr = getJsonStr(context)

        // ボタンの設定データの読み込み
        val fileRW = FileRW(context)
        val id = fileRW.readFile("acSetting$appWidgetId.txt")
        Log.d(TAG, "read id: $id")

        if(id == "") { return json { obj() }}

        return parseJson(jsonStr, id)
    }


    private fun getNowParams(json: JsonObject): SettingData {

        val nowTemp   = json.string("temp")    ?: ""
        val nowMode   = json.string("mode")    ?: ""
        val nowVol    = json.string("vol")     ?: ""
        val nowDir    = json.string("dir")     ?: ""
        val nowButton = json.string("button")  ?: ""

        Log.d(TAG, "info: $json")
        Log.d(TAG, "$nowTemp, $nowMode, $nowVol, $nowDir, $nowButton")

        return SettingData(temp=nowTemp, mode=nowMode, vol=nowVol, dir=nowDir, button=nowButton)
    }

    private fun getSetParams(nowParams: SettingData, json: JsonObject, action: String): SettingData {

        if(json.isEmpty()) return SettingData()

        val settingData = nowParams.copy()
        val nowMode     = nowParams.mode
        val nowTemp     = nowParams.temp
        val nowVol      = nowParams.vol
        val nowButton   = nowParams.button

        when(action){
            plusBtnAction -> {
                Log.d(TAG, "plusBtnAction")

                val modeSettings = json.obj(nowMode)
                val tmpList      = modeSettings?.array<String>("temp")
                val setTempList  = tmpList?.dropWhile { it <= nowTemp }
                val setTemp      =  if (setTempList?.isEmpty() == true) ""      // setTempList が null でないと且つ空の時
                                    else setTempList?.get(0) ?: ""              // setTempList が null ならば "" を返す

                Log.d(TAG, setTempList.toString())
                Log.d(TAG, "setTemp: $setTemp")

                settingData.temp = setTemp
            }
            minusBtnAction -> {
                Log.d(TAG, "minusBtnAction")

                val modeSettings = json.obj(nowMode)
                val tmpList      = modeSettings?.array<String>("temp")
                val setTempList  = tmpList?.takeWhile { it < nowTemp }
                val setTemp      =  if (setTempList?.isEmpty() == true) ""                  // setTempList が null でないと且つ空の時
                                    else setTempList?.get(setTempList.size - 1) ?: ""   // setTempList が null ならば "" を返す

                settingData.temp = setTemp
            }
            airVolumeAction -> {
                Log.d(TAG, "airVolumeAction")

                val modeSettings  = json.obj(nowMode)
                val volList       = modeSettings?.array<String>("vol")
                val setVolList    = volList?.takeWhile { it <= nowVol }
                val volListLen    = volList?.size ?: 1
                val setVolListLen = setVolList?.size ?: 0
                val setVol        = volList?.get( setVolListLen % volListLen ) ?: ""

                settingData.vol = setVol
            }
            onOffAction -> {
                Log.d(TAG, "onOffAction")

                val setButton = if(nowButton == "") "power-off" else ""

                settingData.button = setButton
            }
            modeAction -> {
                Log.d(TAG, "modeAction")

                // モードを変更
                val modeList       = json.map{ it.key }
                val modeListLen    = modeList.size
                val setMOdeList    = modeList.takeWhile { it <= nowMode }
                val setModeListLen = setMOdeList.size
                val setMode        = modeList[setModeListLen % modeListLen]

                // 変更後のモードの設定可能温度を設定
                val modeSettings = json.obj(setMode)
                val tmpList      = modeSettings?.array<String>("temp")
                val setTemp      =  when {
                    (tmpList == null) -> ""
                    (tmpList.isEmpty()) -> ""                  // setTempList が null でないと且つ空の時
                    else -> tmpList[tmpList.size / 2]
                }

                Log.d(TAG, "setMode: $setMode")
                Log.d(TAG, "setTemp: $setTemp")

                settingData.mode = setMode
                settingData.temp = setTemp
            }
        }

        return settingData
    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(TAG, "onReceive")

        val appWidgetId = getWidgetId(intent)

        // どのボタンが押されたのか
        val action = intent.action
        Log.d(TAG, "ACTION: " + action!!)

        if (action != "android.appwidget.action.APPWIDGET_UPDATE") {
            val jsonObj = getJson(context, appWidgetId)

            val id       = jsonObj.string("id")
            val settings = jsonObj.obj("settings") ?: json{obj()}
            val modes    = jsonObj.obj("modes")    ?: json{obj()}

            val nowParams = getNowParams(settings)
            val setParams = getSetParams(nowParams, modes, action)

            Log.d(TAG, "nowParams: $nowParams")
            Log.d(TAG, "setParams: $setParams")


            var response: Pair<Int, String> = Pair(0, "")
            val token = loadToken(context)
            val requestBody = setParams.toRequestBody()

            // 信号の送信
            runBlocking(Dispatchers.Default) {
                // エアコンに信号を送信
                val url = URL("https://api.nature.global/1/appliances/$id/aircon_settings")
                Log.d(TAG, "url: $url, token: $token")
                response = HttpRequest.postHttpRequest(url, token, requestBody)
                Log.d(TAG, "$response")

                displayResponseToastMessage(context, response.first)
            }

            if(response.first == 200) {
                updateAppWidget(context, appWidgetId, setParams)
            } else {
                updateAppWidget(context, appWidgetId, nowParams)
            }
        }
    }


    private fun initView(context: Context, appWidgetId: Int) {
        // 設定されてたエアコンのデータのみ抽出
        val jsonObj  = getJson(context, appWidgetId)
        val settings = jsonObj.obj("settings") ?: json{obj()}

        val nowParams = getNowParams(settings)
        Log.d(TAG, "nowParams: $nowParams")

        updateAppWidget(context, appWidgetId, nowParams)
    }
}
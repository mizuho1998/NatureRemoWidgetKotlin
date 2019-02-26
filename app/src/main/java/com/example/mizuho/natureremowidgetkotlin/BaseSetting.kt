package com.example.mizuho.natureremowidgetkotlin

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URL


open class BaseSetting : AppCompatActivity() {

    private val TAG = "T_BASE_SETTING"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ir_widget_setting)
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
    fun loadToken(): String {
        val prefs: SharedPreferences = getSharedPreferences("TOKEN", Context.MODE_PRIVATE)
        return prefs.getString("TOKEN", "nan") as String
    }

    // Nature Rewmo のサーバーからのレスポンスを取得
    fun getJsonStr() : String{
        var response: Pair<Int, String> = Pair(0, "")
        val token = loadToken()

        // トークンが設定されていない場合はこの処理を中断
        if(token == "nan") {
            Toast.makeText(this@BaseSetting, getString(R.string.note_token), Toast.LENGTH_LONG).show()
            return "[]"
        }

        // Nature Remo のサーバーから登録した信号の情報を取得
        runBlocking(Dispatchers.Default) {
            // spinner に表示する信号を一覧を取得
            val url = URL("https://api.nature.global/1/appliances")
            response = HttpRequest.getHttpRequest(url, token)
            Log.d(TAG, "getJson(): $response")
        }

        // http の GET メソッドの結果を受け取りレスポンスコードによってメッセージを表示
        HttpRequest.displayResponseMessage(this@BaseSetting, response.first)

        return response.second
    }


    // str: json 形式の文字列 (getJsonStr() で取得した文字列)
    // json　の　キー type が引数の type であるものを取得
    fun parseJsonDevices(str: String, type: String): List<JsonObject> {
        Log.d(TAG, "str: $str")

        // stringをjsonに変換
        try{
            // stringをjsonに変換
//            val result       = Klaxon().parseArray<Appliances>(str)
//            val acJsonArrays = result?.filter { it -> it.type == "AC" } ?: listOf()  // type が "AC" のものだけ取得
//            val ac           = acJsonArrays.map { json { obj   ("id" to it.id,
//                "nickname" to it.nickname,
//                "settings" to it.settings,
//                "modes" to it.aircon?.range?.modes
//            ) } }

            val parser: Parser = Parser.default()
            val stringBuilder  = StringBuilder(str)
            val anyObj         = parser.parse(stringBuilder)
            val jsonArrayAny   = anyObj.asType<JsonArray<Any>>()                  // JsonArray<*> 型に変換
            val jsonArray      = jsonArrayAny?.asJsonArrayOfType<JsonObject>()  // JsonArray<JsonObject> 型に変換
            val devicesJsonList = jsonArray?.filter {it -> it.string("type") == type } ?: listOf()

            Log.d(TAG, "parseJsonDevices(): ${devicesJsonList.toString()}")

            return devicesJsonList

        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            return listOf()
        }
    }
}
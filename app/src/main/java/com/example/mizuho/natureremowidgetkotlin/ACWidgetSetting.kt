package com.example.mizuho.natureremowidgetkotlin

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.beust.klaxon.*
import kotlinx.android.synthetic.main.activity_ac_widget_setting.*

class ACWidgetSetting : BaseSetting() {

    private val TAG = "T_AC_SETTING"
    private var appWidgetId  = AppWidgetManager.INVALID_APPWIDGET_ID
    private val REQUEST_CODE = 102 // リクエストコード（呼び出しActivity識別用）


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ac_widget_setting)

        // appWidgetId を取得
        appWidgetId = getWidgetId(intent)
        Log.d(TAG, "IRWidgetSetting sppWidgetId $appWidgetId")

        // spinner, button など View の設定
        setView()
    }


    // jsonのパース
    // エアコンの情報のみを json で取得
    private fun parseJson(str: String): List<JsonObject> {
        try{
            // stringをjsonに変換
            val acJsonArrays   = parseJsonDevices(str, "AC")

            val ac = acJsonArrays.map { json { obj   ("id" to it.string("id"),
                "nickname" to it.string("nickname"),
                "settings" to it.obj("settings"),
                "modes" to it.obj("aircon")?.obj("range")?.obj("modes")
            ) } }

            return ac

        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            return listOf()
        }
    }


    // spinner の設定
    private fun setSpinner(array: Array<Pair<String, String>>) {
        // spinner の設定
        val spinnerAdapter = KeyValueAdapter(this, android.R.layout.simple_spinner_item, array)
        spinner.adapter = spinnerAdapter
    }


    // ボタンの設定
    private fun setButton(json: List<JsonObject>) {

        // OKボタンの設定
        button.setOnClickListener{
            // spinner のアダプターからキーの値を取得
            val selectedPosition = spinner.selectedItemPosition
            val spinnerAdapter   = spinner.adapter as KeyValueAdapter
            val selectedId       = spinnerAdapter.getKey(selectedPosition)

            // 設定情報の保存
            val fileRW = FileRW(this)
            fileRW.writeFileMessage("acSetting$appWidgetId.txt", selectedId)

            // 設定するエアコンの現在の設定情報の取得
            val acJsonArray = json.filter { it -> it.string("id").equals(selectedId) }   // id が selectedId のものだけ取得
            val ac = acJsonArray.map { it -> json { obj   ("settings" to it.obj("settings")) } }
            val acObj = ac[0]

            val nowSettingJson = acObj.obj("settings")
            val nowTemp   = nowSettingJson?.string("temp")   ?: ""
            val nowMode   = nowSettingJson?.string("mode")   ?: ""
            val nowVol    = nowSettingJson?.string("vol")    ?: ""
            val nowDir    = nowSettingJson?.string("dir")    ?: ""
            val nowButton = nowSettingJson?.string("button") ?: ""
            val nowSettingData = SettingData(temp=nowTemp, mode=nowMode, vol=nowVol, dir=nowDir, button=nowButton)

            Log.d(TAG, "nowSettingData: ${nowSettingData.toRequestBody()}")

            // ウィジェットの更新
            ACWidget.updateAppWidget(this, appWidgetId, nowSettingData)

            // 結果をウィジェットに返し、この設定用アクティビティを終了
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }

        // 設定の説明アクティビティを開くボタンの設定
        setupDescriptionBtn.setOnClickListener{
            // トークンボタンの設定
            val intent = Intent(this@ACWidgetSetting, SetupDescription::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        // トークンを取得するボタンの設定
        getTokenBtn.setOnClickListener{
            // Nature Remo のトークン設定ページをウェブブラウザで開く
            val uri = Uri.parse("https://home.nature.global/")
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }


        // トークンボタンの設定
        setTokenBtn.setOnClickListener{
            val intent = Intent(this@ACWidgetSetting, SetToken::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }


    // View の設定
    private fun setView() {
        val jsonStr = getJsonStr()
        val json    = parseJson(jsonStr)

        // エアコンの (id, nickname) のリストを作成
        val acNameList            = json.map{ Pair(it.string("id") , it.string("nickname")) }
        val acNameListNotNullable = acNameList.map { it.asPairOf() }   // 型の変換（not nullable に変換）
        val acNameArray           = acNameListNotNullable.toTypedArray()

        setSpinner(acNameArray)
        setButton(json)
    }


    // SetToken activity の結果を受け取り View の再描画
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Activity側のonActivityResultで呼ばないとFragmentのonActivityResultが呼ばれない
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> {
                // 呼び出し先のActivityから結果を受け取る
                Log.d(TAG, "requestCode: $requestCode, resultCode: $resultCode")

                // spinner, button など View の再設定
                setView()
            }
        }
    }

}

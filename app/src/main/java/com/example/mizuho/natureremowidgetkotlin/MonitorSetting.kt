package com.example.mizuho.natureremowidgetkotlin

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_monitor_setting.*

class MonitorSetting : BaseSetting() {

    private val TAG = "T_MONITOR_SETTING"
    private val REQUEST_CODE = 103 // リクエストコード（呼び出しActivity識別用）

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_setting)

        // appWidgetId を取得
        val appWidgetId = getWidgetId(intent)


        val token = loadToken()
        // トークの設定状況を表示
        tokenStatusDisplay(token)


        // 設定の説明アクティビティを開くボタンの設定
        setupDescriptionBtn.setOnClickListener{
            // トークンボタンの設定
            val intent = Intent(this@MonitorSetting, SetupDescription::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        // トークンを取得するボタンの設定
        getTokenBtn.setOnClickListener{
            // Nature Remo のトークン設定ページをウェブブラウザで開く
            val uri = Uri.parse("https://home.nature.global/")
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }

        // トークンを設定するボタンの設定
        setTokenBtn.setOnClickListener{
            // トークンボタンの設定
            val intent = Intent(this@MonitorSetting, SetToken::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }


        // OK ボタンの設定
        okBtn.setOnClickListener{

            // ウィジェットの更新
            Monitor.updateAppWidget(this, appWidgetId, 0.0, 0.0, 0.0)

            // 結果をウィジェットに返し、この設定用アクティビティを終了
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }

    // トークンの設定状況を確認し、TextView にメッセージを表示する
    private fun tokenStatusDisplay(token: String) {
        if(token == "nan" || token == "") {
            Toast.makeText(this@MonitorSetting, "set token!", Toast.LENGTH_LONG).show()
            textView.text = getString(R.string.set_yet)
        } else {
            textView.text = getString(R.string.already_set)
        }
    }


    // SetToken activity の結果を受け取り View の再描画
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Activity側のonActivityResultで呼ばないとFragmentのonActivityResultが呼ばれない
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> {
                // 呼び出し先のActivityから結果を受け取る
                Log.d(TAG, "requestCode: $requestCode, resultCode: $resultCode")
            }
        }
    }
}

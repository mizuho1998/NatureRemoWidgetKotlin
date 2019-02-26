package com.example.mizuho.natureremowidgetkotlin

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_ir_widget_setting.*
import android.widget.Spinner
import com.beust.klaxon.*
import java.lang.Exception


class IRWidgetSetting : BaseSetting() {

    private val TAG = "T_SW_SETTING"
    private var appWidgetId  = AppWidgetManager.INVALID_APPWIDGET_ID
    private val REQUEST_CODE = 101 // リクエストコード（呼び出しActivity識別用）

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ir_widget_setting)


        // appWidgetId を取得
        appWidgetId = getWidgetId(intent)
        Log.d(TAG, "IRWidgetSetting sppWidgetId $appWidgetId")


        // spinner など View の設定
        updateView()
    }


    // ボタンの設定
    private fun setButton() {

        // OKボタンの設定
        button.setOnClickListener{
            val btnIcons   = arrayOf("", "", "", "")   // ウィジェットのボタンアイコンの名前を保存
            val btnSignals = arrayOf("", "", "", "")   // ウィジェットのボタンの信号のidを保存

            // ウィジェットに設定するデバイスのアイコン、名前の情報
            val devIconAdapter = devIconSpinner.adapter as KeyValueImageAdapter
            val devPosition    = devIconSpinner.selectedItemPosition
            val devIcon = devIconAdapter.getKey(devPosition)
            val devName = devIcon.split("_")[2]

            // 設定するボタンのアイコンのファイル名を取得
            val btnIconSpinnerAdapter = icon_spinner0.adapter as KeyValueImageAdapter
            for (i in 0..3) {
                val btnIconSpinnerId = resources.getIdentifier("icon_spinner$i", "id", packageName)
                val btnIconSpinner   = findViewById<Spinner>(btnIconSpinnerId)
                val position = btnIconSpinner.selectedItemPosition
                val id       = btnIconSpinnerAdapter.getKey(position)
                btnIcons[i] = id

                Log.d(TAG, "i: $i,  id: $id")
            }

            // 設定するボタンの信のid号を取得
            val btnSignalSpinnerAdapter = signal_spinner0.adapter as KeyValueAdapter
            for (i in 0..3) {
                val btnSignalSpinnerId      = resources.getIdentifier("signal_spinner$i", "id", packageName)
                val btnSignalSpinner        = findViewById<Spinner>(btnSignalSpinnerId)
                val position = btnSignalSpinner.selectedItemPosition
                val id = btnSignalSpinnerAdapter.getKey(position)
                btnSignals[i] = id
            }

            val buttonInfo0 = ButtonInfo(buttonIcon=btnIcons[0], signalId=btnSignals[0])
            val buttonInfo1 = ButtonInfo(buttonIcon=btnIcons[1], signalId=btnSignals[1])
            val buttonInfo2 = ButtonInfo(buttonIcon=btnIcons[2], signalId=btnSignals[2])
            val buttonInfo3 = ButtonInfo(buttonIcon=btnIcons[3], signalId=btnSignals[3])
            val iRWidgetSettingData = IRWidgetSettingData(name=devName,
                                                        icon=devIcon,
                                                        button0=buttonInfo0,
                                                        button1=buttonInfo1,
                                                        button2=buttonInfo2,
                                                        button3=buttonInfo3 )

            Log.d(TAG, iRWidgetSettingData.toString())


            // 設定情報の保存
            val fileRW = FileRW(this)
            fileRW.writeFileMessage("setting$appWidgetId.txt", iRWidgetSettingData.toString())

            // 確認
//            val str = fileRW.readFile("setting$appWidgetId.txt")
//            Log.d(TAG, "save data:  $str")
//            val parser: Parser = Parser.default()
//            val stringBuilder  = StringBuilder(str)
//            val jsonObj: JsonObject = parser.parse(stringBuilder) as JsonObject
//            Log.d(TAG, jsonObj.toString())


            // ウィジェットの更新
            val manager = AppWidgetManager.getInstance(this)
            IRWidget.updateAppWidget(this, manager, appWidgetId)

            // 結果をウィジェットに返し、この設定用アクティビティを終了
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }

        // 設定の説明アクティビティを開くボタンの設定
        setupDescriptionBtn.setOnClickListener{
            // トークンボタンの設定
            val intent = Intent(this@IRWidgetSetting, SetupDescription::class.java)
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
            val intent = Intent(this@IRWidgetSetting, SetToken::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }


    // デバイスのアイコンの spinner の設定
    private fun setDeviceIconSpinner() {
        // 画像リソースを取得する
        val fields = R.drawable::class.java.fields
        val devIconFieldList = fields.filter{field -> field.name.startsWith("device_icon")} // デバイスのアイコンの画像のフィールドを取得
        val devIconList      = devIconFieldList.map{field -> field.name}                        // デバイスのアイコンの画像のファイル名を取得
        val devIconArray     = devIconList.toTypedArray()                                       // Arrayに変換

        // デバイス設定用の adapter の設定
        val devSpinnerAdapter = KeyValueImageAdapter(this, R.layout.spinner_dev_icon_list, devIconArray)
        devIconSpinner.adapter = devSpinnerAdapter
//        devIconSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            //　アイテムが選択された時
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                Log.d(TAG, "position: $position, id: $id")
//                Log.d(TAG, "getId(): ${devSpinnerAdapter.getKey(position)}")
//            }
//
//            //　アイテムが選択されなかった
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
    }


    // ボタンのアイコンの spinner の設定
    private fun setButtonIconSpinner() {
        // 画像リソースを取得する
        val fields = R.drawable::class.java.fields
        val btnIconFieldList = fields.filter{field -> field.name.startsWith("button_icon")} // ボタンのアイコンの名前のフィールドを取得
        val btnIconList      = btnIconFieldList.map{field -> field.name}                        // ボタンのアイコン名前のファイル名を取得
        val btnIconArray     = btnIconList.toTypedArray()                                       // Arrayに変換

        // ボタンのアイコンの設定用の spinner の adapter
        val btnIconSpinnerAdapter = KeyValueImageAdapter(this, R.layout.spinner_icon_list, btnIconArray)

        // ボタン4つに設定
        for (i in 0 until 4) {
            // ボタンのアイコンの設定用の adapter の設定
            val btnIconSpinnerId   = resources.getIdentifier("icon_spinner$i", "id", packageName)
            val btnIconSpinner     = findViewById<Spinner>(btnIconSpinnerId)
            btnIconSpinner.adapter = btnIconSpinnerAdapter
        }
    }


    // ボタンの信号の spinner の設定
    private fun setButtonSignalSpinner(array: Array<Pair<String, String>>) {
        Log.d(TAG, "setButtonSignalSpinner")

        // ボタンの信号の設定用の spinner の adapter
        val btnSignalSpinnerAdapter = KeyValueAdapter(this, android.R.layout.simple_spinner_item, array)

        // ボタン4つに設定
        for (i in 0 until 4) {
            // ボタンの信号の設定用の adapter の設定
            val btnSignalSpinnerId = resources.getIdentifier("signal_spinner$i", "id", packageName)
            val btnSignalSpinner       = findViewById<Spinner>(btnSignalSpinnerId)
            btnSignalSpinner.adapter = btnSignalSpinnerAdapter
        }
    }


    // jsonのパース
    // 信号の name,id をリストで返す
    private fun parseJson(str: String): List<JsonObject> {
        try {
            // str から type が IR であるもののみを取得
            val irJsonArrays   = parseJsonDevices(str, "IR")

            // 最終的に return する配列
            // 信号の name, id を格納
            val allSignalJsonList: MutableList<JsonObject> = mutableListOf()
            allSignalJsonList.add(json { obj("id" to "", "nickname-name" to getString(R.string.not_set)) }) // 無効ボタン用の項目の追加

            // name,id を取得
            for (irJson in irJsonArrays) {
                val nickname = irJson.string("nickname")
                val signals  = irJson.array<JsonObject>("signals")

                // 必要なデータのみ取得し整形
                val signalJsonList = signals?.map {
                    json { obj(
                        "id" to it.string("id"),
                        "nickname-name" to "$nickname-${it.string("name")}"
                    ) }
                }

                Log.d(TAG, signalJsonList.toString())

                // allSignalJsonList　に追加
                signalJsonList?.forEach { allSignalJsonList.add(it) }
            }

            Log.d(TAG, "parseJson(): $allSignalJsonList")

            return allSignalJsonList

        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            return listOf()
        }
    }


    // View のコンポーネントの描画
    private fun updateView() {
        // Nature Remo サーバーからのレスポンスデータを取得
        val jsonStr = getJsonStr()

        // json のパース
        val signalJson = parseJson(jsonStr)                                                                         // 全てのデバイスの信号の情報を一つの Json の　List として取得
        val signalList = signalJson.map { Pair(it.string("id") , it.string("nickname-name")) }  // Json の List を Pair の List に変換
        val acNameListNotNullable = signalList.map { it.asPairOf() }                                                // 型の変換（not nullable に変換）
        val signalArray = acNameListNotNullable.toTypedArray()

        if(signalArray.isEmpty()) { return }

        // spinner の設定
        setDeviceIconSpinner()
        setButtonIconSpinner()
        setButtonSignalSpinner(signalArray)
        setButton()
    }


    // SetToken activity の結果を受け取り View の再描画
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Activity側のonActivityResultで呼ばないとFragmentのonActivityResultが呼ばれない
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE -> {
                // 呼び出し先のActivityから結果を受け取る
                // nature remoのサーバからリモコンの情報を取得してそれらを選択肢に追加する
                Log.d(TAG, "requestCode: $requestCode, resultCode: $resultCode")
                updateView()
            }
        }
    }
}
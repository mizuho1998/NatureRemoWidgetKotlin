package com.example.mizuho.natureremowidgetkotlin

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_set_token.*

class SetupDescription : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_description)



        button.setOnClickListener {
            // 前のアクティビティに戻る
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }


    }
}

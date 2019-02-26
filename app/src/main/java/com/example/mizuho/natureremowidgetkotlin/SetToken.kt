package com.example.mizuho.natureremowidgetkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.android.synthetic.main.activity_set_token.*


class SetToken : Activity() {
    val TAG = "T_SET_TOKEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_token)

        Log.d(TAG, "SET TOKEN ACTIVITY")

        button.setOnClickListener {
            val prefs: SharedPreferences = getSharedPreferences("TOKEN", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            val token = editText.text.toString()
            editor.putString("TOKEN", token)
            editor.apply()  // 書き込みを確定


            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }
}
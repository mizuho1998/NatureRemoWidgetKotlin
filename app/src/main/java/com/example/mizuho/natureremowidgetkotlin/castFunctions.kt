package com.example.mizuho.natureremowidgetkotlin

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.json

// Pair<String?, String?> を Pair<String, String>　に安全にキャストするために定義
fun Pair<String?, String?>.asPairOf(): Pair<String, String> =
    if (first !is String || second !is String) "" to ""
    else first as String to second as String

// 型のわからない値が T 型であれば変換
// そうでなければ null を返す
inline fun <reified T> Any?.asType(): T? =
    if (this !is T) null else this

inline fun <reified T> Any?.asJsonType(): T? =
    if (this !is JsonObject) null else this as T

inline fun <reified T> JsonArray<Any>.asJsonArrayOfType(): JsonArray<T>? =
    if (all { it is T }) {
        @Suppress("UNCHECKED_CAST")
        this as JsonArray<T>
    }
    else { null }

fun Any?.asTypeDouble(): Double =
    when{
        (this is Int) -> this.toDouble()
        (this is Float) -> this.toDouble()
        (this is Double) -> this
        else -> 0.0
    }

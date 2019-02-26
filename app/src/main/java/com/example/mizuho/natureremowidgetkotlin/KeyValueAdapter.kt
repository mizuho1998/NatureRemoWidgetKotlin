package com.example.mizuho.natureremowidgetkotlin

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.ac_widget.view.*

class KeyValueAdapter(private val context: Context, private  val itemLayoutId: Int, private val list: Array<Pair<String, String>>) :  BaseAdapter() {

    private val TAG = "T_KV_SPINNER"
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    init {
        Log.d(TAG, "init spinner")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        val holder: ViewHolder
        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            holder = ViewHolder(view)
            view!!.tag = holder
        }
        holder.textView.text = list[position].second

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        val holder: ViewHolder
        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            holder = ViewHolder(view)
            view!!.tag = holder
        }
        holder.textView.text = list[position].second

        return view
    }


    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Int{
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getKey(position: Int): String {
        return list[position].first
    }

    // spinner の表示レイアウト
    inner class ViewHolder (val textView: TextView)
}
package com.example.mizuho.natureremowidgetkotlin

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class KeyValueImageAdapter(private val context: Context, private  val itemLayoutId: Int, private val items: Array<String>) :  BaseAdapter() {

    private val TAG = "T_KVI_SPINNER"
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    var res: Resources = context.resources

    init {
        Log.d(TAG, "init spinner")
        items.map { Log.d(TAG, "item: $it ") }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(itemLayoutId, parent, false)
            holder = ViewHolder(view.findViewById(R.id.image_view))
        }
        val imageId = res.getIdentifier(items[position], "drawable", context.packageName)
        holder.imageView.setImageResource(imageId)
        view!!.tag = holder

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view != null) {
            holder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(itemLayoutId, parent, false)
            holder = ViewHolder(view.findViewById(R.id.image_view))
        }
        val imageId = res.getIdentifier(items[position], "drawable", context.packageName)
        holder.imageView.setImageResource(imageId)
        view!!.tag = holder

        return view
    }


    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Int{
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getKey(position: Int): String {
        return items[position]
    }

    // spinner の表示レイアウト
    inner class ViewHolder (val imageView: ImageView)
}
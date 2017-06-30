package com.sonuan.xxxxsimples.adpter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sonuan.xxxxsimples.base.BaseRecAdapter

/**
 * @author wusongyuan
 * @date 2017.06.30
 * @desc
 */
open class CameraSettingsAdapter : BaseRecAdapter<CameraSettingsAdapter.SettingsViewHolder>() {
    var datas: Array<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(holder: SettingsViewHolder?, position: Int) {
        holder?.setData(datas?.get(position))
    }

    override fun getViewHolder(parent: ViewGroup?, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(LayoutInflater.from(parent?.context).inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    class SettingsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView

        init {
            tvTitle = itemView?.findViewById(android.R.id.text1) as TextView
        }

        fun setData(title: String?) {
            tvTitle.text = title
            println(title)
        }
    }
}
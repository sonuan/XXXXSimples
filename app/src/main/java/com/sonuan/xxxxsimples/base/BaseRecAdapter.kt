package com.sonuan.xxxxsimples.base

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.sonuan.xxxxsimples.other.OnItemClickListener

/**
 * @author wusongyuan
 * @date 2017.06.30
 * @desc
 */

abstract class BaseRecAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {
    abstract fun getViewHolder(parent: ViewGroup?, viewType: Int): T

    var onItemListener: OnItemClickListener? = null

    final override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): T {
        var t = getViewHolder(parent, viewType)
        t.itemView.setOnClickListener {
            v ->
            onItemListener?.onItemClick(t.itemView, t.adapterPosition)
        }
        return t
    }
}

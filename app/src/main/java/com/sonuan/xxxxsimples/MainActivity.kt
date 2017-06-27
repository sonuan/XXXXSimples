package com.sonuan.xxxxsimples

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : BaseActivity(), OnItemClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MainRecyclerAdapter

    override fun initViews() {
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.main_recyclerview) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        var itemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_line))
        recyclerView.addItemDecoration(itemDecoration)
    }

    override fun initDatas(savedInstanceState: Bundle?) {
        adapter = MainRecyclerAdapter(this)
        recyclerView.adapter = adapter

        val list: List<String>? = resources.getStringArray(R.array.main_simples).asList()
        adapter.setData(list)
    }

    override fun onItemClick(obj: Any, position: Int) {
        val title = obj.toString()
        println(title)
        when (position) {
            0 -> toActivity(this, MPermissionActivity::class.java, title)
        }
    }

    open class MainRecyclerAdapter(var listener: OnItemClickListener) : RecyclerView.Adapter<MainRecyclerAdapter.MainItemViewHodler>() {

        var datas: List<String>? = null

        open fun setData(datas: List<String>?) {
            this.datas = datas
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return datas?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainItemViewHodler {
            return MainItemViewHodler(LayoutInflater.from(parent?.context).inflate(android.R.layout.simple_list_item_1, parent, false), listener)
        }

        override fun onBindViewHolder(holder: MainItemViewHodler, position: Int) {
            holder.setData(datas?.get(position))
        }

        class MainItemViewHodler(itemView: View?, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

            var tvText: TextView = itemView?.findViewById(android.R.id.text1) as TextView

            init {
                if (listener != null) {
                    itemView?.setOnClickListener { listener.onItemClick(itemView?.tag, adapterPosition) }
                }
            }

            fun setData(text: String?) {
                tvText.text = text
                itemView.tag = text
            }

        }
    }
}

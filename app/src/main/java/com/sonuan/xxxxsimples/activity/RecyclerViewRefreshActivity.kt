package com.sonuan.xxxxsimples.activity

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.sonuan.xxxxsimples.MainActivity
import com.sonuan.xxxxsimples.R
import com.sonuan.xxxxsimples.base.BaseActivity
import com.sonuan.xxxxsimples.other.OnItemClickListener

class RecyclerViewRefreshActivity : BaseActivity(), OnItemClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var list : MutableList<String>

    override fun initViews() {
        setContentView(R.layout.activity_recycler_view_refresh)
        var recyclerView = findViewById(R.id.refresh_recyclerview) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        var itemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_line))
        recyclerView.addItemDecoration(itemDecoration)

        findViewById(R.id.refresh_add_btn).setOnClickListener { list.add("你好") }
    }
    override fun initDatas(savedInstanceState: Bundle?) {
        var adapter = MainActivity.MainRecyclerAdapter(this)
        recyclerView.adapter = adapter

        list = resources.getStringArray(R.array.main_simples).asList().toMutableList()
        adapter.setData(list)
    }

    override fun onItemClick(itemView: View, position: Int) {
    }
}

package com.sonuan.xxxxsimples

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.sonuan.xxxxsimples.activity.*
import com.sonuan.xxxxsimples.base.BaseActivity
import com.sonuan.xxxxsimples.ex.toActivity
import com.sonuan.xxxxsimples.helper.MPermissionHelper
import com.sonuan.xxxxsimples.other.OnItemClickListener

class MainActivity : BaseActivity(), OnItemClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MainRecyclerAdapter
    lateinit var list: MutableList<String>

    override fun initViews() {
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.main_recyclerview) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        var itemDecoration = DividerItemDecoration(this, LinearLayout.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.divider_line))
        recyclerView.addItemDecoration(itemDecoration)
    }

    override fun initDatas(savedInstanceState: Bundle?) {
        adapter = MainRecyclerAdapter(this)
        recyclerView.adapter = adapter

        list = resources.getStringArray(R.array.main_simples).asList().toMutableList()
        adapter.setData(list)
    }

    override fun onItemClick(itemView: View, position: Int) {
        val title = itemView.tag.toString()
        println(title)
        when (position) {
            0 -> {
                toActivity(this, NativePermissionActivity::class.java, title)
            }
            1 -> toActivity(this, EasyPermissionsActivity::class.java, title)
            2 -> {
                MPermissionHelper.Builder(this).permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).listener(object : MPermissionHelper.OnPermissionListener {
                    override fun onGranted(perms: MutableList<String>?) {
                        this@MainActivity.toActivity(this@MainActivity, Camera1Activity::class.java, title)
                    }

                    override fun onDenied(perms: MutableList<String>?) {
                    }
                }).build().request()
            }
            3 -> toActivity(this, GLSurfaceViewActivity::class.java, title)
            4 -> toActivity(this, OverdrawActivity::class.java, title)
            5 -> toActivity(this, RecyclerViewRefreshActivity::class.java, title)
            6 -> toActivity(this, LayoutManagerActivity::class.java, title)

        }
    }

    open class MainRecyclerAdapter(var listener: OnItemClickListener) : RecyclerView.Adapter<MainRecyclerAdapter.MainItemViewHodler>() {

        var datas: List<String>? = null

        open fun setData(datas: MutableList<String>?) {
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
                    itemView?.setOnClickListener { listener.onItemClick(itemView, adapterPosition) }
                }
            }

            fun setData(text: String?) {
                tvText.text = text
                itemView.tag = text
            }

        }
    }
}

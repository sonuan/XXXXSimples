package com.sonuan.xxxxsimples

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initDatas(savedInstanceState)
    }

    protected abstract fun initViews()

    protected abstract fun initDatas(savedInstanceState: Bundle?)
}

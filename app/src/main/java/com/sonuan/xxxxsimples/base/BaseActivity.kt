package com.sonuan.xxxxsimples.base

import android.os.Bundle

abstract class BaseActivity : BasePermissionsActivity() {

    companion object {
        val TITLE = "activity_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConfig(savedInstanceState)
        initViews()
        initDatas(savedInstanceState)
    }

    protected fun initConfig(savedInstanceState: Bundle?) {
        val _intent = intent
        if (_intent != null) {
            val _title = _intent.getStringExtra(TITLE)
            if (_title != null) {
                title = _title
            }

        }

    }

    protected abstract fun initViews()

    protected abstract fun initDatas(savedInstanceState: Bundle?)
}

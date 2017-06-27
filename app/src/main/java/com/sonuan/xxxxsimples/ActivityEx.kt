package com.sonuan.xxxxsimples

import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * @author wusongyuan
 * @date 2017.06.26
 * @desc Activity扩展
 */

fun BaseActivity.toActivity(context: Context, clazz: Class<*>, title: String?) {
    val intent = Intent(context, clazz)
    if (title != null) {
        intent.putExtra(BaseActivity.TITLE, title)
    }
    context.startActivity(intent)
}

fun BaseActivity.toActivity(context: Context, clazz: Class<*>) {
    toActivity(context, clazz, null)
}

fun BaseActivity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

var BaseActivity.TAG: String?
    set(value) {}
    get() {
        return this.localClassName
    }

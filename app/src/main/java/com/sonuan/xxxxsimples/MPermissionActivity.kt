package com.sonuan.xxxxsimples

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.view.View

class MPermissionActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private val REQUESTCODE_CAMERA = 1000

    }

    override fun initViews() {
        setContentView(R.layout.activity_mpermission)
        findViewById(R.id.permission_camera).setOnClickListener(this)
    }

    override fun initDatas(savedInstanceState: Bundle?) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.permission_camera -> camera()
        }
    }

    fun camera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showToast("camera permission granted.")
            // TODO 权限已通过
        } else {
            var permission = Array<String>(1, { i -> Manifest.permission.CAMERA })
            ActivityCompat.requestPermissions(this, permission, REQUESTCODE_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUESTCODE_CAMERA) {
            val result = grantResults[0]
            if (result == PermissionChecker.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    showToast("Result: shouldShowRequestPermissionRationale.")
                    // TODO nothing
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    showToast("Result: go to app settings.")
                    // TODO 跳转到应用详情页面
                } else {
                    showToast("Result: camera permission denied.")
                    // TODO 告诉用户，权限被禁用以及影响什么功能
                }
            } else if (result == PermissionChecker.PERMISSION_GRANTED) {
                showToast("Result: camera permission granted .")
                // TODO 权限通过
            }
        }
        Log.i(TAG, "requestCode:" + requestCode)
        Log.i(TAG, "permissions:" + permissions.asList().toString())
        Log.i(TAG, "grantResults:" + grantResults.asList().toString())
    }
}

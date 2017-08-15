package com.sonuan.xxxxsimples.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import com.sonuan.xxxxsimples.R
import com.sonuan.xxxxsimples.base.BaseActivity
import com.sonuan.xxxxsimples.ex.TAG
import com.sonuan.xxxxsimples.ex.showToast
import pub.devrel.easypermissions.AppSettingsDialog
import java.util.*

class NativePermissionActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private val REQUESTCODE_CAMERA = 11000

    }

    override fun initViews() {
        setContentView(R.layout.nativepermission_activity)
        findViewById(R.id.nativepermissions_camera_btn).setOnClickListener(this)
    }

    override fun initDatas(savedInstanceState: Bundle?) {
    }

    override fun onResume() {
        super.onResume()
    }

    fun test(v: View?) {
        showToast("tint")
    }

    fun testSettingsPermission(v: View?) {
        val uuid = getUUID(applicationContext)
        showToast("uuid:" + uuid )
        var mac = getLocalMacAddress(applicationContext)
        showToast("mac:" + mac)

//        MPermissionHelper.Builder(this).permissions(Manifest.permission.READ_PHONE_STATE).listener(object : MPermissionHelper.OnPermissionListener {
//            override fun onGranted(perms: MutableList<String>?) {
//                val uuid = getUUID(applicationContext)
//                var mac = getLocalMacAddress(applicationContext)
//
//                showToast("uuid:" + uuid + "\nmac:" + mac)
//            }
//
//            override fun onDenied(perms: MutableList<String>?) {
//            }
//
//        }).build().request()
    }

    /**
     * 生成UUID

     * @param context
     * *
     * @return
     */
    @SuppressLint("HardwareIds")
    fun getUUID(context: Context): String {

        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val tmDevice: String
        val tmSerial: String
        val androidId: String
        tmDevice = "" + tm.deviceId
//        tmDevice = "tmDevice"
        Log.i(TAG, "getUUID: " + tmDevice)
        tmSerial = "" + tm.simSerialNumber
//        tmSerial = "tmSerial"
        Log.i(TAG, "getUUID: " + tmSerial)
        androidId = "" + android.provider.Settings.Secure.getString(context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID)
        Log.i(TAG, "getUUID: " + androidId)
        val deviceUuid = UUID(androidId.hashCode().toLong(), tmDevice.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong())
        return deviceUuid.toString()
    }

    fun getLocalMacAddress(context: Context): String {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifi.connectionInfo
        return info.macAddress
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nativepermissions_camera_btn -> camera()
        }
    }

    fun camera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "camera permission granted.")
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
                    Log.i(TAG, "Result: shouldShowRequestPermissionRationale. ")
                    showToast("Result: shouldShowRequestPermissionRationale.")
                    // TODO nothing
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    Log.i(TAG, "Result: go to app settings.")
                    showToast("Result: go to app settings.")
                    // TODO 跳转到应用详情页面
                    AppSettingsDialog.Builder(this).setTitle("权限说明").setRationale("跳转到设置修改权限").setPositiveButton(
                            "设置").setNegativeButton("取消").build().show()

                } else {
                    Log.i(TAG, "Result: camera permission denied.")
                    showToast("Result: camera permission denied.")
                    // TODO 告诉用户，权限被禁用以及影响什么功能
                }
            } else if (result == PermissionChecker.PERMISSION_GRANTED) {
                Log.i(TAG, "Result: camera permission granted .")
                showToast("Result: camera permission granted .")
                // TODO 权限通过
            }
        }
        Log.i(TAG, "requestCode:" + requestCode)
        Log.i(TAG, "permissions:" + permissions.asList().toString())
        Log.i(TAG, "grantResults:" + grantResults.asList().toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Log.i(TAG, "onActivityResult: from app settings")
            camera()
        }
    }
}

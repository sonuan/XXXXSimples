package com.sonuan.xxxxsimples.activity

import android.content.Context
import android.hardware.Camera
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.sonuan.xxxxsimples.R
import com.sonuan.xxxxsimples.adpter.CameraSettingsAdapter
import com.sonuan.xxxxsimples.base.BaseActivity

class Camera1Activity : BaseActivity(), SurfaceHolder.Callback {

    companion object {
        val TAG = "Camera1Activity"
    }

    lateinit var mSurfaceView: SurfaceView
    lateinit var mRecyclerView: RecyclerView
    var mCamera: Camera? = null
    override fun initViews() {
        setContentView(R.layout.activity_camera1)
        mSurfaceView = findViewById(R.id.surfaceView) as SurfaceView
        var surfaceHolder = mSurfaceView.holder
        surfaceHolder.addCallback(this)

        mRecyclerView = findViewById(R.id.camera_recyclerview) as RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun initDatas(savedInstanceState: Bundle?) {
        mCamera = getCameraInstance()
        var parameter = mCamera?.parameters

        var adapter = CameraSettingsAdapter()
        mRecyclerView.adapter = adapter

        val settings = resources.getStringArray(R.array.camera_settings)
        adapter.datas = settings
    }

    /** A safe way to get an instance of the Camera object.  */
    fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT) // attempt to get a Camera instance
        } catch (e: Exception) {
            e.printStackTrace()
            // Camera is not available (in use or does not exist)
        }
        return c // returns null if camera is unavailable
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (holder?.getSurface() == null) {
            // preview surface does not exist
            return
        }

        // stop preview before making changes
        try {
            mCamera?.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
            // ignore: tried to stop a non-existent preview
        }


        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        startPreview(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        startPreview(holder)
    }

    private fun startPreview(holder: SurfaceHolder?) {
        try {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(0, cameraInfo)
            val cameraRotationOffset = cameraInfo.orientation
            Log.i(TAG, "cameraRotationOffset:" + cameraRotationOffset)
            val rotation = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
            Log.i(TAG, "rotation:" + rotation)
            mCamera?.setDisplayOrientation(90)
            mCamera?.setPreviewDisplay(holder)
            mCamera?.startPreview()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCamera?.release()
    }

}

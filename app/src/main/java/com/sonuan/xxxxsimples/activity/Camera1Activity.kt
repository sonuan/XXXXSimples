package com.sonuan.xxxxsimples.activity

import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.sonuan.xxxxsimples.R
import com.sonuan.xxxxsimples.base.BaseActivity

class Camera1Activity : BaseActivity(), SurfaceHolder.Callback {
    lateinit var mSurfaceView: SurfaceView
    var mCamera: Camera? = null
    override fun initViews() {
        setContentView(R.layout.activity_camera1)
        mSurfaceView = findViewById(R.id.surfaceView) as SurfaceView
        var surfaceHolder = mSurfaceView.holder
        surfaceHolder.addCallback(this)
    }

    override fun initDatas(savedInstanceState: Bundle?) {
        mCamera = getCameraInstance()
        var parameter = mCamera?.parameters
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
        try {
            mCamera?.setPreviewDisplay(holder)
            mCamera?.startPreview()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mCamera?.setPreviewDisplay(holder)
        mCamera?.startPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCamera?.release()
    }

}

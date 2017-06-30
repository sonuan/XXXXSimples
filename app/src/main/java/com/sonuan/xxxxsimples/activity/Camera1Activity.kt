package com.sonuan.xxxxsimples.activity

import android.hardware.Camera
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.sonuan.xxxxsimples.R
import com.sonuan.xxxxsimples.adpter.CameraSettingsAdapter
import com.sonuan.xxxxsimples.base.BaseActivity
import com.sonuan.xxxxsimples.other.OnItemClickListener


class Camera1Activity : BaseActivity(), SurfaceHolder.Callback, OnItemClickListener {
    companion object {
        val TAG = "Camera1Activity"
    }

    lateinit var mSurfaceView: SurfaceView
    lateinit var mRecyclerView: RecyclerView
    var mCamera: Camera? = null
    lateinit var sSettings:Array<String>
    var mDisplayRotation:Int = 0
    override fun initViews() {
        setContentView(R.layout.activity_camera1)
        mSurfaceView = findViewById(R.id.surfaceView) as SurfaceView
        var surfaceHolder = mSurfaceView.holder
        surfaceHolder.addCallback(this)

        mRecyclerView = findViewById(R.id.camera_recyclerview) as RecyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(this)

    }
    var currentCameraId = 0
    private var faceBackCameraId: Int = 0

    private var faceBackCameraOrientation: Int = 0

    private var faceFrontCameraId: Int = 0

    private var faceFrontCameraOrientation: Int = 0

    override fun initDatas(savedInstanceState: Bundle?) {
        initSettings()

        initCamera()
    }

    private fun initSettings() {
        var adapter = CameraSettingsAdapter()
        adapter.onItemListener = this
        mRecyclerView.adapter = adapter
        sSettings = resources.getStringArray(R.array.camera_settings)
        adapter.datas = sSettings
    }

    private fun initCamera() {
        val numberOfCameras = Camera.getNumberOfCameras()
        Log.i(TAG, "initCamera: " + numberOfCameras)
        for (i in 0..numberOfCameras - 1) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                faceBackCameraId = i
                faceBackCameraOrientation = cameraInfo.orientation
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                faceFrontCameraId = i
                faceFrontCameraOrientation = cameraInfo.orientation
            }
        }
        currentCameraId = faceFrontCameraId;
        mCamera = getCameraInstance()
        var parameter = mCamera?.parameters
    }

    /** A safe way to get an instance of the Camera object.  */
    fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open(currentCameraId) // attempt to get a Camera instance
        } catch (e: Exception) {
            e.printStackTrace()
            // Camera is not available (in use or does not exist)
        }
        return c // returns null if camera is unavailable
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (holder?.getSurface() == null) {
            return
        }
        try {
            mCamera?.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
            // ignore: tried to stop a non-existent preview
        }
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
            Camera.getCameraInfo(currentCameraId, cameraInfo)
            val cameraRotationOffset = cameraInfo.orientation
            val rotation = windowManager.defaultDisplay.rotation
            var degrees = 0
            when (rotation) {
                Surface.ROTATION_0 -> degrees = 0
                Surface.ROTATION_90 -> degrees = 90
                Surface.ROTATION_180 -> degrees = 180
                Surface.ROTATION_270 -> degrees = 270
            }
            Log.i(TAG, "startPreview: cameraRotationOffset:" + cameraRotationOffset + " rotation:" + rotation + " degrees:" + degrees)
            if (cameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mDisplayRotation = (cameraRotationOffset + degrees) % 360
                Log.i(TAG, "front-facing mDisplayRotation:" + mDisplayRotation)
                mDisplayRotation = (360 - mDisplayRotation) % 360  // compensate the mirror
                Log.i(TAG, "front-facing mDisplayRotation:" + mDisplayRotation)
            } else {  // back-facing
                mDisplayRotation = (cameraRotationOffset- degrees + 360) % 360
                Log.i(TAG, "back-facing mDisplayRotation:" + mDisplayRotation)
            }
            mCamera?.setDisplayOrientation(mDisplayRotation)
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

    override fun onItemClick(itemView: View, position: Int) {
        when (position) {
            0 -> {
                mDisplayRotation += 90
                mCamera?.setDisplayOrientation(mDisplayRotation % 360) }
        }
    }

}

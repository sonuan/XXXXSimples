package com.sonuan.xxxxsimples.base;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sonuan.xxxxsimples.helper.MPermissionHelper;

/**
 * @author wusongyuan
 * @date 2017.06.28
 * @desc
 */

public class BasePermissionsActivity extends AppCompatActivity
        implements MPermissionHelper.OnPermissionsResultListener {
    private static final String TAG = "BasePermissionsActivity";
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult: ");
        if (mPermissionsResultCallback != null) {
            mPermissionsResultCallback.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    ActivityCompat.OnRequestPermissionsResultCallback mPermissionsResultCallback;

    @Override
    public void setPermissionsResultCallback(
            ActivityCompat.OnRequestPermissionsResultCallback permissionsResultCallback) {
        mPermissionsResultCallback = permissionsResultCallback;
    }
}
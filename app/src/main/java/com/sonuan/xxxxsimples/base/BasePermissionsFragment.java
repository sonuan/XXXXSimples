package com.sonuan.xxxxsimples.base;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.sonuan.xxxxsimples.helper.MPermissionHelper;

/**
 * @author wusongyuan
 * @date 2017.06.28
 * @desc
 */

public class BasePermissionsFragment extends Fragment implements MPermissionHelper.OnPermissionsResultListener {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
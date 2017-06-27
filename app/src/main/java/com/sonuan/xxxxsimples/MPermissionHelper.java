package com.sonuan.xxxxsimples;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author wusongyuan
 * @date 2017.06.27
 * @desc 动态权限授权
 *          Build.VERSION.SDK_INT >= Build.VERSION_CODES.M的手机，不能在安装时授权，需要动态权限授权
 */
public class MPermissionHelper implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MPermissionHelper";
    private HashMap<Integer, OnPermissionListener> mListener;
    public static int sRequestCode = 1000;
    private int mRequestCode;
    private boolean mIsShowAppSettingsDialog = true;
    private boolean isDestroyed = false;
    private Activity mActivity;

    public interface OnPermissionListener {
        void onGranted(List<String> perms);

        void onDenied(List<String> perms);
    }

    public MPermissionHelper(Activity activity) {
        mActivity = activity;
        mListener = new HashMap<>();
        mRequestCode = sRequestCode;
        sRequestCode += 1000;
        if (sRequestCode >= Integer.MAX_VALUE) {
            sRequestCode = 1000;
        }
    }

    /**
     * 请求授权，无回调
     *
     * @param perms 一个或多个权限
     */
    public void requestPermissions(String... perms) {
        requestPermissions(null, true, perms);
    }

    /**
     * 请求授权，带回调
     *
     * @param listener                     授权回调
     * @param isShowSettingsDialogIfDenied 在被拒绝后, 是否需要显示跳转到设置Dialog
     * @param perms                        一个或多个权限
     */
    public void requestPermissions(OnPermissionListener listener, boolean isShowSettingsDialogIfDenied,
            String... perms) {
        mListener.put(mRequestCode, listener);
        mIsShowAppSettingsDialog = isShowSettingsDialogIfDenied;
        if (EasyPermissions.hasPermissions(mActivity, perms)) {
            if (listener != null) {
                listener.onGranted(Arrays.asList(perms));
            }
            Log.i(TAG, "requestPermissions: all granted.");
        } else {
            // 请求权限，一个或多个
            EasyPermissions.requestPermissions(mActivity, mActivity.getString(R.string.rationale_camera), mRequestCode,
                    perms);
            Log.i(TAG, "requestPermissions: requestCode:" + mRequestCode);
            mRequestCode++;

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsGranted: requestCode:" + requestCode);
        if (!isDestroyed) {
            if (mListener != null) {
                OnPermissionListener listener = mListener.get(requestCode);
                if (listener != null) {
                    listener.onGranted(perms);
                }
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied: requestCode:" + requestCode);
        if (!isDestroyed) {
            if (EasyPermissions.somePermissionPermanentlyDenied(mActivity, perms) && mIsShowAppSettingsDialog) {
                new AppSettingsDialog.Builder(mActivity).build().show();
            }
            if (mListener != null) {
                OnPermissionListener listener = mListener.get(requestCode);
                if (listener != null) {
                    listener.onDenied(perms);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        // do nothing
    }

    public void destroy() {
        isDestroyed = true;
        if (mListener != null) {
            mListener.clear();
            mListener = null;
        }
        mActivity = null;
    }
}

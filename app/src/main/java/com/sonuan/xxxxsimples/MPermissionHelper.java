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
 * Build.VERSION.SDK_INT >= Build.VERSION_CODES.M的手机，不能在安装时授权，需要动态权限授权
 */
public class MPermissionHelper implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MPermissionHelper";
    private HashMap<Integer, OnPermissionListener> mListeners;
    public static int sRequestCode = 1000;
    private int mRequestCode;
    private boolean isDestroyed = false;

    private Activity mActivity;
    private boolean mIsShowRationaleSettingsDialog = true;
    private String mRationale = "需要相应权限才能更好地正常使用";
    private String mRationaleSettings = "没有相应的权限，此应用程序可能无法正常工作。 打开应用设置页面以修改应用权限";
    private String[] mPermissions;
    private OnPermissionListener mListener;

    private MPermissionHelper(Builder builder) {
        mActivity = builder.mActivity;
        mIsShowRationaleSettingsDialog = builder.mIsShowRationaleSettingsDialog;
        mRationale = builder.mRationale;
        mRationaleSettings = builder.mRationaleSettings;
        mPermissions = builder.mPermissions;
        mListener = builder.mListener;
        mListeners = new HashMap<>();
        mRequestCode = sRequestCode;
        sRequestCode += 1000;
        if (sRequestCode >= Integer.MAX_VALUE) {
            sRequestCode = 1000;
        }
        if (mActivity instanceof EasyPermissionsActivity) {
            EasyPermissionsActivity activity = (EasyPermissionsActivity) mActivity;
            activity.setPermissionsResultCallback(this);
        }
    }


    public interface OnPermissionListener {
        void onGranted(List<String> perms);

        void onDenied(List<String> perms);
    }

    public MPermissionHelper(Activity activity) {
        mActivity = activity;
        mListeners = new HashMap<>();
        mRequestCode = sRequestCode;
        sRequestCode += 1000;
        if (sRequestCode >= Integer.MAX_VALUE) {
            sRequestCode = 1000;
        }
    }

    public MPermissionHelper request() {
        if (mPermissions == null || mPermissions.length == 0) {
            throw new NullPointerException("mPermissions is null");
        }
        requestPermissions();
        return this;
    }

    public MPermissionHelper request(Builder builder) {
        mActivity = builder.mActivity;
        mIsShowRationaleSettingsDialog = builder.mIsShowRationaleSettingsDialog;
        mRationale = builder.mRationale;
        mRationaleSettings = builder.mRationaleSettings;
        mPermissions = builder.mPermissions;
        request();
        return this;
    }

    private void requestPermissions() {
        mListeners.put(mRequestCode, mListener);
        if (EasyPermissions.hasPermissions(mActivity, mPermissions)) {
            if (mListener != null) {
                mListener.onGranted(Arrays.asList(mPermissions));
            }
            Log.i(TAG, "requestPermissions: all granted.");
        } else {
            // 请求权限，一个或多个
            EasyPermissions.requestPermissions(mActivity, mRationale, mRequestCode, mPermissions);
            Log.i(TAG, "requestPermissions: requestCode:" + mRequestCode);
            mRequestCode++;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsGranted: requestCode:" + requestCode);
        if (!isDestroyed) {
            if (mListeners != null) {
                OnPermissionListener listener = mListeners.get(requestCode);
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
            if (EasyPermissions.somePermissionPermanentlyDenied(mActivity, perms) && mIsShowRationaleSettingsDialog) {
                new AppSettingsDialog.Builder(mActivity).setTitle("权限说明")
                        .setRationale(mRationaleSettings)
                        .setPositiveButton("设置")
                        .setNegativeButton("取消")
                        .build()
                        .show();
            }
            if (mListeners != null) {
                OnPermissionListener listener = mListeners.get(requestCode);
                if (listener != null) {
                    listener.onDenied(perms);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void destroy() {
        isDestroyed = true;
        if (mListeners != null) {
            mListeners.clear();
            mListeners = null;
        }
        mActivity = null;
    }

    public MPermissionHelper rationale(String rationale) {
        mRationale = rationale;
        return this;
    }

    public MPermissionHelper rationaleSettings(String rationaleSettings) {
        mRationaleSettings = rationaleSettings;
        return this;
    }

    public MPermissionHelper permissions(String... permissions) {
        mPermissions = permissions;
        return this;
    }

    public MPermissionHelper listener(OnPermissionListener listener) {
        mListener = listener;
        return this;
    }

    public MPermissionHelper showRationaleSettingsDialog(boolean showRationaleSettingsDialog) {
        mIsShowRationaleSettingsDialog = showRationaleSettingsDialog;
        return this;
    }

    public static final class Builder {
        private String mRationale;
        private String mRationaleSettings;
        private String[] mPermissions;
        private OnPermissionListener mListener;
        private Activity mActivity;
        private boolean mIsShowRationaleSettingsDialog;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder rationale(String rationale) {
            mRationale = rationale;
            return this;
        }

        public Builder rationaleSettings(String rationaleSettings) {
            mRationaleSettings = rationaleSettings;
            return this;
        }

        public Builder permissions(String... permissions) {
            mPermissions = permissions;
            return this;
        }

        public Builder listener(OnPermissionListener listener) {
            mListener = listener;
            return this;
        }

        public Builder showRationaleSettingsDialog(boolean showRationaleSettingsDialog) {
            mIsShowRationaleSettingsDialog = showRationaleSettingsDialog;
            return this;
        }

        public MPermissionHelper build() {
            return new MPermissionHelper(this);
        }
    }
}

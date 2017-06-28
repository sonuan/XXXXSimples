package com.sonuan.xxxxsimples;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
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
    public static int sRequestCode = 1000;
    public static final String RATIONALE = "需要相应权限才能更好地正常使用";
    public static final String RATIONALE_SETTINGS = "没有相应的权限，此应用程序可能无法正常工作。 打开应用设置页面以修改应用权限";

    private int mRequestCode;
    private boolean isDestroyed = false;
    private EasyPermissionsActivity mActivity;
    private boolean mIsShowRationaleSettingsDialog = true;
    private String mRationale = RATIONALE;
    private String mRationaleSettings = RATIONALE_SETTINGS;
    private String[] mPermissions;
    private OnPermissionListener mListener;
    private Builder mBuilder;

    private MPermissionHelper(Builder builder) {
        mBuilder = builder;
        mIsShowRationaleSettingsDialog = builder.mIsShowRationaleSettingsDialog;
        mRationale = builder.mRationale;
        mRationaleSettings = builder.mRationaleSettings;
        mPermissions = builder.mPermissions;
        mListener = builder.mListener;
        mRequestCode = sRequestCode;
        sRequestCode += 1;
        if (sRequestCode >= Integer.MAX_VALUE) {
            sRequestCode = 1000;
        }
        if (builder.mActivity instanceof EasyPermissionsActivity) {
            mActivity = (EasyPermissionsActivity) builder.mActivity;
            mActivity.setPermissionsResultCallback(this);
        }
    }


    public interface OnPermissionListener {
        void onGranted(List<String> perms);

        void onDenied(List<String> perms);
    }

    private MPermissionHelper(EasyPermissionsActivity activity) {
        mBuilder = null;
        mActivity = activity;
        mActivity.setPermissionsResultCallback(this);
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

    private void requestPermissions() {
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
            if (mListener != null) {
                mListener.onGranted(perms);
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
            if (mListener != null) {
                mListener.onDenied(perms);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        reset();
        if (mActivity != null) {
            mActivity.setPermissionsResultCallback(null);
        }
    }

    public void reset() {
        mIsShowRationaleSettingsDialog = true;
        mListener = null;
        mPermissions = null;
        mRationale = RATIONALE;
        mRationaleSettings = RATIONALE_SETTINGS;
    }

    private void destroy() {
        isDestroyed = true;
        if (mActivity != null) {
            mActivity.setPermissionsResultCallback(null);
        }
    }

    private MPermissionHelper rationale(String rationale) {
        mRationale = rationale;
        return this;
    }

    private MPermissionHelper rationaleSettings(String rationaleSettings) {
        mRationaleSettings = rationaleSettings;
        return this;
    }

    private MPermissionHelper permissions(String... permissions) {
        mPermissions = permissions;
        return this;
    }

    private MPermissionHelper listener(OnPermissionListener listener) {
        mListener = listener;
        return this;
    }

    private MPermissionHelper showRationaleSettingsDialog(boolean showRationaleSettingsDialog) {
        mIsShowRationaleSettingsDialog = showRationaleSettingsDialog;
        return this;
    }

    public static final class Builder {
        private String mRationale = RATIONALE;
        private String mRationaleSettings = RATIONALE_SETTINGS;
        private String[] mPermissions;
        private OnPermissionListener mListener;
        private Activity mActivity;
        private boolean mIsShowRationaleSettingsDialog = true;

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

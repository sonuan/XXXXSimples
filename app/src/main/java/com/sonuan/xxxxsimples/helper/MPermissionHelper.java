package com.sonuan.xxxxsimples.helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
    private Activity mActivity;
    private Fragment mFragment;
    private boolean mIsShowRationaleSettingsDialog = true;
    private String mRationale = RATIONALE;
    private String mRationaleSettings = RATIONALE_SETTINGS;
    private String[] mPermissions;
    private OnPermissionListener mListener;
    private Context mContext;
    private OnPermissionsResultListener mActivityResult;
    private OnPermissionsResultListener mFragmentResult;

    private MPermissionHelper(Builder builder) {
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
        if (builder.mActivity != null) {
            mActivity = builder.mActivity;
            mContext = builder.mActivity;
            if (builder.mActivity instanceof OnPermissionsResultListener) {
                mActivityResult = (OnPermissionsResultListener) builder.mActivity;
                mActivityResult.setPermissionsResultCallback(this);
            }
        } else if (builder.mFragment != null) {
            mFragment = builder.mFragment;
            mContext = builder.mFragment.getContext();
            if (builder.mFragment instanceof OnPermissionsResultListener) {
                mFragmentResult = (OnPermissionsResultListener) builder.mFragment;
                mFragmentResult.setPermissionsResultCallback(this);
            }
        }

    }

    public interface OnPermissionsResultListener {
        void setPermissionsResultCallback(ActivityCompat.OnRequestPermissionsResultCallback permissionsResultCallback);
    }

    public interface OnPermissionListener {
        void onGranted(List<String> perms);

        void onDenied(List<String> perms);
    }

    private MPermissionHelper(@NonNull Activity activity) {
        mActivity = activity;
        mRequestCode = sRequestCode;
        sRequestCode += 1000;
        if (sRequestCode >= Integer.MAX_VALUE) {
            sRequestCode = 1000;
        }
        if (mActivity != null) {
            mContext = mActivity;
            if (mActivity instanceof OnPermissionsResultListener) {
                mActivityResult = (OnPermissionsResultListener) mActivity;
                mActivityResult.setPermissionsResultCallback(this);
            }
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
        if (EasyPermissions.hasPermissions(mContext, mPermissions)) {
            if (mListener != null) {
                mListener.onGranted(Arrays.asList(mPermissions));
            }
            Log.i(TAG, "requestPermissions: all granted.");
        } else {
            // 请求权限，一个或多个
            if (mActivity != null) {
                EasyPermissions.requestPermissions(mActivity, mRationale, mRequestCode, mPermissions);
            } else if (mFragment != null) {
                EasyPermissions.requestPermissions(mFragment, mRationale, mRequestCode, mPermissions);
            }
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
            if (isSomePermissionPermanentlyDenied(perms) && mIsShowRationaleSettingsDialog) {
                if (mActivity != null) {
                    showSettingsDialogByActivity();
                } else if (mFragment != null) {
                    showSettingsDialogByFragment();
                }
            }
            if (mListener != null) {
                mListener.onDenied(perms);
            }
        }

    }

    private boolean isSomePermissionPermanentlyDenied(List<String> perms) {
        boolean somePermissionPermanentlyDenied = false;
        if (mActivity != null) {
            somePermissionPermanentlyDenied = EasyPermissions.somePermissionPermanentlyDenied(mActivity, perms);
        } else if (mFragment != null) {
            somePermissionPermanentlyDenied = EasyPermissions.somePermissionPermanentlyDenied(mFragment, perms);
        }
        return somePermissionPermanentlyDenied;
    }

    private void showSettingsDialogByActivity() {
        new AppSettingsDialog.Builder(mActivity).setTitle("权限说明").setRationale(mRationaleSettings).setPositiveButton(
                "设置").setNegativeButton("取消").build().show();
    }

    private void showSettingsDialogByFragment() {
        new AppSettingsDialog.Builder(mFragment).setTitle("权限说明").setRationale(mRationaleSettings).setPositiveButton(
                "设置").setNegativeButton("取消").build().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        reset();
        if (mActivityResult != null) {
            mActivityResult.setPermissionsResultCallback(null);
        } else if (mFragmentResult != null) {
            mFragmentResult.setPermissionsResultCallback(null);
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
        if (mActivityResult != null) {
            mActivityResult.setPermissionsResultCallback(null);
        }
        if (mFragmentResult != null) {
            mFragmentResult.setPermissionsResultCallback(null);
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
        private boolean mIsShowRationaleSettingsDialog = true;
        private Activity mActivity;
        private Fragment mFragment;

        public Builder(@NonNull Activity activity) {
            mActivity = activity;
        }

        public Builder(Fragment fragment) {
            mFragment = fragment;
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

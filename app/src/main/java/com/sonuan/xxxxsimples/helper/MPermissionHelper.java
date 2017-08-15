package com.sonuan.xxxxsimples.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.sonuan.xxxxsimples.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    private List<String> mGranteds = new ArrayList<>();
    private List<String> mDenieds = new ArrayList<>();

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
                Log.i(TAG, "MPermissionHelper: act setPermissionsResultCallback");
                mActivityResult.setPermissionsResultCallback(this);
            }
        } else if (builder.mFragment != null) {
            mFragment = builder.mFragment;
            mContext = builder.mFragment.getContext();
            if (builder.mFragment instanceof OnPermissionsResultListener) {
                mFragmentResult = (OnPermissionsResultListener) builder.mFragment;
                Log.i(TAG, "MPermissionHelper: frag setPermissionsResultCallback");
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

    public MPermissionHelper request() {
        if (mPermissions == null || mPermissions.length == 0) {
            throw new NullPointerException("mPermissions is null");
        }
        requestPermissions();
        return this;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mGranteds = Arrays.asList(mPermissions);
            if (mListener != null) {
                mListener.onGranted(mGranteds);
            }
            Log.i(TAG, "sdk < M requestPermissions: all granted.");
        } else {
            mGranteds.clear();
            mDenieds.clear();
            for (String perm : mPermissions) {
                if (checkPermission(mContext, perm)) {
                    mGranteds.add(perm);
                } else {
                    mDenieds.add(perm);
                }
            }
            if (mDenieds.size() == 0 && mGranteds.size() == mPermissions.length) {
                Log.i(TAG, "requestPermissions: all granted.");
                if (mListener != null) {
                    mListener.onGranted(mGranteds);
                }
            } else if (mDenieds.size() > 0) {
                if (XiaomiPermissionUtil.isMiuiOS()) {
                    boolean isGrantedByXiaomiSecurityCenter = true;
                    for (String perm : mDenieds) {
                        if (!XiaomiPermissionUtil.checkPermissionBySecurityCenter(mContext, perm)) {
                            isGrantedByXiaomiSecurityCenter = false;
                            break;
                        }
                    }
                    if (!isGrantedByXiaomiSecurityCenter) {
                        requestPermissionsByXiaomi();
                    } else {
                        requestPermissionsByEasy();
                    }
                } else {
                    requestPermissionsByEasy();
                }
            }
        }
    }

    private void requestPermissionsByEasy() {
        // 请求权限，一个或多个
        if (mActivity != null) {
            EasyPermissions.requestPermissions(mActivity, mRationale, R.string.dlg_btn_text_info, R.string.text_empty,
                    mRequestCode, (String[]) mDenieds.toArray(new String[mDenieds.size()]));
        } else if (mFragment != null) {
            EasyPermissions.requestPermissions(mFragment, mRationale, R.string.dlg_btn_text_info, R.string.text_empty,
                    mRequestCode, (String[]) mDenieds.toArray(new String[mDenieds.size()]));
        }
        Log.i(TAG, "requestPermissionsByEasy: requestCode:" + mRequestCode + " " + mDenieds.toString());
    }

    private void requestPermissionsByXiaomi() {
        if (mActivity != null) {
            ActivityCompat.requestPermissions(mActivity, mDenieds.toArray(new String[mDenieds.size()]), mRequestCode);
        } else if (mFragment != null) {
            mFragment.requestPermissions(mDenieds.toArray(new String[mDenieds.size()]), mRequestCode);
        }
        Log.i(TAG, "requestPermissionsByXiaomi: requestCode:" + mRequestCode + " " + mDenieds.toString());
    }

    private boolean checkPermission(Context context, String perm) {
        boolean isGranted = false;
        if (XiaomiPermissionUtil.isMiuiOS()) {
            isGranted = XiaomiPermissionUtil.checkPermissionInXiaomi(context, perm);
        }else{
            isGranted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED;
        }
        Log.i(TAG, "checkPermission: " + perm + " " + isGranted);
        return isGranted;
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (mGranteds.size() != 0) {
            perms.addAll(0, mGranteds);
            mDenieds.clear();
        }
        Log.i(TAG, "onPermissionsGranted: requestCode:" + requestCode + " perms:" + perms);
        if (!isDestroyed && mRequestCode == requestCode) {
            if (mListener != null) {
                mListener.onGranted(perms);
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsDenied: requestCode:" + requestCode + " perms:" + perms);
        if (!isDestroyed && mRequestCode == requestCode) {
            if (mIsShowRationaleSettingsDialog && isSomePermissionPermanentlyDenied(perms)) {
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, MPermissionHelper.this);
        destroy();
    }

    private void destroy() {
        isDestroyed = true;
        if (mGranteds != null) {
            mGranteds.clear();
        }
        if (mDenieds != null) {
            mDenieds.clear();
        }
        mActivity = null;
        mFragment = null;
        mContext = null;
        mListener = null;
        mPermissions = null;
        mRationaleSettings = null;
        mRationale = null;
        if (mActivityResult != null) {
            mActivityResult.setPermissionsResultCallback(null);
            mActivityResult = null;
        }
        if (mFragmentResult != null) {
            mFragmentResult.setPermissionsResultCallback(null);
            mFragmentResult = null;
        }
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

    public static class XiaomiPermissionUtil {
        @TargetApi(23)
        public static boolean checkPermissionInXiaomi(Context context, String perm) {
            Log.i(TAG, "checkPermissionInXiaomi: ");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED;
            }
            boolean isGranted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED;
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int checkOp = appOpsManager.checkOp(AppOpsManager.permissionToOp(perm), Process.myUid(),
                    context.getPackageName());
            if (isGranted && checkOp == AppOpsManager.MODE_ALLOWED) {
                return true;
            }
            //if (isGranted && checkOp == AppOpsManager.MODE_IGNORED) {
            //    return false;
            //}
            return false;
        }

        @TargetApi(23)
        public static boolean checkPermissionBySecurityCenter(Context context, String perm) {
            Log.i(TAG, "checkPermissionBySecurityCenter: ");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return true;
            }
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int checkOp = appOpsManager.checkOp(AppOpsManager.permissionToOp(perm), Process.myUid(),
                    context.getPackageName());
            return checkOp == AppOpsManager.MODE_ALLOWED;
        }

        public static void init() {
            isMiuiOS = !TextUtils.isEmpty(getSystemProperty(PROPERTY_MIUI_NAME));
            if (!isMiuiOS) {
                isMiuiOS = !TextUtils.isEmpty(getSystemProperty(PROPERTY_MIUI_CODE));
            }
            Log.i(TAG, "init: isMiuiOS:" + isMiuiOS);
        }

        public static boolean isMiuiOS;

        public static boolean isMiuiOS() {
            return isMiuiOS;
        }

        public static final String PROPERTY_MIUI_NAME = "ro.miui.ui.version.name";
        public static final String PROPERTY_MIUI_CODE = "ro.miui.ui.version.code";
        public static String getSystemProperty(String propName) {
            String line;
            BufferedReader input = null;
            try {
                java.lang.Process p = Runtime.getRuntime()
                        .exec("getprop " + propName);
                input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
                line = input.readLine();
                input.close();
                p.destroy();
            } catch (IOException ex) {
                Log.e(TAG, "Unable to read sysprop " + propName, ex);
                return null;
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Exception while closing InputStream", e);
                    }
                }
            }
            return line;
        }
    }
}

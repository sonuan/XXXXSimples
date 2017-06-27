package com.sonuan.xxxxsimples;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EasyPermissionsActivity extends BaseActivity implements View.OnClickListener {


    private static final int RC_CAMERA_PERM = 1001;
    private static final int RC_LOCATION_CONTACTS_PERM = 1002;
    private MPermissionHelper mPermissionHelper;

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_easy_permissions);
        findViewById(R.id.permission_camera).setOnClickListener(this);
        findViewById(R.id.permission_camera2).setOnClickListener(this);
        findViewById(R.id.multi_permission).setOnClickListener(this);
    }

    @Override
    protected void initDatas(Bundle savedInstanceState) {
        mPermissionHelper = new MPermissionHelper(this);
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void camera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, null, RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    public void locationAndContactsTask() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            Toast.makeText(this, "TODO: Location and Contacts things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location_contacts),
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }

    private void camera2() {
        mPermissionHelper.requestPermissions(Manifest.permission.CAMERA);
    }

    private void multiPer() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
        mPermissionHelper.requestPermissions(perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, mPermissionHelper);
    }
    //
    //
    //@Override
    //public void onPermissionsGranted(int requestCode, List<String> perms) {
    //
    //}
    //
    //@Override
    //public void onPermissionsDenied(int requestCode, List<String> perms) {
    //    // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
    //    // This will display a dialog directing them to enable the permission in app settings.
    //    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
    //        new AppSettingsDialog.Builder(this).build().show();
    //    }
    //}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission_camera:
                camera();
                break;
            case R.id.permission_camera2:
                camera2();
                break;
            case R.id.multi_permission:
                multiPer();
                break;
        }
    }
}

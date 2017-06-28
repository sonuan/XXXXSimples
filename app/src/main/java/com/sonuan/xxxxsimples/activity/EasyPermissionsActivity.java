package com.sonuan.xxxxsimples.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sonuan.xxxxsimples.helper.MPermissionHelper;
import com.sonuan.xxxxsimples.R;
import com.sonuan.xxxxsimples.base.BaseActivity;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EasyPermissionsActivity extends BaseActivity implements View.OnClickListener {


    private static final int RC_CAMERA_PERM = 1001;
    private static final int RC_LOCATION_CONTACTS_PERM = 1002;

    @Override
    protected void initViews() {
        setContentView(R.layout.easypermissions_activity);
        findViewById(R.id.easypermissions_camera_native).setOnClickListener(this);
        findViewById(R.id.easypermissions_camera_my).setOnClickListener(this);
        findViewById(R.id.easypermissions_camera_and_location).setOnClickListener(this);
        findViewById(R.id.easypermissions_camera_and_location_words).setOnClickListener(this);
    }

    @Override
    protected void initDatas(Bundle savedInstanceState) {
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
        new MPermissionHelper.Builder(this).permissions(Manifest.permission.CAMERA).build().request();
    }

    private void multiPer() {
        new MPermissionHelper.Builder(this).permissions(Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION).build().request();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easypermissions_camera_native:
                camera();
                break;
            case R.id.easypermissions_camera_my:
                camera2();
                break;
            case R.id.easypermissions_camera_and_location:
                multiPer();
                break;
            case R.id.easypermissions_camera_and_location_words:
                new MPermissionHelper.Builder(this).permissions(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION).rationale("拒绝后，再次申请时提示语").rationaleSettings(
                        "权限被禁，提示去设置").showRationaleSettingsDialog(true).listener(
                        new MPermissionHelper.OnPermissionListener() {
                            @Override
                            public void onGranted(List<String> perms) {

                            }

                            @Override
                            public void onDenied(List<String> perms) {

                            }
                        }).build().request();
                break;
        }
    }
}

package com.sonuan.xxxxsimples.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sonuan.xxxxsimples.helper.MPermissionHelper;
import com.sonuan.xxxxsimples.R;
import com.sonuan.xxxxsimples.base.BaseFragment;

/**
 * @author wusongyuan
 * @date 2017.06.28
 * @desc
 */

public class EasyPermissionsFragment extends BaseFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.easypermissions_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.easypermissions_camera_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easypermissions_camera_btn:
                new MPermissionHelper.Builder(this).permissions(Manifest.permission.CAMERA).build().request();
                break;
        }
    }
}

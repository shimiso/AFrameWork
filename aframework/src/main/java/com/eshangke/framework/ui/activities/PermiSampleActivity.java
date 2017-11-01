package com.eshangke.framework.ui.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import com.eshangke.framework.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by shims on 2017/10/27.
 */
@RuntimePermissions
public class PermiSampleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission);

        findViewById(R.id.permission_btn).setOnClickListener(v -> {
            // 调用带权限检查的 showCamera 方法
            PermiSampleActivityPermissionsDispatcher.showCameraWithPermissionCheck(this);
        });

    }

    //获取单个权限
    @NeedsPermission(Manifest.permission.CAMERA)
    public void showCamera() {
        Toast.makeText(this, "showCamera", Toast.LENGTH_LONG).show();
    }

    @OnShowRationale({Manifest.permission.CAMERA})
    void showRationaleForCamera(final PermissionRequest request) {//给用户解释要请求什么权限，为什么需要此权限
        new AlertDialog.Builder(this)
                .setMessage("使用此功能需要CAMERA权限，下一步将继续请求权限")
                .setPositiveButton("下一步", (dialog, which) -> {
                    request.proceed();//继续执行请求
                }).setNegativeButton("取消", (dialog, which) -> {
            request.cancel();//取消执行请求
        }).show();
    }

    // 用户拒绝授权回调（可选）
    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        Toast.makeText(this, "用户拒绝权限", Toast.LENGTH_SHORT).show();
    }

    // 用户勾选了“不再提醒”时调用（可选）
    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        Toast.makeText(this,"用户勾选了不再提醒", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermiSampleActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}

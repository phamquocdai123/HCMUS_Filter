package com.example.hcmus_filter.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hcmus_filter.R;
import com.example.hcmus_filter.utils.DeviceUtils;
import com.example.hcmus_filter.utils.PermissionUtils;

import butterknife.Bind;

public class SplashActivity extends BaseActivity {

    @Bind(R.id.tv_splash)
    TextView tvSplash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void createView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        animate();
    }

    private void animate() {
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                askForPermission();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tvSplash.startAnimation(animation);
    }

    private void askForPermission() {
        askCompactPermissions(new String[]{PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE}
                , new PermissionUtils.PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        showActivity(MainActivity.class);
                        finish();
                    }

                    @Override
                    public void permissionDenied() {
                        showToast("Bạn cần cung cấp quyền để sử dụng ứng dụng");
                    }

                    @Override
                    public void permissionForeverDienid() {
                        new AlertDialog.Builder(SplashActivity.this).setTitle("Cung cấp quyền")
                                .setMessage("Bạn có muốn cấp quyền sử dụng camera, đọc file, ghi file để chạy app này không?")
                                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        DeviceUtils.openSettingsApp(SplashActivity.this);
                                    }
                                })
                                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                });
    }



}

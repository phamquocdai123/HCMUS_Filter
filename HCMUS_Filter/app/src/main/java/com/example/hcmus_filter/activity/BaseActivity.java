package com.example.hcmus_filter.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.example.hcmus_filter.R;
import com.example.hcmus_filter.utils.Constant;
import com.example.hcmus_filter.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    private final int KEY_PERMISSION = 200;
    private PermissionUtils.PermissionResult permissionResult;
    private String permissionsAsk[];
    private SVProgressHUD svProgressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        createView();
        svProgressHUD = new SVProgressHUD(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * get layout id of activity
     *
     * @return layout id
     */
    protected abstract int getLayoutId();

    /**
     * create view
     */
    protected abstract void createView();

    /**
     * show toast message
     *
     * @param msg message to show
     */
    public void showToast(String msg) {
        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * go to other activity
     *
     * @param t Activity class
     */
    public void showActivity(Class t) {
        Intent intent = new Intent(this, t);
        startActivity(intent);
    }

    public void showActivity(Class t, Bundle bundle) {
        Intent intent = new Intent(this, t);
        intent.putExtra(Constant.KEY_EXTRA, bundle);
        startActivity(intent);
    }


    public boolean isPermissionGranted(String permission) {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
    }





    public void askCompactPermissions(String permissions[], PermissionUtils.PermissionResult permissionResult) {
        permissionsAsk = permissions;
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }

    private void internalRequestPermission(String[] permissionAsk) {
        String arrayPermissionNotGranted[];
        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        for (int i = 0; i < permissionAsk.length; i++) {
            if (!isPermissionGranted(permissionAsk[i])) {
                permissionsNotGranted.add(permissionAsk[i]);
            }
        }
        if (permissionsNotGranted.isEmpty()) {
            if (permissionResult != null)
                permissionResult.permissionGranted();
        } else {
            arrayPermissionNotGranted = new String[permissionsNotGranted.size()];
            arrayPermissionNotGranted = permissionsNotGranted.toArray(arrayPermissionNotGranted);
            ActivityCompat.requestPermissions(this, arrayPermissionNotGranted, KEY_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != KEY_PERMISSION) {
            return;
        }
        List<String> permissionDienid = new LinkedList<>();
        boolean granted = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                granted = false;
                permissionDienid.add(permissions[i]);
            }
        }
        if (permissionResult != null) {
            if (granted) {
                permissionResult.permissionGranted();
            } else {
                for (String s : permissionDienid) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {
                        permissionResult.permissionForeverDienid();
                        return;
                    }
                }
                permissionResult.permissionDenied();
            }
        }

    }

    public void showProgressDialog(String message) {
        if (svProgressHUD == null) {
            svProgressHUD = new SVProgressHUD(this);
        }
        if (!svProgressHUD.isShowing()) {
            if (message != null) {
                svProgressHUD.showWithStatus(message, SVProgressHUD.SVProgressHUDMaskType.Clear);
            } else {
                svProgressHUD.showWithStatus("Loading", SVProgressHUD.SVProgressHUDMaskType.Clear);
            }
            svProgressHUD.show();
        }
    }

    public void hideProgressDialog() {
        if (svProgressHUD != null && svProgressHUD.isShowing()) {
            svProgressHUD.dismiss();
            svProgressHUD.dismissImmediately();
        }

    }
}

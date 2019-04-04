package com.example.hcmus_filter.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hcmus_filter.BuildConfig;
import com.example.hcmus_filter.R;
import com.example.hcmus_filter.utils.Constant;
import com.example.hcmus_filter.utils.DeviceUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

public class SaveImageActivity extends BaseActivity {

    @Bind(R.id.iv_picture)
    ImageView ivPicture;
    @Bind(R.id.et_caption)
    EditText etCaption;
    String imagePath;
    @Bind(R.id.bt_post)
    ImageView imvPost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(this).load(new File(imagePath)).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivPicture);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_save_image;
    }

    @Override
    protected void createView() {
        int screenWidth = DeviceUtils.getScreenWidth(this);
        ivPicture.getLayoutParams().height = screenWidth;
    }

    @OnClick(R.id.bt_close)
    public void closeCamera() {
        finish();
    }

    private void getData() {
        Bundle bundle = getIntent().getBundleExtra(Constant.KEY_EXTRA);
        if (bundle != null) {
            imagePath = bundle.getString(Constant.KEY_IMAGE_PATH);
        }
    }

    @OnClick(R.id.bt_post)
    public void post() {
        //share
        final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File file = new File(imagePath);
        Uri uri = FileProvider.getUriForFile(SaveImageActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);
       // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)));
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Hic");
        startActivity(Intent.createChooser(intent, "Share image via"));
    }


}

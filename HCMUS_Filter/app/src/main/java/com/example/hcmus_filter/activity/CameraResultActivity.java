package com.example.hcmus_filter.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.hcmus_filter.R;
import com.example.hcmus_filter.adapter.ListFilterAdapter;
import com.example.hcmus_filter.model.FilterData;
import com.example.hcmus_filter.utils.Constant;
import com.example.hcmus_filter.utils.DeviceUtils;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import org.wysaid.myUtils.ImageUtil;
import org.wysaid.nativePort.CGENativeLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.example.hcmus_filter.activity.MainActivity.EFFECT_CONFIGS;

public class CameraResultActivity extends BaseActivity {

    private static final int RC_PICK_IMAGE = 1235;

    @Bind(R.id.rv_filter)
    RecyclerView rvFilter;
    @Bind(R.id.iv_crop)
    CropImageView ivCrop;
    @Bind(R.id.rl_image)
    RelativeLayout rlImage;
    @Bind(R.id.tv_loading_image)
    TextView tvLoadingImage;
    String imagePath;
    String targetScreen;
    Bitmap resource;
    Bitmap finalResource;

    int screenWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        screenWidth = DeviceUtils.getScreenWidth(this);
        rlImage.getLayoutParams().height = screenWidth;
        ivCrop.getLayoutParams().height = screenWidth;
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(this).load(new File(imagePath)).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    CameraResultActivity.this.resource = resource;
                    ivCrop.setImageBitmap(resource);
                    ivCrop.setCustomRatio(screenWidth * 2 / 3, screenWidth * 2 / 3);
                    tvLoadingImage.setVisibility(View.GONE);
                }
            });
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RC_PICK_IMAGE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera_result;
    }

    @Override
    protected void createView() {
        setUpListFilterEffect();

    }

    private void getData() {
        Bundle bundle = getIntent().getBundleExtra(Constant.KEY_EXTRA);
        if (bundle != null) {
            imagePath = bundle.getString(Constant.KEY_IMAGE_PATH);
            targetScreen = bundle.getString(Constant.KEY_TARGET_SCREEN, "");
        }
    }

    private void setUpListFilterEffect() {
        rvFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //create list filter
        List<FilterData> listFilter = new ArrayList<>();
        int[] imageFilterId = {R.drawable.original_1, R.drawable.natural_1, R.drawable.natural_2
                , R.drawable.pure_1, R.drawable.pure_2, R.drawable.pinky_1
                , R.drawable.pinky_2, R.drawable.pinky_3, R.drawable.pinky_4
                , R.drawable.warm_1, R.drawable.warm_2, R.drawable.cool_1
                , R.drawable.cool_2, R.drawable.mood, R.drawable.bw};
        for (int i = 0; i < EFFECT_CONFIGS.length; i++) {
            //listFilter.add(new FilterData(EFFECT_CONFIGS[i], imageFilterId[i ]));
            if (i == 0) {
                listFilter.add(new FilterData("Original", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 1) {
                listFilter.add(new FilterData("Natural 1", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 2) {
                listFilter.add(new FilterData("Natural 2", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 3) {
                listFilter.add(new FilterData("Pure 1", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 4) {
                listFilter.add(new FilterData("Pure 2", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 5) {
                listFilter.add(new FilterData("Pinky 1", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 6) {
                listFilter.add(new FilterData("Pinky 2", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 7) {
                listFilter.add(new FilterData("Pinky 3", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 8) {
                listFilter.add(new FilterData("Pinky 4", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 9) {
                listFilter.add(new FilterData("Warm 1", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 10) {
                listFilter.add(new FilterData("Warm 2", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 11) {
                listFilter.add(new FilterData("Cool 1", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 12) {
                listFilter.add(new FilterData("Cool 2", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 13) {
                listFilter.add(new FilterData("Mood", EFFECT_CONFIGS[i], imageFilterId[i]));
            } else if (i == 14) {
                listFilter.add(new FilterData("B&W", EFFECT_CONFIGS[i], imageFilterId[i]));
            }
        }
        ListFilterAdapter filterAdapter = new ListFilterAdapter(listFilter);
        filterAdapter.setOnFilterSelect(new ListFilterAdapter.OnFilterSelect() {
            @Override
            public void onSelect(FilterData filterData) {
                new FilterBitmapTask().execute(filterData.getRule());
            }
        });
        rvFilter.setAdapter(filterAdapter);
    }

    private class FilterBitmapTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("filtering");
        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap) {
            super.onPostExecute(resultBitmap);
            ivCrop.setImageBitmap(resultBitmap);
            String logData = "FilterBitmapTask " + " bimap null:" + (resultBitmap == null);
            if (resultBitmap != null) {
                logData = logData + "bitmap width = " + resultBitmap.getWidth() + " - bitmap height = " + resultBitmap.getHeight();
            }
            hideProgressDialog();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            finalResource = CGENativeLibrary.cgeFilterImage_MultipleEffects(resource, params[0], 1f);
            return finalResource;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Glide.with(this).load(new File(getRealPathFromURI(selectedImageUri))).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    CameraResultActivity.this.resource = resource;
                    ivCrop.setImageBitmap(resource);
                    ivCrop.setCustomRatio(screenWidth * 2 / 3, screenWidth * 2 / 3);
                    tvLoadingImage.setVisibility(View.GONE);
                }
            });
        } else {
            finish();
        }


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @OnClick({R.id.bt_continue, R.id.bt_done})
    public void cropImage() {
        ivCrop.startCrop(null, new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                File file = new File(Environment.getExternalStorageDirectory() + "/FeedyPhoto/Filtered");
                if (!file.exists()) {
                    file.mkdirs();
                }
                String imagePath = ImageUtil.saveBitmap(cropped, file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
                Bundle bundle = new Bundle();
                if (imagePath != null) {
                    bundle.putString(Constant.KEY_IMAGE_PATH, imagePath);
                    showActivity(SaveImageActivity.class, bundle);
                }

            }

            @Override
            public void onError(Throwable e) {

            }
        }, new SaveCallback() {
            @Override
            public void onSuccess(Uri uri) {

            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }


    @OnClick({R.id.bt_close, R.id.bt_back})
    public void closeCamera() {
        finish();
    }


}

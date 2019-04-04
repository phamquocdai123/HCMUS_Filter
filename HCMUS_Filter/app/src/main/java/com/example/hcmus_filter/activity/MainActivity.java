package com.example.hcmus_filter.activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.hcmus_filter.R;
import com.example.hcmus_filter.adapter.ListFilterAdapter;
import com.example.hcmus_filter.model.FilterData;
import com.example.hcmus_filter.utils.Constant;
import com.example.hcmus_filter.utils.DeviceUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import org.wysaid.camera.CameraInstance;
import org.wysaid.common.Common;
import org.wysaid.myUtils.ImageUtil;
import org.wysaid.nativePort.CGENativeLibrary;
import org.wysaid.view.CameraRecordGLSurfaceView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.rl_camera_area)
    RelativeLayout rlCameraView;
    @Bind(R.id.c_view)
    CameraRecordGLSurfaceView cameraView;
    @Bind(R.id.bt_flash_mode)
    ImageView btFlashMode;
    @Bind(R.id.rv_filter)
    RecyclerView rvFilter;
    @Bind(R.id.iv_pick_image)
    RoundedImageView ivPickImage;

    int flashIndex = 0;
    String[] flashModes = {
            Camera.Parameters.FLASH_MODE_TORCH,
            Camera.Parameters.FLASH_MODE_AUTO,
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void createView() {
        setUpCameraView();
        setUpListFilterEffect();
        setUpButtonChooseImage();
    }

    private void setUpButtonChooseImage() {
        String lastTakenImagePath = getLastImageTaken();
        if (lastTakenImagePath != null) {
            Glide.with(this).load(new File(lastTakenImagePath)).into(ivPickImage);
        }
    }

    private void setUpCameraView() {
        int screenWidth = DeviceUtils.getScreenWidth(this);
        int screenHeight = DeviceUtils.getScreenHeight(this);
        //set vùng camera thành vùng vuông
        rlCameraView.getLayoutParams().height = screenWidth;
        cameraView.getLayoutParams().height = screenWidth;
        cameraView.presetCameraForward(true);
        cameraView.presetRecordingSize(screenHeight, screenHeight);
        cameraView.setPictureSize(screenHeight, screenHeight, true); // > 4MP

        cameraView.setZOrderOnTop(false);
        cameraView.setZOrderMediaOverlay(true);
        cameraView.setOnCreateCallback(new CameraRecordGLSurfaceView.OnCreateCallback() {
            @Override
            public void createOver(boolean success) {
                if (success) {
                    Log.i(LOG_TAG, "view create OK");
                } else {
                    Log.e(LOG_TAG, "view create failed!");
                }
            }
        });
        btFlashMode.setOnClickListener(new View.OnClickListener() {
            int flashIndex = 0;
            String[] flashModes = {
                    Camera.Parameters.FLASH_MODE_TORCH,
                    Camera.Parameters.FLASH_MODE_AUTO,
                    /*,
                    Camera.Parameters.FLASH_MODE_ON,
                    Camera.Parameters.FLASH_MODE_TORCH,
                    Camera.Parameters.FLASH_MODE_RED_EYE,*/
            };

            @Override
            public void onClick(View v) {
                cameraView.setFlashLightMode(flashModes[flashIndex]);
                ++flashIndex;
                flashIndex %= flashModes.length;
                if (flashIndex == 0) {
                    btFlashMode.setImageResource(R.drawable.ic_turn_on_flash);
                } else {
                    btFlashMode.setImageResource(R.drawable.ic_turn_off_flash);
                }
            }
        });
        CGENativeLibrary.setLoadImageCallback(mLoadImageCallback, null);
    }

    private void setUpListFilterEffect() {
        //list danh sách hiệu ứng nằm ngang
        rvFilter.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //create list filter
        List<FilterData> listFilter = new ArrayList<>();
        int[] imageFilterId = {R.drawable.original_1, R.drawable.natural_1, R.drawable.natural_2
                , R.drawable.pure_1, R.drawable.pure_2, R.drawable.pinky_1
                , R.drawable.pinky_2, R.drawable.pinky_3, R.drawable.pinky_4
                , R.drawable.warm_1, R.drawable.warm_2, R.drawable.cool_1
                , R.drawable.cool_2, R.drawable.mood, R.drawable.bw};
        for (int i = 0; i < EFFECT_CONFIGS.length; i++) {
            //listFilter.add(new FilterData(EFFECT_CONFIGS[i], imageFilterId[i]));
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
                seletedFilterData = filterData;
                cameraView.setFilterWithConfig(filterData.getRule());
                mCurrentConfig = filterData.getRule();
            }
        });
        rvFilter.setAdapter(filterAdapter);
    }


    @OnClick(R.id.bt_close)
    public void closeCamera() {
        finish();
    }

    @OnClick(R.id.bt_rotate_camera)
    public void rotateCamera() {
        cameraView.switchCamera();
    }

    @OnClick(R.id.iv_pick_image)
    public void pickImage() {
        Bundle bundle = new Bundle();
        showActivity(CameraResultActivity.class, bundle);
    }

    @OnClick({R.id.bt_take_picture})
    public void onTakePictureClick() {
        showToast("Đang chụp ảnh...");
        cameraView.takeShot(new CameraRecordGLSurfaceView.TakePictureCallback() {
            @Override
            public void takePictureOK(Bitmap bmp) {
                File file = new File(Environment.getExternalStorageDirectory() + "/FilterImageDemo");
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (bmp != null) {
                    String imagePath = ImageUtil.saveBitmap(bmp, file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
                    bmp.recycle();
                    //showToast("Đã xong!");
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.KEY_IMAGE_PATH, imagePath);
                    bundle.putSerializable(Constant.KEY_FILTER, seletedFilterData);
                    showActivity(CameraResultActivity.class, bundle);
                } else {
                    showToast("Ôi, có lỗi rồi!");
                }
            }
        });
    }

    private String getLastImageTaken() {
        File imageFolder = new File(Environment.getExternalStorageDirectory() + "/FilterImageDemo");
        if (!imageFolder.exists()) {
            return null;
        } else {
            File[] listFile = imageFolder.listFiles();
            if (listFile == null || listFile.length == 0) {
                return null;
            } else {
                for (int i = listFile.length - 1; i >= 0; i--) {
                    File imageFile = listFile[i];
                    String fileName = imageFile.getName();
                    if (!imageFile.isDirectory() && (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                            || fileName.endsWith(".JPG") || fileName.endsWith(".jpeg")
                            || fileName.endsWith(".png") || fileName.endsWith(".PNG"))) {
                        return imageFile.getAbsolutePath();
                    }
                }
                return null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraInstance.getInstance().stopCamera();
        cameraView.release(null);
        cameraView.onPause();
    }

    FilterData seletedFilterData = new FilterData("None", EFFECT_CONFIGS[0], 0);
    private String mCurrentConfig;

    public final static String LOG_TAG = "FilterDemo";
    public static final String EFFECT_CONFIGS[] = {
            "@adjust lut original.png",
            "@adjust lut natural01.png",
            "@adjust lut natural02.png",
            "@adjust lut pure01.png",
            "@adjust lut pure02.png",
            "@adjust lut lovely01.png",
            "@adjust lut lovely02.png",
            "@adjust lut lovely03.png",
            "@adjust lut lovely04.png",
            "@adjust lut warm01.png",
            "@adjust lut warm02.png",
            "@adjust lut cool01.png",
            "@adjust lut cool02.png",
            "@adjust lut vintage.png",
            "@adjust lut gray.png",
    };

    public CGENativeLibrary.LoadImageCallback mLoadImageCallback = new CGENativeLibrary.LoadImageCallback() {

        //Notice: the 'name' passed in is just what you write in the rule, e.g: 1.jpg
        @Override
        public Bitmap loadImage(String name, Object arg) {

            Log.i(Common.LOG_TAG, "Loading file: " + name);
            AssetManager am = getAssets();
            InputStream is;
            try {
                is = am.open(name);
            } catch (IOException e) {
                Log.e(Common.LOG_TAG, "Can not open file " + name);
                return null;
            }

            return BitmapFactory.decodeStream(is);
        }

        @Override
        public void loadImageOK(Bitmap bmp, Object arg) {
            Log.i(Common.LOG_TAG, "Loading bitmap over, you can choose to recycle or cache");

            //The bitmap is which you returned at 'loadImage'.
            //You can call recycle when this function is called, or just keep it for further usage.
            bmp.recycle();
        }
    };


}

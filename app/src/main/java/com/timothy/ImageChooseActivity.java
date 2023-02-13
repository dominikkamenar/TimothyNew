package com.timothy;

import static com.timothy.MainActivity.changeStatusBarContrastStyle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.yalantis.ucrop.UCrop;

public class ImageChooseActivity extends AppCompatActivity {

    private CardView choose_image_btn, continue_btn;
    private TextView choose_image_btn_txt, continue_btn_txt;
    private ImageView back_btn, background_image;
    private RelativeLayout bottom;
    ActivityResultLauncher<String> cropImage;
    private ImageView image_view;
    String image;
    Uri mainUri = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_choose);

        Window w = ImageChooseActivity.this.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setStatusBarColor(getColor(R.color.grey));
        changeStatusBarContrastStyle(w, false);

        Intent intent1 = getIntent();
        image = intent1.getStringExtra("image");

        image_view = findViewById(R.id.image_view);
        back_btn = findViewById(R.id.back_btn);
        choose_image_btn = findViewById(R.id.choose_image_btn);
        choose_image_btn = findViewById(R.id.choose_image_btn);

        back_btn = findViewById(R.id.back_btn);
        background_image = findViewById(R.id.background_image);
        bottom = findViewById(R.id.bottom);
        choose_image_btn_txt = findViewById(R.id.choose_image_btn_txt);
        choose_image_btn = findViewById(R.id.choose_image_btn);
        continue_btn_txt = findViewById(R.id.continue_btn_txt);
        continue_btn = findViewById(R.id.continue_btn);

        background_image.setImageResource(Integer.parseInt(image));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottom.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, (getNavigationBarHeight() + 20));
        bottom.setLayoutParams(layoutParams);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(false);
                        continue_btn.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(true);
                        continue_btn.setEnabled(true);
                        finish();
                    }
                }).playOn(back_btn);
            }
        });

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(false);
                        continue_btn.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(true);
                        continue_btn.setEnabled(true);
                        if (mainUri != null) {
                            Intent intent1 = new Intent(ImageChooseActivity.this, ImagePickerActivity.class);
                            intent1.putExtra("image", image);
                            intent1.putExtra("imagePicked", mainUri.toString());
                            startActivity(intent1);
                            finish();
                        } else {
                            Toast.makeText(ImageChooseActivity.this, "Please firstly choose an image :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).playOn(continue_btn_txt);
            }
        });

        choose_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        choose_image_btn.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        choose_image_btn.setEnabled(true);
                        ImagePermission();
                    }
                }).playOn(choose_image_btn_txt);
            }
        });

        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(ImageChooseActivity.this.getApplicationContext(), CropperActivity.class);
            if (result != null) {
                intent.putExtra("SendImageData", result.toString());
                intent.putExtra("type", "image");
                startActivityForResult(intent, 100);
            }
        });
    }

    private void ImagePermission() {
        Dexter.withContext(ImageChooseActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 101) {
            String result = data.getStringExtra("CROP");
            Uri uri = data.getData();
            if (result != null) {
                uri = Uri.parse(result);

                mainUri = uri;

                image_view.setImageURI(uri);
            }
        }
    }

    public int getNavigationBarHeight()
    {
        boolean hasMenuKey = ViewConfiguration.get(ImageChooseActivity.this).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey)
        {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void changeStatusBarContrastStyle(Window window, Boolean lightIcons) {
        View decorView = window.getDecorView();
        if (lightIcons) {
            // Draw light icons on a dark background color
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            // Draw dark icons on a light background color
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
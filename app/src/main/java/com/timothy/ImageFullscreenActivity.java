package com.timothy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.time.Instant;
import java.time.Year;

import javax.sql.DataSource;

public class ImageFullscreenActivity extends AppCompatActivity {

    private ImageView fullImage, back_btn, share_btn;
    private RelativeLayout top;

    private ProgressBar progress_bar;
    String image = "";
    ScaleGestureDetector scaleGestureDetector;
    float scaleFactor = 1.0f;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_image_fullscreen);

        Window w = ImageFullscreenActivity.this.getWindow();
        changeStatusBarContrastStyle(w, false);

        Intent intent = getIntent();
        image = intent.getStringExtra("parseData");

        fullImage = findViewById(R.id.fullImage);
        top = findViewById(R.id.top);
        back_btn = findViewById(R.id.back_btn);
        share_btn = findViewById(R.id.share_btn);
        progress_bar = findViewById(R.id.progress_bar);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) top.getLayoutParams();
        params.setMargins(0, (getStatusBarHeight() + 20), 0, 0);
        top.setLayoutParams(params);

        fullImage.setImageURI(Uri.parse(image));

        //Glide.with(this).load(image).into(fullImage);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(true);
                        finish();
                    }
                }).playOn(back_btn);
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        share_btn.setEnabled(false);
                        back_btn.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.FadeOut).duration(100).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                progress_bar.setVisibility(View.VISIBLE);
                            }
                        }).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                try {
                                    imageAction();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).playOn(progress_bar);
                    }
                }).playOn(share_btn);
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(this,
                new ScaleListener());

    }

    private void imageAction() throws IOException {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        /*InputStream in = ImageFullscreenActivity.this.getContentResolver().openInputStream(Uri.parse(image));
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        in.close();*/

        File file = new File(getExternalCacheDir() + "/" + getResources().getString(R.string.app_name));
        Intent shareInt;

        YoYo.with(Techniques.FadeOut).duration(100).playOn(progress_bar);

        share_btn.setEnabled(true);
        back_btn.setEnabled(true);

        try {

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.flush();
            outputStream.close();
            shareInt = new Intent(Intent.ACTION_SEND);
            shareInt.setType("image/*");
            shareInt.putExtra(Intent.EXTRA_STREAM, Uri.parse(image));
            shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {
            throw  new RuntimeException(e);
        }

        startActivity(Intent.createChooser(shareInt, "share image"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f,Math.min(scaleFactor,10.0f));

            fullImage.setScaleX(scaleFactor);
            fullImage.setScaleY(scaleFactor);

            return true;
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavigationBarHeight()
    {
        boolean hasMenuKey = ViewConfiguration.get(ImageFullscreenActivity.this).hasPermanentMenuKey();
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
package com.timothy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class ImagePresentationActivity extends AppCompatActivity {

    private ImageView back_btn, background_image;
    private TextView choose_image_btn_txt, continue_btn_txt;
    private CardView choose_image_btn, continue_btn;
    private RelativeLayout bottom;

    String image;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_presentation);

        Window w = ImagePresentationActivity.this.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setStatusBarColor(getColor(R.color.grey));
        changeStatusBarContrastStyle(w, false);



        Intent intent = getIntent();
        image = intent.getStringExtra("image");

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
                        Intent intent1 = new Intent(ImagePresentationActivity.this, ImagePickerActivity.class);
                        intent1.putExtra("image", image);
                        startActivity(intent1);
                        finish();
                    }
                }).playOn(continue_btn_txt);
            }
        });
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
        boolean hasMenuKey = ViewConfiguration.get(ImagePresentationActivity.this).hasPermanentMenuKey();
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
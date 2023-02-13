package com.timothy.Policies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.timothy.MainActivity;
import com.timothy.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AcceptPrivacyPolicyActivity extends AppCompatActivity {

    private CardView accept_btn;
    private TextView accept_btn_txt;
    private RelativeLayout loading_layout;
    private ImageView checked_icon;
    private ProgressBar progress_bar;

    private static final String FILE_NAME2 = "timothy.internal.memory.privacy.policy";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_privacy_policy);

        Window w = AcceptPrivacyPolicyActivity.this.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setStatusBarColor(getColor(R.color.grey));
        changeStatusBarContrastStyle(w, false);

        accept_btn = findViewById(R.id.accept_btn);
        accept_btn_txt = findViewById(R.id.accept_btn_txt);
        loading_layout = findViewById(R.id.loading_layout);
        checked_icon = findViewById(R.id.checked_icon);
        progress_bar = findViewById(R.id.progress_bar);

        accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        accept_btn.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.FadeIn).duration(200).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                loading_layout.setVisibility(View.VISIBLE);
                            }
                        }).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                save();
                            }
                        }).playOn(loading_layout);
                    }
                }).playOn(accept_btn_txt);
            }
        });
    }

    private void save() {
        String txt = "accepted";

        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME2, MODE_PRIVATE);
            fos.write(txt.getBytes());

            YoYo.with(Techniques.FadeOut).duration(200).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    YoYo.with(Techniques.FadeIn).duration(200).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            checked_icon.setVisibility(View.VISIBLE);
                        }
                    }).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            YoYo.with(Techniques.RubberBand).duration(400).onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    Intent intent = new Intent(AcceptPrivacyPolicyActivity.this, AcceptTermsAndConditionsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).playOn(checked_icon);
                        }
                    }).playOn(checked_icon);
                }
            }).playOn(progress_bar);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
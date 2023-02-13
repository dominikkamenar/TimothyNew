package com.timothy.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.timothy.AddBackgroundActivity;
import com.timothy.MainActivity;
import com.timothy.Policies.AcceptPrivacyPolicyActivity;
import com.timothy.Policies.AcceptTermsAndConditionsActivity;
import com.timothy.R;
import com.timothy.Walkthrough.WelcomeActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SplashScreen extends AppCompatActivity {

    private ImageView logo;

    private static final String FILE_NAME = "timothy.internal.memory.walkthrough";
    private static final String FILE_NAME2 = "timothy.internal.memory.privacy.policy";
    private static final String FILE_NAME3 = "timothy.internal.memory.terms.and.conditions";

    private int SPLASH_TIME_OUT = 300;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Window w = SplashScreen.this.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setStatusBarColor(getColor(R.color.grey));
        changeStatusBarContrastStyle(w, false);

        logo = findViewById(R.id.logo);

        loadPrivacyPolicy();
    }

    public void loadPrivacyPolicy() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME2);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String text = "";

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            if (!sb.toString().isEmpty()) {
                if (sb.toString().contains("accepted")) {
                    loadTermsAndConditions();
                } else {
                    YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            logo.setVisibility(View.VISIBLE);
                        }
                    }).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    Intent intent = new Intent(SplashScreen.this, AcceptPrivacyPolicyActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).playOn(logo);
                        }
                    }).playOn(logo);
                }
            } else {
                YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        logo.setVisibility(View.VISIBLE);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                Intent intent = new Intent(SplashScreen.this, AcceptPrivacyPolicyActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).playOn(logo);
                    }
                }).playOn(logo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    logo.setVisibility(View.VISIBLE);
                }
            }).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            Intent intent = new Intent(SplashScreen.this, AcceptPrivacyPolicyActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).playOn(logo);
                }
            }).playOn(logo);
        } catch (IOException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    logo.setVisibility(View.VISIBLE);
                }
            }).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            Intent intent = new Intent(SplashScreen.this, AcceptPrivacyPolicyActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).playOn(logo);
                }
            }).playOn(logo);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadTermsAndConditions() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME3);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String text = "";

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            if (!sb.toString().isEmpty()) {
                if (sb.toString().contains("accepted")) {
                    load();
                } else {
                    YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            logo.setVisibility(View.VISIBLE);
                        }
                    }).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    Intent intent = new Intent(SplashScreen.this, AcceptTermsAndConditionsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).playOn(logo);
                        }
                    }).playOn(logo);
                }
            } else {
                YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        logo.setVisibility(View.VISIBLE);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                Intent intent = new Intent(SplashScreen.this, AcceptTermsAndConditionsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).playOn(logo);
                    }
                }).playOn(logo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    logo.setVisibility(View.VISIBLE);
                }
            }).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            Intent intent = new Intent(SplashScreen.this, AcceptTermsAndConditionsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).playOn(logo);
                }
            }).playOn(logo);
        } catch (IOException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    logo.setVisibility(View.VISIBLE);
                }
            }).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            Intent intent = new Intent(SplashScreen.this, AcceptTermsAndConditionsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).playOn(logo);
                }
            }).playOn(logo);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String text = "";

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            if (!sb.toString().isEmpty()) {
                if (sb.toString().contains("seen")) {
                    YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            logo.setVisibility(View.VISIBLE);
                        }
                    }).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                    intent.putExtra("loadAgain", "true");
                                    startActivity(intent);
                                    finish();
                                }
                            }).playOn(logo);
                        }
                    }).playOn(logo);
                } else {
                    YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            logo.setVisibility(View.VISIBLE);
                        }
                    }).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                                @Override
                                public void call(Animator animator) {
                                    Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).playOn(logo);
                        }
                    }).playOn(logo);
                }
            } else {
                YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        logo.setVisibility(View.VISIBLE);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).playOn(logo);
                    }
                }).playOn(logo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    logo.setVisibility(View.VISIBLE);
                }
            }).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).playOn(logo);
                }
            }).playOn(logo);
        } catch (IOException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    logo.setVisibility(View.VISIBLE);
                }
            }).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).playOn(logo);
                }
            }).playOn(logo);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
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
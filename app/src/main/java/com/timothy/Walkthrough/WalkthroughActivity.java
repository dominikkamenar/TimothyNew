package com.timothy.Walkthrough;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.timothy.MainActivity;
import com.timothy.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WalkthroughActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 4;
    private int mCurrentPage;
    private TextView[] mDots;

    private static final String FILE_NAME = "timothy.internal.memory.walkthrough";

    View view;

    private ImageView previous_btn, next_btn, next_white_cover, previous_white_cover;
    private ViewPager viewPager;
    private LinearLayout mDotLayout;
    private CardView continue_btn;
    private TextView continue_btn_txt;
    private RelativeLayout loading_layout;
    private ProgressBar progress_bar;
    private ImageView checked_icon;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);

        Window w = WalkthroughActivity.this.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setStatusBarColor(getColor(R.color.grey));
        changeStatusBarContrastStyle(w, false);

        continue_btn = findViewById(R.id.continue_btn);
        continue_btn_txt = findViewById(R.id.continue_btn_txt);
        mDotLayout = findViewById(R.id.dots_layout);
        viewPager = findViewById(R.id.view_pager);
        next_btn = findViewById(R.id.next_btn);
        next_white_cover = findViewById(R.id.next_white_cover);
        previous_white_cover = findViewById(R.id.previous_white_cover);
        previous_btn = findViewById(R.id.previous_btn);
        loading_layout = findViewById(R.id.loading_layout);
        progress_bar = findViewById(R.id.progress_bar);
        checked_icon = findViewById(R.id.checked_icon);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(100).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        continue_btn.setEnabled(false);
                        next_white_cover.setVisibility(View.VISIBLE);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {

                        YoYo.with(Techniques.FadeIn).duration(300).onStart(new YoYo.AnimatorCallback() {
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
                }).playOn(continue_btn_txt);
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.FadeIn).duration(100).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        next_white_cover.setVisibility(View.VISIBLE);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.FadeOut).duration(100).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                viewPager.setCurrentItem(mCurrentPage + 1);
                            }
                        }).playOn(next_white_cover);
                    }
                }).playOn(next_white_cover);
            }
        });

        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.FadeIn).duration(100).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        previous_white_cover.setVisibility(View.VISIBLE);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.FadeOut).duration(100).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                viewPager.setCurrentItem(mCurrentPage - 1);
                            }
                        }).playOn(previous_white_cover);
                    }
                }).playOn(previous_white_cover);
            }
        });

        ScreenSlideAdapter screenSlideAdapter = new ScreenSlideAdapter();
        viewPager.setAdapter(screenSlideAdapter);

        addDotsIndicators(0);

        viewPager.addOnPageChangeListener(viewListener);
    }

    private void save() {
        String txt = "seen";

        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(txt.getBytes());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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
                                            continue_btn.setEnabled(true);
                                            Intent intent1 = new Intent(WalkthroughActivity.this, MainActivity.class);
                                            intent1.putExtra("loadAgain", "true");
                                            startActivity(intent1);
                                            finish();
                                        }
                                    }).playOn(checked_icon);
                                }
                            }).playOn(checked_icon);
                        }
                    }).playOn(progress_bar);
                }
            }, 300);

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeOut).duration(200).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    continue_btn.setEnabled(true);
                    Toast.makeText(WalkthroughActivity.this, "An error occurred while trying to approach internal memory. Please check permissions in the settings :)", Toast.LENGTH_SHORT).show();
                }
            }).playOn(loading_layout);
        } catch (IOException e) {
            e.printStackTrace();

            YoYo.with(Techniques.FadeOut).duration(200).onEnd(new YoYo.AnimatorCallback() {
                @Override
                public void call(Animator animator) {
                    continue_btn.setEnabled(true);
                    Toast.makeText(WalkthroughActivity.this, "An error occurred while trying to approach internal memory. Please check permissions in the settings :)", Toast.LENGTH_SHORT).show();
                }
            }).playOn(loading_layout);
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

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicators(position);
            mCurrentPage = position;

            if (position == 0) {
                next_btn.setEnabled(true);
                previous_btn.setEnabled(false);
                continue_btn.setEnabled(false);
                previous_btn.setVisibility(View.INVISIBLE);
                continue_btn.setVisibility(View.INVISIBLE);
                next_btn.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                next_btn.setEnabled(true);
                previous_btn.setEnabled(true);
                continue_btn.setEnabled(false);
                previous_btn.setVisibility(View.VISIBLE);
                continue_btn.setVisibility(View.INVISIBLE);
                next_btn.setVisibility(View.VISIBLE);
            } else if (position == 2) {
                next_btn.setEnabled(true);
                previous_btn.setEnabled(true);
                continue_btn.setEnabled(false);
                previous_btn.setVisibility(View.VISIBLE);
                continue_btn.setVisibility(View.INVISIBLE);
                next_btn.setVisibility(View.VISIBLE);
            } else {
                next_btn.setVisibility(View.INVISIBLE);
                previous_btn.setVisibility(View.VISIBLE);
                continue_btn.setVisibility(View.VISIBLE);
                next_btn.setEnabled(false);
                previous_btn.setEnabled(true);
                continue_btn.setEnabled(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void addDotsIndicators(int position) {
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.darker_grey));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.dark));
        }
    }

    public class ScreenSlideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (RelativeLayout) object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            if (position == 0) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.intro_walkthrough_one_layout, (ViewGroup) container, false);



                ((ViewGroup) container).addView(view);

                return view;
            } else if (position == 1) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.intro_walkthrough_two_layout, (ViewGroup) container, false);



                ((ViewGroup) container).addView(view);

                return view;
            } else if (position == 2) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.intro_walkthrough_three_layout, (ViewGroup) container, false);



                ((ViewGroup) container).addView(view);

                return view;
            } else {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.intro_walkthrough_four_layout, (ViewGroup) container, false);



                ((ViewGroup) container).addView(view);

                return view;
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
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
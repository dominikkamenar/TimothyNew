package com.timothy;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ImageCombinerActivity extends AppCompatActivity {

    private ImageView img, rotate_left, rotate_right, gradient, checked_icon, back_btn, image_background;
    private RelativeLayout save_image, bottom, zoom, move, zoom_clicked, move_clicked, preview, close_preview
            , preview_layout, timothy_logo, loading_layout;
    private TextView degrees, save_image_txt;
    private ProgressBar progress_bar;

    ActivityResultLauncher<String> cropImage;

    String image;
    Bitmap b;

    float xDown = 0, yDown = 0;
    float distanceX = 0, distanceY = 0;
    float movedX = 0, movedY = 0;

    private ScaleGestureDetector scaleGestureDetector;
    private float FACTOR = 1.0f;

    private boolean moveBool = true;
    private boolean isClickable = true;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_image_combiner);

        Intent intent = getIntent();
        image = intent.getStringExtra("image");

        img = findViewById(R.id.img);
        save_image = findViewById(R.id.save_image);
        zoom = findViewById(R.id.zoom);
        move = findViewById(R.id.move);
        bottom = findViewById(R.id.bottom);
        zoom_clicked = findViewById(R.id.zoom_clicked);
        move_clicked = findViewById(R.id.move_clicked);
        rotate_left = findViewById(R.id.rotate_left);
        rotate_right = findViewById(R.id.rotate_right);
        degrees = findViewById(R.id.degrees);
        preview = findViewById(R.id.preview);
        close_preview = findViewById(R.id.close_preview);
        preview_layout = findViewById(R.id.preview_layout);
        gradient = findViewById(R.id.gradient);
        timothy_logo = findViewById(R.id.timothy_logo);
        loading_layout = findViewById(R.id.loading_layout);
        checked_icon = findViewById(R.id.checked_icon);
        progress_bar = findViewById(R.id.progress_bar);
        save_image_txt = findViewById(R.id.save_image_txt);
        back_btn = findViewById(R.id.back_btn);
        image_background = findViewById(R.id.image_background);

        if (image.contains("uri")) {
            image = image.replace(" - uri", "");
            image_background.setImageURI(Uri.parse(image));
        } else {
            image_background.setImageResource(Integer.parseInt(image));
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) preview_layout.getLayoutParams();
        params.setMargins(0, (getStatusBarHeight() + 20), 0, 0);
        preview_layout.setLayoutParams(params);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottom.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, (getNavigationBarHeight() + 20));
        bottom.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) timothy_logo.getLayoutParams();
        layoutParams1.setMargins(0, 0, 32, (getNavigationBarHeight() + 20));
        timothy_logo.setLayoutParams(layoutParams1);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(ImageCombinerActivity.this.openFileInput("myImage"));
            img.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(false);
                        save_image.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        back_btn.setEnabled(true);
                        save_image.setEnabled(true);
                        finish();
                    }
                }).playOn(back_btn);
            }
        });

        save_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        save_image.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ImageCombinerActivity.this);
                        builder.setMessage("Do you want to proceed with saving the image?");
                        builder.setNegativeButton("No", null);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                YoYo.with(Techniques.FadeOut).duration(300).playOn(bottom);
                                YoYo.with(Techniques.FadeOut).duration(300).playOn(gradient);
                                YoYo.with(Techniques.FadeOut).duration(300).playOn(preview_layout);
                                YoYo.with(Techniques.FadeIn).duration(300).onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        timothy_logo.setVisibility(View.VISIBLE);
                                    }
                                }).onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        img.setBackground(null);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap bitmap = takeScreenshot();
                                                saveImage(bitmap);
                                            }
                                        }, 100);
                                    }
                                }).playOn(timothy_logo);
                            }
                        }).create().show();
                    }
                }).playOn(save_image_txt);
            }
        });

        degrees.setText("0.0 °");

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img.setBackground(null);

                YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.SlideInRight).duration(300).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                close_preview.setVisibility(View.VISIBLE);
                            }
                        }).playOn(close_preview);
                    }
                }).playOn(preview);

                YoYo.with(Techniques.SlideOutLeft).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.SlideOutDown).duration(300).playOn(gradient);
                    }
                }).playOn(bottom);
            }
        });

        close_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img.setBackgroundResource(R.drawable.image_background);

                YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.SlideInRight).duration(300).onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                preview.setVisibility(View.VISIBLE);
                            }
                        }).playOn(preview);
                    }
                }).playOn(close_preview);

                YoYo.with(Techniques.SlideInLeft).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.SlideInUp).duration(300).playOn(gradient);
                    }
                }).playOn(bottom);
            }
        });

        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rot = img.getRotation();
                float rot2 = rot - 1;
                img.setRotation(rot2);
                degrees.setText(rot2 + " °");
            }
        });

        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rot = img.getRotation();
                float rot2 = rot + 1;
                img.setRotation(rot2);
                degrees.setText(rot2 + " °");
            }
        });

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!moveBool) {
                    YoYo.with(Techniques.FadeIn).duration(300).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            move_clicked.setVisibility(View.VISIBLE);

                            move.setEnabled(false);
                            zoom.setEnabled(false);
                            moveBool = true;

                            YoYo.with(Techniques.FadeOut).duration(300).playOn(zoom_clicked);
                        }
                    }).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            move.setEnabled(true);
                            zoom.setEnabled(true);
                        }
                    }).playOn(move_clicked);
                }
            }
        });

        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moveBool) {
                    YoYo.with(Techniques.FadeIn).duration(300).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            zoom_clicked.setVisibility(View.VISIBLE);

                            move.setEnabled(false);
                            zoom.setEnabled(false);
                            moveBool = false;

                            YoYo.with(Techniques.FadeOut).duration(300).playOn(move_clicked);
                        }
                    }).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            move.setEnabled(true);
                            zoom.setEnabled(true);
                        }
                    }).playOn(zoom_clicked);
                }
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (moveBool) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                xDown = event.getX();
                yDown = event.getY();
            } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                movedX = event.getX();
                movedY = event.getY();

                distanceX = movedX - xDown;
                distanceY = movedY - yDown;

                img.setX(img.getX() + distanceX);
                img.setY(img.getY() + distanceY);

                xDown = event.getX();
                yDown = event.getY();
            }
        } else {
            scaleGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {

            FACTOR *= detector.getScaleFactor();
            FACTOR = Math.max(0.1f, Math.min(FACTOR, 10.f));
            img.setScaleY(FACTOR);
            img.setScaleX(FACTOR);
            return true;
        }
    }

    public Bitmap takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void saveImage(Bitmap bitmap) {

        Uri image;
        ContentResolver contentResolver = getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            image = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            image = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".tim." + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
        Uri uri = contentResolver.insert(image, contentValues);

        try {

            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);

        } catch (Exception e) {

        }

        Intent intent1 = new Intent(ImageCombinerActivity.this.getApplicationContext(), CropperActivity.class);
        intent1.putExtra("SendImageData", uri.toString());
        intent1.putExtra("type", "image");
        startActivityForResult(intent1, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 101) {
            String result = data.getStringExtra("CROP");
            Uri uri = data.getData();
            if (result != null) {
                uri = Uri.parse(result);

                try {
                    saveImageFinal(getThumbnail(uri));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void saveImageFinal(Bitmap bitmap) {

        Uri image;
        ContentResolver contentResolver = getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            image = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            image = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".timothy." + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
        Uri uri = contentResolver.insert(image, contentValues);

        try {

            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);

        } catch (Exception e) {

        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeIn).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        loading_layout.setVisibility(View.VISIBLE);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
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
                                                YoYo.with(Techniques.RubberBand).duration(500).onEnd(new YoYo.AnimatorCallback() {
                                                    @Override
                                                    public void call(Animator animator) {
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Intent intent = new Intent(ImageCombinerActivity.this, MainActivity.class);
                                                                intent.putExtra("loadAgain", "true");
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }, 200);
                                                    }
                                                }).playOn(checked_icon);
                                            }
                                        }).playOn(checked_icon);
                                    }
                                }).playOn(progress_bar);
                            }
                        }, 500);
                    }
                }).playOn(loading_layout);
            }
        }, 200);
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = ImageCombinerActivity.this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = ImageCombinerActivity.this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
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
        boolean hasMenuKey = ViewConfiguration.get(ImageCombinerActivity.this).hasPermanentMenuKey();
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
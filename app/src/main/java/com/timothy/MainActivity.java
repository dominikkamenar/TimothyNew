package com.timothy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.Animator;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.timothy.Models.Images;
import com.timothy.Models.imageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout top, bottom;
    private CardView choose_image_btn, no_images_layout;
    private TextView choose_image_btn_txt, text;
    private ImageView more;
    private RecyclerView recycler_view;
    private ImagesAdapter imagesAdapter;
    private List<Images> imagesList;
    private List<String> titles;
    private List<Integer> images;
    private List<String> desc;
    private ArrayList<imageModel> list;
    private List<String> names;
    RecyclerView recyclerView;
    ImageAdapter adapter;
    ArrayList<File> myImageFile;
    List<String> mList;

    String loadAgain = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window w = MainActivity.this.getWindow();
        w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        w.setStatusBarColor(getColor(R.color.grey));
        changeStatusBarContrastStyle(w, false);

        Intent intent = getIntent();
        loadAgain = intent.getStringExtra("loadAgain");

        top = findViewById(R.id.top);
        bottom = findViewById(R.id.bottom);
        recycler_view = findViewById(R.id.recycler_view);
        more = findViewById(R.id.more);
        choose_image_btn = findViewById(R.id.choose_image_btn);
        choose_image_btn_txt = findViewById(R.id.choose_image_btn_txt);
        no_images_layout = findViewById(R.id.no_images_layout);
        text = findViewById(R.id.text);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottom.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, (getNavigationBarHeight() + 20));
        bottom.setLayoutParams(layoutParams);

        recycler_view = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(this, 2);
        recycler_view.setLayoutManager(linearLayoutManager_saves);
        imagesList = new ArrayList<>();
        imagesAdapter = new ImagesAdapter(this, imagesList);
        recycler_view.setAdapter(imagesAdapter);

        text.setText("Timothy is developed by one man - \"I'm sure you will have fun using it and making your friends laugh :)\"");

        getImagesListed();

        names = new ArrayList<>();
        names.clear();

        recyclerView = findViewById(R.id.recycler_view_my_photos);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(300).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        more.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        more.setEnabled(true);
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                }).playOn(more);
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
                        Intent intent = new Intent(MainActivity.this, AddBackgroundActivity.class);
                        startActivity(intent);
                    }
                }).playOn(choose_image_btn_txt);
            }
        });

        if (loadAgain != null) {
            if (loadAgain.contains("true")) {
                Dexter.withContext(this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                //display();
                                //ReadSdcard(MainActivity.this);
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
        }
    }

    /*private ArrayList<File> findImage(File file) {

        ArrayList<File> imageList = new ArrayList<>();

        File[] imageFile = file.listFiles();

        assert imageFile != null;
        for (File singleImage : imageFile) {

            if (singleImage.isDirectory() && ! singleImage.isHidden()) {
                imageList.addAll(findImage(singleImage));
            } else {
                if (singleImage.getName().endsWith(".jpg")) {
                    imageList.add(singleImage);
                }
            }

        }

        return imageList;
    }

    private void display() {
        myImageFile = findImage(Environment.getExternalStorageDirectory());

        for (int j = 0; j < myImageFile.size(); j++) {
            mList.add(String.valueOf(myImageFile.get(j)));
        }
    }*/

    private void ReadSdcard(Context context){
        Uri collection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else
        {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        }
        String projection[] = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        list = new ArrayList<>();

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ImageAdapter(list, this);
        recyclerView.setAdapter(adapter);

        names.add("a");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try(Cursor cursor = MainActivity.this.getContentResolver().query(
                    collection,
                    projection,
                    null,
                    null
            )){
                int idColumn = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int i = 1;

                while (cursor.moveToNext()){
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);

                    String name = cursor.getString(nameColumn);


                    if (name.contains("timothy")) {
                        list.add(new imageModel(contentUri));
                    }
                }

                if (list.isEmpty()) {
                    YoYo.with(Techniques.FadeIn).duration(500).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            no_images_layout.setVisibility(View.VISIBLE);
                        }
                    }).playOn(no_images_layout);
                } else {
                    Collections.reverse(list);
                    adapter.notifyDataSetChanged();
                    YoYo.with(Techniques.FadeIn).duration(500).onStart(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }).playOn(recyclerView);
                }
                cursor.close();
            }
        }
    }

    private void getImagesListed() {

        images = new ArrayList<Integer>();
        titles = new ArrayList<>();
        desc = new ArrayList<>();
        titles.clear();
        images.clear();
        desc.clear();

        images.add(R.drawable.dwayne_11);
        titles.add("Dwayne Johnson");
        desc.add("This is description 1, hope you enjoy");

        images.add(R.drawable.background_one);
        titles.add("Party");
        desc.add("This is description 1, hope you enjoy");

        images.add(R.drawable.background_three);
        titles.add("Night Club");
        desc.add("This is description 1, hope you enjoy");

        images.add(R.drawable.background_four);
        titles.add("A Cow");
        desc.add("This is description 1, hope you enjoy");

        /*images.add(R.drawable.taylor);
        titles.add("Dwayne Johnson");
        desc.add("This is description 1, hope you enjoy");

        images.add(R.drawable.joe_biden4_copy);
        titles.add("Joe Biden");
        desc.add("This is description 1, hope you enjoy");

        images.add(R.drawable.trump_5);
        titles.add("Donald Trump");
        desc.add("This is description 1, hope you enjoy");

        images.add(R.drawable.cena);
        titles.add("Tom Cruise");
        desc.add("This is description 1, hope you enjoy");*/

        for (int i = 0; i <= (images.size() - 1); i++) {

            Images imagesModel = new Images();
            imagesModel.setImage(images.get(i));
            imagesModel.setTitle(titles.get(i));
            imagesModel.setDesc(desc.get(i));

            imagesList.add(imagesModel);
        }

        imagesAdapter.notifyDataSetChanged();
    }

    public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

        private Context mContext;
        private List<Images> mealsList;

        public ImagesAdapter(Context mContext, List<Images> mealsList) {
            this.mContext = mContext;
            this.mealsList = mealsList;
        }

        @NonNull
        @Override
        public ImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.background_item, parent, false);
            return new ImagesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImagesAdapter.ViewHolder holder, int position) {
            final Images image = mealsList.get(position);

            if (mContext == null) {
                return;
            }

            holder.title.setText(image.getTitle());
            holder.desc.setText(image.getDesc());

            holder.background_image.setImageResource(image.getImage());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.RubberBand).duration(200).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            //Intent intent = new Intent(MainActivity.this, ImagePresentationActivity.class);
                            Intent intent = new Intent(MainActivity.this, ImageChooseActivity.class);
                            intent.putExtra("image", image.getImage().toString());
                            startActivity(intent);
                        }
                    }).playOn(holder.star);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mealsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView background_image, star;
            private TextView title, desc;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                background_image = itemView.findViewById(R.id.background_image);
                title = itemView.findViewById(R.id.title);
                desc = itemView.findViewById(R.id.desc);
                star = itemView.findViewById(R.id.star);
            }
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
        boolean hasMenuKey = ViewConfiguration.get(MainActivity.this).hasPermanentMenuKey();
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

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.viewHolder> {
        private List<String> list;

        public CustomAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public  CustomAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_items, parent, false);
            return new CustomAdapter.viewHolder(view);
        }

        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(@NonNull CustomAdapter.viewHolder holder, int position) {

            holder.imageView.setImageURI(Uri.parse(list.get(position)));

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class viewHolder extends RecyclerView.ViewHolder
        {
            ImageView imageView;
            public viewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image);
            }
        }
    }
}
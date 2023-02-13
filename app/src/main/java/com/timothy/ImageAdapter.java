package com.timothy;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.timothy.Models.imageModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.viewHolder> {
    private ArrayList<imageModel> list;
    private Context context;

    public ImageAdapter(ArrayList<imageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getPath()).into(holder.imageView);

        holder.title.setText("Image " + position);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parseData = list.get(position).getPath().toString();
                context.startActivity(new Intent(context, ImageFullscreenActivity.class).putExtra("parseData", parseData));
            }
        });

        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RubberBand).duration(200).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        holder.share_btn.setEnabled(false);
                    }
                }).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        holder.share_btn.setEnabled(true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Do you want to share this image?");
                        builder.setNegativeButton("No", null);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.progress_bar.setVisibility(View.VISIBLE);
                                try {
                                    imageAction(list.get(position).getPath(), holder.progress_bar);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).create().show();
                    }
                }).playOn(holder.share_btn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView, share_btn;
        private TextView title;

        private ProgressBar progress_bar;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.background_image);
            title = itemView.findViewById(R.id.title);
            share_btn = itemView.findViewById(R.id.share_btn);
            progress_bar = itemView.findViewById(R.id.progress_bar);
        }
    }

    private void imageAction(Uri uri, ProgressBar progress_bar) throws IOException {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        /*InputStream in = ImageFullscreenActivity.this.getContentResolver().openInputStream(Uri.parse(image));
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        in.close();*/

        File file = new File(context.getExternalCacheDir() + "/" + context.getResources().getString(R.string.app_name));
        Intent shareInt;

        YoYo.with(Techniques.FadeOut).duration(200).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                progress_bar.setVisibility(View.GONE);
            }
        }).playOn(progress_bar);

        try {

            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.flush();
            outputStream.close();
            shareInt = new Intent(Intent.ACTION_SEND);
            shareInt.setType("image/*");
            shareInt.putExtra(Intent.EXTRA_STREAM, uri);
            shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (Exception e) {
            throw  new RuntimeException(e);
        }

        context.startActivity(Intent.createChooser(shareInt, "share image"));
    }
}

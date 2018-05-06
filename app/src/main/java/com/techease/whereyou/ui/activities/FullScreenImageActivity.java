package com.techease.whereyou.ui.activities;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.techease.whereyou.R;
import com.techease.whereyou.controllers.MyGlideApp;

//import com.bumptech.glide.request.animation.GlideAnimation;

//import alessandro.firebaseandroid.adapter.CircleTransform;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ImageView ivUser;
    private TextView tvUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.gc();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindViews() {
        progressDialog = new ProgressDialog(this);
        mImageView = (ImageView) findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivUser = (ImageView) toolbar.findViewById(R.id.avatar);
        tvUser = (TextView) toolbar.findViewById(R.id.title);
    }

    private void setValues() {
        String nameUser, urlPhotoUser, urlPhotoClick;
        nameUser = getIntent().getStringExtra("nameUser");
        urlPhotoUser = getIntent().getStringExtra("urlPhotoUser");
        urlPhotoClick = getIntent().getStringExtra("urlPhotoClick");
        Log.i("TAG", "imagem recebida " + urlPhotoClick);
        tvUser.setText(nameUser); // Name
        Glide.with(this).load(urlPhotoUser).into(ivUser);

        Glide.with(this).load(urlPhotoClick).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                progressDialog.dismiss();
                mImageView.setImageDrawable(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                Toast.makeText(FullScreenImageActivity.this, "Load Fail", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                progressDialog.setMessage("Loading Image...");
                progressDialog.show();
            }
        });
    }

}

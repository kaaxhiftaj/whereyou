package com.techease.whereyou.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.techease.whereyou.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.bumptech.glide.request.animation.GlideAnimation;

//import alessandro.firebaseandroid.adapter.CircleTransform;

public class FullScreenImageActivity extends AppCompatActivity {

    @BindView(R.id.avatar)
    ImageView ivUser;
    @BindView(R.id.title)
    TextView tvUser;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.btn_save_to_device)
    Button btnSaveToDevice;

    private ProgressDialog progressDialog;
    Drawable image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ButterKnife.bind(this);
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                image = resource;

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

    @OnClick(R.id.btn_save_to_device)
    public void onViewClicked() {
        Dexter.withActivity(FullScreenImageActivity.this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (image != null) {
                    saveImage(drawableToBitmap(image));
                } else {
                    Toast.makeText(FullScreenImageActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void saveImage(Bitmap image) {
        String savedImagePath = null;

        String imageFileName = "JPEG_" + "FILE_NAME" + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/WhereYou");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            try {
                File imageFile = new File(storageDir, imageFileName);
                savedImagePath = imageFile.getAbsolutePath();

                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
            Toast.makeText(this, "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
}

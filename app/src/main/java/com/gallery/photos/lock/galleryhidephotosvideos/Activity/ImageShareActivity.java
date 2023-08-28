package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.preference.PowerPreference;

import java.io.File;

public class ImageShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_share);
        setview();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.imgHome).setOnClickListener(v -> {
            if (ViewFolderImagesActivity.activity != null) {
                ViewFolderImagesActivity.activity.finish();
            }
            onBackPressed();
        });

        ImageView imgPreview = findViewById(R.id.imgPreview);
        ImageView imgShare = findViewById(R.id.imgShare);
        ImageView imgwp = findViewById(R.id.imgwp);
        ImageView imgfb = findViewById(R.id.imgfb);
        ImageView imginsta = findViewById(R.id.imginsta);
        ImageView imgtwitter = findViewById(R.id.imgtwitter);
        ImageView imgmessenger = findViewById(R.id.imgmessenger);
        ImageView imggmail = findViewById(R.id.imggmail);

        String path = getIntent().getStringExtra("path");

        Glide.with(this).load(path).into(imgPreview);

        imgShare.setOnClickListener(view -> {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(path)));

            startActivity(Intent.createChooser(shareIntent, "Share Images Using"));
        });

        imgwp.setOnClickListener(v -> shareVideo("Whatsapp", "com.whatsapp", path));

        imgfb.setOnClickListener(v -> shareVideo("Facebook", "com.facebook.katana", path));

        imginsta.setOnClickListener(v -> shareVideo("Instagram", "com.instagram.android", path));

        imgmessenger.setOnClickListener(v -> shareVideo("Facebook Messenger", "com.facebook.orca", path));

        imggmail.setOnClickListener(v -> shareVideo("Gmail", "com.google.android.gm", path));

        imgtwitter.setOnClickListener(v -> shareVideo("Twitter", "com.twitter.android", path));

    }

    public void shareVideo(String name, String shareFlag, String createdVideo) {
        File mFile = new File(createdVideo);
        if (mFile.exists()) {
            PackageManager pm = getPackageManager();
            boolean isInstalled = isInstall(shareFlag, pm);

            if (isInstalled) {
                try {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    Uri uri = Uri.fromFile(mFile);
                    Intent videoshare = new Intent(Intent.ACTION_SEND);
                    videoshare.setType("image/*");
                    if (shareFlag != null) videoshare.setPackage(shareFlag);
                    videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    videoshare.putExtra(Intent.EXTRA_STREAM, uri);
                    videoshare.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    startActivity(videoshare);
                } catch (Exception ignored) {
                }
            } else {
                Utils.getInstance().showError(this, name + " is not Installed.");
            }
        } else {
            Utils.getInstance().showError(this, "Something went Wrong...!!");
        }
    }

    @Override
    public void onBackPressed() {
        new BackInterAds().showInterAds(this, new BackInterAds.OnAdClosedListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    public static boolean isInstall(String pName, PackageManager pm) {
        boolean found = true;
        try {
            pm.getPackageInfo(pName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (PowerPreference.getDefaultFile().getBoolean(Constant.FullScreenOnOff, true)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        new MiniNativeAds().showNativeAds(this, null);
    }
}

package com.gallery.photos.lock.galleryhidephotosvideos.Activity;


import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.folderData;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.LargeNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.gallery.photos.lock.galleryhidephotosvideos.databinding.ActivityShowDetailsBinding;
import com.gallery.photos.lock.galleryhidephotosvideos.databinding.ActivitySplashBinding;
import com.preference.PowerPreference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShowDetailsActivity extends AppCompatActivity {

    ActivityShowDetailsBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setview();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (PowerPreference.getDefaultFile().getBoolean(Constant.FullScreenOnOff, true)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }


        new LargeNativeAds().showNativeAds(this, null);
    }

    private void setview() {
        binding.imgBack.setOnClickListener(view -> onBackPressed());

        if (getIntent().hasExtra("isFromFolder")) {

            File file = new File(folderData.get(getIntent().getStringExtra("path")).get(0)).getParentFile();

            binding.txtTimeTitle.setText("Count");
            binding.llHeight.setVisibility(View.GONE);
            binding.llWidth.setVisibility(View.GONE);

            binding.txtTitle.setText(file.getName());
            binding.txtPath.setText(file.getAbsolutePath());
            binding.txtTime.setText("" + folderData.get(getIntent().getStringExtra("path")).size());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    long size = Files.walk(file.toPath())
                            .map(f -> f.toFile())
                            .filter(f -> f.isFile())
                            .mapToLong(f -> f.length()).sum();


                    binding.txtSize.setText(getFileSize(size));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                binding.txtSize.setText(getFileSize(getFolderSize(file)));
            }
        } else {
            File file = new File(getIntent().getStringExtra("path"));
            if (file.getAbsolutePath().contains("storage")) {
                long date = Utils.getInstance().getimageInfo(file.getAbsolutePath(), this);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);

                if (calendar.get(Calendar.YEAR) == 1970) {
                    calendar.setTimeInMillis(date * 1000);
                }

                SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String datestr = timeStampFormat.format(calendar.getTime());
                binding.txtTime.setText(datestr);
            } else {
                findViewById(R.id.llTime).setVisibility(View.GONE);
            }

            binding.txtTitle.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
            binding.txtSize.setText(getFileSize(file));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;

            binding.txtHeight.setText(imageHeight + "");
            binding.txtWidth.setText(imageWidth + "");

            binding.txtPath.setText(file.getAbsolutePath());

            boolean isImage;
            if (file.getAbsolutePath().endsWith(".nomedia")) {
                isImage = Utils.getInstance().isImageTypeForHidden(file.getAbsolutePath());
            } else {
                isImage = Utils.getInstance().isImageType(file.getAbsolutePath(), ShowDetailsActivity.this);
            }

            if (!isImage) {
                binding.llHeight.setVisibility(View.GONE);
                binding.llWidth.setVisibility(View.GONE);
            }
        }
    }

    public long getFolderSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            assert fileList != null;
            for (File file : fileList) {
                if (file.isDirectory()) {
                    result += getFolderSize(file);
                } else {
                    result += file.length();
                }
            }
            return result;
        }
        return 0;
    }


    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;

    public String getFileSize(File file) {

        if (!file.isFile()) {
            throw new IllegalArgumentException("Expected a file");
        }
        final double length = file.length();

        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }

    public String getFileSize(long length) {

        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }
}

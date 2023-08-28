package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.HiddenImages;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.binImages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.HideImageFoldersFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.CustomVideoView;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Database;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.ViewPagerFixed;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.jsibbold.zoomage.ZoomageView;
import com.preference.PowerPreference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ViewRecycledImageActivity extends AppCompatActivity {

    public static Activity activity;
    private String path;
    private boolean isstart = true;
    private boolean ispagescrolled = false;
    private ZoomageView imgPreview;

    MediaMetadataRetriever mediaMetadataRetriever;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.activity_view_recycled_image);

        mediaMetadataRetriever = new MediaMetadataRetriever();
        path = getIntent().getStringExtra("path");

        setview();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());

        ViewPagerFixed pager = findViewById(R.id.pager);

        pager.setOffscreenPageLimit(0);
        pager.setSaveFromParentEnabled(false);

        TextView txttitle = findViewById(R.id.txttitle);
        txttitle.setText(new File(path).getName());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (!isstart) {
                    ispagescrolled = true;
                } else {
                    isstart = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                ispagescrolled = false;
                path = binImages.get(position);

                txttitle.setText(new File(path).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                ispagescrolled = false;
            }
        });

        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return binImages.size();
            }

            @NonNull
            @NotNull
            @Override
            public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

                View inflate = LayoutInflater.from(ViewRecycledImageActivity.this).inflate(R.layout.item_view_photo, container, false);
                CustomVideoView videoview;

                String path = "";
                File filesrc;
                boolean isImage;
                path = binImages.get(position);

                filesrc = new File(path);

                if (path.endsWith(".nomedia")) {
                    isImage = Utils.getInstance().isImageTypeForHidden(path);
                } else {
                    isImage = Utils.getInstance().isImageType(path, ViewRecycledImageActivity.this);
                }

                videoview = inflate.findViewById(R.id.videoview);
                imgPreview = inflate.findViewById(R.id.imgPreview);
                RelativeLayout relVideoView = inflate.findViewById(R.id.relVideoView);

                if (isImage) {

                    relVideoView.setVisibility(View.GONE);
                    imgPreview.setVisibility(View.VISIBLE);

                    Glide.with(ViewRecycledImageActivity.this).load(path).into(imgPreview);

                } else {

                    relVideoView.setVisibility(View.VISIBLE);
                    imgPreview.setVisibility(View.GONE);

                    ImageView imgPlay = inflate.findViewById(R.id.imgPlay);

                    MediaController mediaController = new MediaController(ViewRecycledImageActivity.this);
                    mediaController.setAnchorView(videoview);

                    videoview.setMediaController(null);

                    try {
                        mediaMetadataRetriever.setDataSource(path);
                        if (mediaMetadataRetriever.getFrameAtTime() != null) {
                            videoview.setVideoURI(Uri.fromFile(filesrc));
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (mediaMetadataRetriever != null) {
                            try {
                                mediaMetadataRetriever.release();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        mediaMetadataRetriever = new MediaMetadataRetriever();
                    }
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                            if (ispagescrolled) {
                                if (videoview.isPlaying())
                                    videoview.pause();

                                if (mediaController.isShowing())
                                    mediaController.hide();

                            } else {
                                handler.removeCallbacks(this);
                            }

                            if (videoview.isPlaying())
                                handler.postDelayed(this, 100);
                        }
                    };

                    videoview.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
                        @Override

                        public void onPlay() {
                            imgPlay.setVisibility(View.GONE);

                            handler.postDelayed(runnable, 100);
                        }

                        @Override
                        public void onPause() {
                            imgPlay.setVisibility(View.VISIBLE);
                        }
                    });

                    videoview.setOnCompletionListener(mp -> imgPlay.setVisibility(View.VISIBLE));

                    videoview.setOnPreparedListener(mp -> {
                        videoview.start();

                        if (mediaController.isShowing())
                            mediaController.hide();

                        new Handler().postDelayed(() -> videoview.pause(), 5);
                    });

                    imgPlay.setOnClickListener(v -> {
                        videoview.setMediaController(mediaController);
                        videoview.start();
                    });
                }

                container.addView(inflate);
                return inflate;
            }

            @Override
            public void destroyItem(@NotNull ViewGroup collection, int position, @NotNull Object view) {
                collection.removeView((View) view);
            }

            @Override
            public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
                return (view == object);
            }

            @Override
            public int getItemPosition(@NotNull Object object) {
                return PagerAdapter.POSITION_NONE;
            }

        };

        pager.setAdapter(adapter);
        pager.setCurrentItem(binImages.indexOf(path));

        findViewById(R.id.imgDetails).setOnClickListener(view -> {
            new InterAds().showInterAds(ViewRecycledImageActivity.this, new InterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(ViewRecycledImageActivity.this, ShowDetailsActivity.class).putExtra("path", path));
                }
            });
        });

        findViewById(R.id.llDelete).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete");
            builder.setMessage("Are you sure to delete this file?");
            builder.setPositiveButton("OK", (dialog, which) -> {
                new File(path).delete();

                if (binImages.size() <= 1) {
                    binImages.clear();
                    adapter.notifyDataSetChanged();
                    if (binImages.size() <= 0) {
                        ViewRecycledImageActivity.this.onBackPressed();
                    }
                } else {
                    binImages.remove(path);
                    adapter.notifyDataSetChanged();
                }

            });

            builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        findViewById(R.id.llRestore).setOnClickListener(v -> {

            if (SplashActivity.isDataLoaded) {
                restore();
            } else {
                Utils.getInstance().showImageLoadingDialog(ViewRecycledImageActivity.this, () -> restore());
            }
        });
    }

    private void restore() {

        File destFile = new File(Database.getInstance(this).getBinInfo(new File(path).getName()));

        if (destFile.getParentFile() != null) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            Utils.getInstance().copyImageFile(this, new File(path), destFile);

            if (!destFile.getAbsolutePath().endsWith(".nomedia"))
                Utils.getInstance().addFiles(destFile.getAbsolutePath(), ViewRecycledImageActivity.this);
            else {
                Utils.getInstance().getHiddenImages(ViewRecycledImageActivity.this, new Utils.hidedone() {
                    @Override
                    public void hideComplete() {
                        if (HideImageFoldersFragment.stickyList != null && HideImageFoldersFragment.llNoDataFound != null) {
                            HideImageFoldersFragment.mAdapter.notifyDataSetChanged();
                            if (HiddenImages.size() > 0) {
                                HideImageFoldersFragment.stickyList.setVisibility(View.VISIBLE);
                                HideImageFoldersFragment.llNoDataFound.setVisibility(View.GONE);
                            } else {
                                HideImageFoldersFragment.stickyList.setVisibility(View.GONE);
                                HideImageFoldersFragment.llNoDataFound.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, null);
            }
        } else {
            new File(path).delete();
        }

        binImages.remove(path);
        new File(path).delete();

        ArrayList<String> data = new ArrayList<>();
        data.add(path);
        Database.getInstance(ViewRecycledImageActivity.this).deleteFromBin(data);
        Utils.getInstance().showSuccess(ViewRecycledImageActivity.this, "Restored Successfully");
        finish();
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


        new MiniNativeAds().showNativeAds(this, null);

    }
}

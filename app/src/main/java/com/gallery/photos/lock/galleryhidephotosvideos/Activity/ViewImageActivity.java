package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import static com.gallery.photos.lock.galleryhidephotosvideos.Activity.MainActivity.llSelectedOptions;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.DefaultFolderData;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.HiddenImages;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.HiddenImagesWithoutFolder;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimages;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimagesCopyWithoutDates;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.folderData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.AllPhotosFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.AllPhotosFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.FolderFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.HideImageFoldersFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;

import com.gallery.photos.lock.galleryhidephotosvideos.Helper.CustomVideoView;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Database;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.MyApplication;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.jsibbold.zoomage.ZoomageView;
import com.preference.PowerPreference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewImageActivity extends AppCompatActivity {

    MediaMetadataRetriever mediaMetadataRetriever;
    private int angle = 0;
    private Bitmap bm;
    public static Activity activity;
    private String path;

    private long date;
    private boolean isFav = false;
    private boolean isFromRotate;
    private boolean isImage = true;
    private ZoomageView imgPreview;
    private ArrayList<String> data = new ArrayList<>();
    private boolean ispagescrolled = false;
    private boolean isstart = true;
    int position = 0;
    private ViewPager pager;
    ImageView imgFav;
    LinearLayout llEdit;
    TextView txttitle;
    // type

    // 1 - all photos
    // 2 - Hidden images
    // 3 - folder images

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        setContentView(R.layout.activity_view_image);
        mediaMetadataRetriever = new MediaMetadataRetriever();

        position = getIntent().getIntExtra("position", 0);
        if (getIntent().getIntExtra("type", 0) == 1) {
            data.addAll(allimagesCopyWithoutDates);
        } else if (getIntent().getIntExtra("type", 0) == 2) {
            data.addAll(HiddenImagesWithoutFolder);
        } else if (getIntent().getIntExtra("type", 0) == 3) {
            data.addAll(MyApplication.dataArr);
            ArrayList<String> dataarr = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).contains("storage"))
                    dataarr.add(data.get(i));
            }
            data.clear();
            data.addAll(dataarr);
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setview();
    }


    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(view -> onBackPressed());

        pager = findViewById(R.id.pager);

        ImageView imgDetails = findViewById(R.id.imgDetails);

        LinearLayout llShare = findViewById(R.id.llShare);
        LinearLayout llHide = findViewById(R.id.llHide);
        imgFav = findViewById(R.id.imgFav);
        llEdit = findViewById(R.id.llEdit);
        LinearLayout llDelete = findViewById(R.id.llDelete);
        LinearLayout llMore = findViewById(R.id.llMore);


        txttitle = findViewById(R.id.txttitle);

        path = getIntent().getStringExtra("path");

        if (path.endsWith(".nomedia")) {
            llDelete.setVisibility(View.GONE);
            llMore.setVisibility(View.GONE);
            llEdit.setVisibility(View.GONE);
        }

        imgFav.setVisibility(View.GONE);

        pager.setOffscreenPageLimit(0);
        pager.setSaveFromParentEnabled(false);

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

                try {
                    if (position >= data.size()) {
                        position = position - 1;
                    }

                    ispagescrolled = false;
                    path = data.get(position);

                    if (path.endsWith(".nomedia")) {
                        llDelete.setVisibility(View.GONE);
                        llMore.setVisibility(View.GONE);
                        llEdit.setVisibility(View.GONE);
                    }


                    isFav = Database.getInstance(ViewImageActivity.this).isAddedToFav(path);

                    if (isFav) {
                        imgFav.setImageResource(R.drawable.ic_favourite_red);
                    } else {
                        imgFav.setImageResource(R.drawable.ic_fav_gray);
                    }

                    if (path.endsWith(".nomedia")) {
                        imgFav.setVisibility(View.GONE);

                        date = new File(path).getAbsoluteFile().lastModified();
                        isImage = Utils.getInstance().isImageTypeForHidden(path);
                    } else {
                        if (path.contains("storage")) {
                            date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                            imgFav.setVisibility(View.VISIBLE);
                        }

                        isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
                    }

                    if (isImage) {
                        new Thread(() -> bm = getImageExIF(path)).start();

                        llEdit.setVisibility(View.VISIBLE);
                    } else {
                        llEdit.setVisibility(View.GONE);
                    }

                    /*if (date > 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(date);

                        if (calendar.get(Calendar.YEAR) == 1970) {
                            calendar.setTimeInMillis(date * 1000);
                        }

                        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd,yyyy");

                        String datestr = timeStampFormat.format(calendar.getTime());
                        if (datestr.equals(getTodayDate(true))) {
                            txttitle.setText("Today");
                        } else if (datestr.equals(getTodayDate(false))) {
                            txttitle.setText("Yesterday");
                        } else {
                            try {
                                String dates = datestr;
                                SimpleDateFormat spf = new SimpleDateFormat("MMM dd,yyyy");
                                Date newDate = spf.parse(dates);
                                spf = new SimpleDateFormat("dd MMMM yyyy");
                                dates = spf.format(newDate);
                                txttitle.setText(dates);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        txttitle.setText("Photos");
                    }
*/

                    txttitle.setText(new File(path).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                ispagescrolled = false;
            }
        });

        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return data.size();
            }

            @NonNull
            @NotNull
            @Override
            public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {

                View inflate = LayoutInflater.from(ViewImageActivity.this).inflate(R.layout.item_view_photo, container, false);
                try {

                    CustomVideoView videoview;

                    String path = "";
                    File filesrc;
                    boolean isImage;

                    if (position >= data.size()) {
                        position = position - 1;
                    }

                    path = data.get(position);


                    filesrc = new File(path);

                    if (path.endsWith(".nomedia")) {
                        imgFav.setVisibility(View.GONE);
                        isImage = Utils.getInstance().isImageTypeForHidden(path);
                    } else {
                        isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);

                        if (path.contains("storage"))
                            imgFav.setVisibility(View.VISIBLE);
                    }

                    if (position == 0) {

                        isFav = Database.getInstance(ViewImageActivity.this).isAddedToFav(path);

                        if (isFav) {
                            imgFav.setImageResource(R.drawable.ic_favourite_red);
                        } else {
                            imgFav.setImageResource(R.drawable.ic_favourite_gray);
                        }
                    }

                    videoview = inflate.findViewById(R.id.videoview);
                    imgPreview = inflate.findViewById(R.id.imgPreview);
                    RelativeLayout relVideoView = inflate.findViewById(R.id.relVideoView);

                    if (isImage) {

                        relVideoView.setVisibility(View.GONE);
                        imgPreview.setVisibility(View.VISIBLE);

                        if (!isFromRotate) {
                            Glide.with(ViewImageActivity.this).load(path).into(imgPreview);

                        } else {
                            isFromRotate = false;
                            Glide.with(ViewImageActivity.this).load(bm).into(imgPreview);
                        }


                    } else {

                        relVideoView.setVisibility(View.VISIBLE);
                        imgPreview.setVisibility(View.GONE);

                        ImageView imgPlay = inflate.findViewById(R.id.imgPlay);

                        Handler handler = new Handler();

                        MediaController mediaController = new MediaController(ViewImageActivity.this);
                        mediaController.setAnchorView(videoview);

                        videoview.setMediaController(null);

                        try {
                            mediaMetadataRetriever.setDataSource(path);
                            if (mediaMetadataRetriever.getFrameAtTime() != null) {
                                videoview.setVideoURI(Uri.fromFile(filesrc));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (mediaMetadataRetriever != null)
                                mediaMetadataRetriever.release();
                            mediaMetadataRetriever = new MediaMetadataRetriever();
                        }


                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                                if (ispagescrolled) {
                                    if (videoview != null && videoview.isPlaying())
                                        videoview.pause();

                                    if (mediaController != null && mediaController.isShowing())
                                        mediaController.hide();

                                } else {
                                    handler.removeCallbacks(this);
                                }

                                if (videoview != null && videoview.isPlaying())
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
                            try {
                                videoview.setMediaController(mediaController);
                                videoview.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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
        if (data.size() > 1) {
            pager.setCurrentItem(data.indexOf(path));
        }

        if (path.endsWith(".nomedia")) {
            imgFav.setVisibility(View.GONE);

            date = new File(path).getAbsoluteFile().lastModified();
            isImage = Utils.getInstance().isImageTypeForHidden(path);
        } else {
            if (path.contains("storage")) {
                date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                imgFav.setVisibility(View.VISIBLE);
            }

            isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
        }

        if (isImage) {
            new Thread(() -> bm = getImageExIF(path)).start();
            llEdit.setVisibility(View.VISIBLE);
        } else {
            llEdit.setVisibility(View.GONE);
        }

        txttitle.setText(new File(path).getName());

        imgDetails.setOnClickListener(view -> {
           /* new InterAds().showInterAds(ViewImageActivity.this, new InterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {*/
                    startActivity(new Intent(ViewImageActivity.this, ShowDetailsActivity.class).putExtra("path", path));
                /*}
            });*/
        });

        imgFav.setOnClickListener(view -> {
            Database.getInstance(this).addToFavourite(path, !isFav);

            isFav = Database.getInstance(ViewImageActivity.this).isAddedToFav(path);

            if (isFav) {
                imgFav.setImageResource(R.drawable.ic_favourite_red);
            } else {
                imgFav.setImageResource(R.drawable.ic_favourite_gray);
            }
        });

        if (getIntent().hasExtra("isFromHidden")) {

            ImageView imgUnlock = findViewById(R.id.imglock);
            TextView txtlock = findViewById(R.id.txtlock);

            txtlock.setText("Unhide");
            imgUnlock.setImageResource(R.drawable.ic_unlock);
        }

        llShare.setOnClickListener(view -> {

            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                if (isImage)
                    shareIntent.setType("image/*");
                else
                    shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(ViewImageActivity.this, getPackageName() + ".provider", new File(path)));

                startActivity(Intent.createChooser(shareIntent, "Share Images Using"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        llHide.setOnClickListener(v -> {

            if (SplashActivity.isDataLoaded) {
                hideunhide(adapter);
            } else {
                Utils.getInstance().showImageLoadingDialog(ViewImageActivity.this, () -> hideunhide(adapter));
            }
        });

        llEdit.setOnClickListener(v -> {
            new InterAds().showInterAds(ViewImageActivity.this, new InterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(ViewImageActivity.this, EditImageActivity.class).putExtra("path", path));
                }
            });
        });

        llDelete.setOnClickListener(view -> {
            if (SplashActivity.isDataLoaded) {
                delete(adapter);
            } else {
                Utils.getInstance().showImageLoadingDialog(ViewImageActivity.this, () -> delete(adapter));
            }
        });

        llMore.setOnClickListener(view -> {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View popupView = layoutInflater.inflate(R.layout.popu_up_more_view_image, null);

            PopupWindow infoPopup = new PopupWindow(popupView, (int) getResources().getDimension(com.intuit.sdp.R.dimen._130sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
            infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            infoPopup.setOutsideTouchable(true);

            TextView txtAddtoAlbum = popupView.findViewById(R.id.txtAddtoAlbum);
            RelativeLayout relRotate = popupView.findViewById(R.id.relRotate);
            TextView txtRename = popupView.findViewById(R.id.txtRename);
            TextView txtSetas = popupView.findViewById(R.id.txtSetas);

            if (isImage) {
                relRotate.setVisibility(View.VISIBLE);
                txtSetas.setVisibility(View.VISIBLE);
            } else {
                relRotate.setVisibility(View.GONE);
                txtSetas.setVisibility(View.GONE);
            }

            txtAddtoAlbum.setOnClickListener(view1 -> {
                infoPopup.dismiss();

                ArrayList<String> selectedItems = new ArrayList<>();
                selectedItems.add(path);

                new InterAds().showInterAds(ViewImageActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(ViewImageActivity.this, AddtoAlbumActivity.class).putExtra("selected", selectedItems).putExtra("isFromView", true));

                    }
                });
            });

            txtRename.setOnClickListener(view12 -> {
                infoPopup.dismiss();

                File filesrc = new File(path);
                String oldName = filesrc.getName();
                Utils.getInstance().renameFilePopup(this, filesrc.getName().substring(0, filesrc.getName().lastIndexOf(".")), name -> {

                    File oldFolder = filesrc.getParentFile();
                    File newFolder = new File(oldFolder, name + filesrc.getName().substring(filesrc.getName().lastIndexOf(".")));

                    boolean temp = filesrc.renameTo(newFolder);

                    if (temp) {

                        Utils.getInstance().scanMedia(ViewImageActivity.this, newFolder.getAbsolutePath());

                        String foldername = oldFolder.getParentFile().getName();

                        if (folderData.containsKey(foldername)) {
                            ArrayList<String> data = new ArrayList<>();
                            data.addAll(folderData.get(foldername));

                            if (data.contains(path)) {
                                data.set(data.indexOf(path), newFolder.getAbsolutePath());
                            }

                            folderData.put(foldername, data);
                        } else if (DefaultFolderData.containsKey(foldername)) {
                            ArrayList<String> data = new ArrayList<>();
                            data.addAll(DefaultFolderData.get(foldername));
                            if (data.contains(path)) {
                                data.set(data.indexOf(path), newFolder.getAbsolutePath());
                            }

                            DefaultFolderData.put(foldername, data);
                        }

                        if (allimages.contains(path))
                            allimages.set(allimages.indexOf(path), newFolder.getAbsolutePath());

                        AllPhotosFragment.mAdapter.adddata(allimages);
                        AllPhotosFragment.mAdapter.notifyDataSetChanged();

                        FolderFragment.adapterDefault.addData(DefaultFolderData);
                        FolderFragment.adapter.addData(folderData);

                        path = newFolder.getAbsolutePath();
                        txttitle.setText(new File(path).getName());
                    }
                });
            });

            txtSetas.setOnClickListener(v -> {
                infoPopup.dismiss();
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent setAs = new Intent(Intent.ACTION_ATTACH_DATA);
                setAs.addCategory(Intent.CATEGORY_DEFAULT);
                Uri sourceUri = Uri.fromFile(new File(path));
                setAs.setDataAndType(sourceUri, "image/*");
                setAs.putExtra("mimeType", "image/*");
                setAs.putExtra("save_path", sourceUri);
                startActivity(Intent.createChooser(setAs, "Select service:"));
            });

            relRotate.setOnClickListener(view13 -> {
                infoPopup.dismiss();

                LayoutInflater layoutInflater1 = LayoutInflater.from(this);
                View popupView1 = layoutInflater1.inflate(R.layout.popup_rotate, null);

                PopupWindow infoPopup1 = new PopupWindow(popupView1, (int) getResources().getDimension(com.intuit.sdp.R.dimen._130sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
                infoPopup1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                infoPopup1.setOutsideTouchable(true);

                TextView txtRotateLeft = popupView1.findViewById(R.id.txtRotateLeft);
                TextView txtRotateRight = popupView1.findViewById(R.id.txtRotateRight);
                TextView txtFlip = popupView1.findViewById(R.id.txFlip);

                txtFlip.setOnClickListener(view14 -> {

                    isFromRotate = true;
                    infoPopup1.dismiss();

                    flipBitmap(bm);
                    adapter.notifyDataSetChanged();


                    Utils.getInstance().saveBitmapToStorage(bm, new File(path));

                    isFromRotate = false;

                });

                txtRotateLeft.setOnClickListener(view16 -> {
                    isFromRotate = true;

                    infoPopup1.dismiss();

                    rotateBitmap(bm, angle - 90);
                    adapter.notifyDataSetChanged();

                    Utils.getInstance().saveBitmapToStorage(bm, new File(path));

                    isFromRotate = false;
                });

                txtRotateRight.setOnClickListener(view15 -> {
                    isFromRotate = true;

                    infoPopup1.dismiss();
                    rotateBitmap(bm, angle + 90);
                    adapter.notifyDataSetChanged();

                    Utils.getInstance().saveBitmapToStorage(bm, new File(path));

                    isFromRotate = false;

                });

                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                infoPopup1.showAtLocation(view, Gravity.BOTTOM, size.x - view.getWidth(), view.getBottom() + (int) getResources().getDimension(com.intuit.sdp.R.dimen._30sdp));

            });

            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            infoPopup.showAtLocation(view, Gravity.BOTTOM, size.x - view.getWidth(), view.getBottom() + (int) getResources().getDimension(com.intuit.sdp.R.dimen._30sdp));
        });
    }

    private void hideunhide(PagerAdapter adapter) {
        if (getIntent().hasExtra("isFromHidden")) {

            Utils.getInstance().unhideImage(path, ViewImageActivity.this);

            Utils.getInstance().showSuccess(ViewImageActivity.this, "Unhided from privacy Successfully");
            finish();

            if (HiddenImages.size() > 0) {
                HideImageFoldersFragment.stickyList.setVisibility(View.VISIBLE);
                HideImageFoldersFragment.llNoDataFound.setVisibility(View.GONE);
            } else {
                HideImageFoldersFragment.stickyList.setVisibility(View.GONE);
                HideImageFoldersFragment.llNoDataFound.setVisibility(View.VISIBLE);
            }

        } else {
            ArrayList<String> dataq = new ArrayList<>();
            dataq.add(path);

            Utils.getInstance().hidePhotos(dataq, this, false, () -> runOnUiThread(() -> {

                data.remove(path);
                adapter.notifyDataSetChanged();

                if (data.size() <= 0) {
                    onBackPressed();
                } else {
                    path = data.get(pager.getCurrentItem());

                    if (path.endsWith(".nomedia")) {
                        imgFav.setVisibility(View.GONE);

                        date = new File(path).getAbsoluteFile().lastModified();
                        isImage = Utils.getInstance().isImageTypeForHidden(path);
                    } else {
                        if (path.contains("storage")) {
                            date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                            imgFav.setVisibility(View.VISIBLE);
                        }

                        isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
                    }

                    if (isImage) {
                        new Thread(() -> bm = getImageExIF(path)).start();
                        llEdit.setVisibility(View.VISIBLE);
                    } else {
                        llEdit.setVisibility(View.GONE);
                    }

                    txttitle.setText(new File(path).getName());


                }
            }));
        }
    }

    private void delete(PagerAdapter adapter) {

        Utils.getInstance().recycleImage(path, ViewImageActivity.this);
        Utils.getInstance().removeImage(path, ViewImageActivity.this);


        AllPhotosFragment.mAdapter.adddata(allimages);
        AllPhotosFragment.mAdapter.notifyDataSetChanged();

        FolderFragment.adapterDefault.notifyDataSetChanged();
        FolderFragment.adapter.notifyDataSetChanged();

        if (data.size() > 0)
            data.remove(path);

        adapter.notifyDataSetChanged();

        if (data.size() <= 0) {
            onBackPressed();
        } else {
            path = data.get(pager.getCurrentItem());


            if (path.endsWith(".nomedia")) {
                imgFav.setVisibility(View.GONE);

                date = new File(path).getAbsoluteFile().lastModified();
                isImage = Utils.getInstance().isImageTypeForHidden(path);
            } else {
                if (path.contains("storage")) {
                    date = Utils.getInstance().getimageInfo(path, ViewImageActivity.this);
                    imgFav.setVisibility(View.VISIBLE);
                }

                isImage = Utils.getInstance().isImageType(path, ViewImageActivity.this);
            }

            if (isImage) {
                new Thread(() -> bm = getImageExIF(path)).start();
                llEdit.setVisibility(View.VISIBLE);
            } else {
                llEdit.setVisibility(View.GONE);
            }

            txttitle.setText(new File(path).getName());

        }

//        pager.setCurrentItem(pager.getCurrentItem() + 1);
//        pager.setCurrentItem(pager.getCurrentItem() - 1);


    }

    public static Bitmap getImageExIF(String str) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        int i = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        Bitmap decodeFile = BitmapFactory.decodeFile(str, new BitmapFactory.Options());

        try {
            String attribute = new ExifInterface(str).getAttribute("Orientation");
            if (attribute != null) {
                i = Integer.parseInt(attribute);
            }

            int i2 = 0;
            if (i == 6) {
                i2 = 90;
            }
            if (i == 3) {
                i2 = 180;
            }
            if (i == 8) {
                i2 = 270;
            }

            Matrix matrix = new Matrix();
            matrix.setRotate((float) i2, ((float) decodeFile.getWidth()) / 2.0f, ((float) decodeFile.getHeight()) / 2.0f);
            return Bitmap.createBitmap(decodeFile, 0, 0, options.outWidth, options.outHeight, matrix, true);

        } catch (Exception e) {
            return null;
        }
    }

    public void rotateBitmap(Bitmap original, float degrees) {

        Bitmap res = original.copy(original.getConfig(), true);

        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);

        Bitmap rotatedBitmap = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
        res.recycle();

        res = rotatedBitmap;
        bm = res;
    }

    public void flipBitmap(Bitmap bms) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(bms.getWidth(), 0);

        matrix.postTranslate(0, bms.getHeight());
        bm = Bitmap.createBitmap(bms, 0, 0, bms.getWidth(), bms.getHeight(), matrix, true);
    }

    private String getTodayDate(boolean isToday) {

        Calendar calendar = Calendar.getInstance();
        if (!isToday)
            calendar.add(Calendar.DATE, -1);

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd,yyyy");

        return timeStampFormat.format(calendar.getTime());
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

    @Override
    public void onBackPressed() {

        new BackInterAds().showInterAds(this, new BackInterAds.OnAdClosedListener() {
            @Override
            public void onAdClosed() {

                finish();

                if (AllPhotosFragment.mAdapter != null && allimages != null)
                    AllPhotosFragment.mAdapter.adddata(allimages);

                if (AllPhotosFragment.mAdapter != null)
                    AllPhotosFragment.mAdapter.notifyDataSetChanged();

                if (FolderFragment.adapterDefault != null)
                    FolderFragment.adapterDefault.notifyDataSetChanged();

                if (FolderFragment.adapter != null)
                    FolderFragment.adapter.notifyDataSetChanged();
            }
        });


    }
}

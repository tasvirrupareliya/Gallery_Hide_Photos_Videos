package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimages;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimagesCopyWithoutDates;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.folderData;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.gallery.photos.lock.galleryhidephotosvideos.Adapter.AdapterMenu;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.LargeNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.AllPhotosFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.FolderFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.HideImageFoldersFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.CustomViewPager;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;

import com.gallery.photos.lock.galleryhidephotosvideos.MyApplication;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.gallery.photos.lock.galleryhidephotosvideos.slidingTab.SlidingFragmentPagerAdapter;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.preference.PowerPreference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {
    public static CustomViewPager viewPager;
    public static RelativeLayout relSelection;
    public static RelativeLayout relActionbarMain;
    public static LinearLayout llSelectedOptions;
    public static FrameLayout llHideOptions;
    public static TextView txtSelectedTitle;
    public static Activity activity;
    private final String[] titles = {
            "PHOTOS",
            "ALBUMS",
            "HIDDEN"
    };
    private ArrayList<String> searchlist = new ArrayList<>();

    private AdapterMenu adapterMenu;

    Uri photoURI;
    File filepath;

    public static LinearLayout llShare, llMore;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private HashMap<String, ArrayList<String>> folderDataCopy = new HashMap<>();
    ArrayList<String> allimgcopy = new ArrayList<>();
    public static ImageView imgUnlock;
    public static ImageView imgselectall;
    public static AnimatedBottomBar bottomBar;
    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        doTask();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        new InterAds().loadInterAds(MainActivity.this);
        new BackInterAds().loadInterAds(MainActivity.this);
        new LargeNativeAds().loadNativeAds(MainActivity.this, null);
        new MiniNativeAds().loadNativeAds(MainActivity.this, null);
    }

    /*@Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        activity = this;
        relativeLayout.setVisibility(View.VISIBLE);
        doTask();
    }*/

    public void doTask() {

        setdrawer();
        setview();

        Utils.getInstance().getHiddenImages(MainActivity.this, null, null);
    }

    @SuppressLint("SetTextI18n")
    private void setdrawer() {

        RecyclerView rvMenu = findViewById(R.id.rvMenu);

        DrawerLayout drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TextView txtPhotoCount = findViewById(R.id.txtPhotoCount);
        TextView txtVideoCount = findViewById(R.id.txtVideoCount);

        txtPhotoCount.setText(Utils.getInstance().getAllPhotosCount(this) + "");
        txtVideoCount.setText(Utils.getInstance().getAllVideoCount(this) + "");

        ArrayList<String> list = new ArrayList<>();
        list.add("Photos");
        list.add("Albums");
        list.add("Hide");
        list.add("Favourite");
        list.add("Recycle bin");
        list.add("Rate for us");
        list.add("Share");

        ArrayList<Integer> listIcon = new ArrayList<>();
        listIcon.add(R.drawable.ic_gallery);
        listIcon.add(R.drawable.ic_album);
        listIcon.add(R.drawable.ic_hide);
        listIcon.add(R.drawable.ic_favourite_gray);
        listIcon.add(R.drawable.ic_recyclebin);
        listIcon.add(R.drawable.ic_rate);
        listIcon.add(R.drawable.ic_share);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMenu.setLayoutManager(layoutManager);

        adapterMenu = new AdapterMenu(list, listIcon, pos -> {

            if (pos == 0)
                viewPager.setCurrentItem(0, true);
            else if (pos == 1)
                viewPager.setCurrentItem(1, true);
            else if (pos == 2)
                viewPager.setCurrentItem(2, true);
            else if (pos == 3) {
                new InterAds().showInterAds(MainActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(MainActivity.this, ViewFolderImagesActivity.class));
                    }
                });
            } else if (pos == 4) {

                new InterAds().showInterAds(MainActivity.this, new InterAds.OnAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(MainActivity.this, RecyclebinActivity.class));
                    }
                });
            } else if (pos == 5) {
                final String appName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (ActivityNotFoundException a) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }
            }
            else {
                try {
                    final String appLink = "\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent sendInt = new Intent(Intent.ACTION_SEND);
                    sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message) + appLink);
                    sendInt.setType("text/plain");
                    startActivity(Intent.createChooser(sendInt, "Share"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            drawer.closeDrawer(Gravity.LEFT);
        });

        rvMenu.setAdapter(adapterMenu);

        ImageView imgMenu = findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(v -> drawer.openDrawer(Gravity.LEFT));
    }

    /* private void showRateDialog(boolean isFrommenu) {
         final Dialog dialog = new Dialog(this, R.style.dialog);

         dialog.setContentView(R.layout.dialog_rate_us);
         dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

         if(isFrommenu)
         {
             dialog.findViewById(R.id.txtExit).setVisibility(View.GONE);
         }
         dialog.findViewById(R.id.txtExit).setOnClickListener(v -> {

             dialog.dismiss();

             if (!isFrommenu) {
                 Adhelper.showFullScreen(this, () -> {
                     Intent intent = new Intent(activity, LeavingAppActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     activity.startActivity(intent);
                     activity.finish();
                 });
             }

         });

         dialog.findViewById(R.id.txtRate).setOnClickListener(v -> {
             dialog.dismiss();
             startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
         });

         dialog.show();
     }
 */
    private void setview() {
        viewPager = findViewById(R.id.pager);
        bottomBar = findViewById(R.id.bottom_bar);

        txtTitle = findViewById(R.id.txtTitle);

        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgCamera = findViewById(R.id.imgCamera);
        ImageView imgSearch = findViewById(R.id.imgSearch);
        ImageView imgClose = findViewById(R.id.imgClose);
        llHideOptions = findViewById(R.id.llHideOptions);
        txtSelectedTitle = findViewById(R.id.txtSelectedTitle);
        llSelectedOptions = findViewById(R.id.llSelectedOptions);
        relSelection = findViewById(R.id.relSelection);
        relActionbarMain = findViewById(R.id.relActionbarMain);
        LinearLayout relSearch = findViewById(R.id.relSearch);

        imgUnlock = findViewById(R.id.imgUnlock);

        imgselectall = findViewById(R.id.imgselectall);

        EditText editSearch = findViewById(R.id.editSearch);


        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

       /* tabLayout.setTextSize(15);
        tabLayout.setAllCaps(false);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setTabType(TabType.TEXT_ICON);

        tabLayout.setViewPager(viewPager);*/


        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                viewPager.setCurrentItem(bottomBar.getSelectedIndex());
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

        editSearch.clearFocus();

        imgselectall.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 0) {
                AllPhotosFragment.mAdapter.selectDeselctAll(false);
            } else {
                FolderFragment.adapter.selectDeselctAll(false);
            }
        });

        imgBack.setOnClickListener(v -> onBackPressed());

        imgSearch.setOnClickListener(view -> {

            searchlist.clear();
            allimgcopy.clear();

            searchlist.addAll(allimages);
            allimgcopy.addAll(allimages);

            relSearch.setVisibility(View.VISIBLE);
            relSelection.setVisibility(View.GONE);
            relActionbarMain.setVisibility(View.GONE);

            editSearch.requestFocus();
        });

        imgClose.setOnClickListener(view -> {
            relSearch.setVisibility(View.GONE);
            relSelection.setVisibility(View.GONE);
            relActionbarMain.setVisibility(View.VISIBLE);

            searchlist.clear();
            searchlist.addAll(allimgcopy);
            try {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }

            editSearch.setText("");
            editSearch.clearFocus();
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                ArrayList<String> data = new ArrayList<>();

                ArrayList<String> datasearch = new ArrayList<>();
                datasearch.addAll(allimagesCopyWithoutDates);
                searchlist.clear();

                if (charSequence.toString().length() <= 0) {
                    data.addAll(allimgcopy);
                } else {
                    Collection<String> filtered = Collections2.filter(datasearch,
                            Predicates.containsPattern(charSequence.toString()));
                    data.addAll(filtered);
                }

                AllPhotosFragment.mAdapter.adddata(data);
                AllPhotosFragment.mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final PopupWindow[] infoPopup = new PopupWindow[1];

        ImageView imgMenumore = findViewById(R.id.imgMenumore);
        imgMenumore.setOnClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View popupView = layoutInflater.inflate(R.layout.popup_edit, null);

            infoPopup[0] = new PopupWindow(popupView, (int) getResources().getDimension(com.intuit.sdp.R.dimen._100sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
            infoPopup[0].setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            infoPopup[0].setOutsideTouchable(true);

            RelativeLayout relShortby = popupView.findViewById(R.id.relShortby);
            if (viewPager.getCurrentItem() == 0) {
                relShortby.setVisibility(View.GONE);
            } else if (viewPager.getCurrentItem() == 1) {
                relShortby.setVisibility(View.VISIBLE);
            }

            popupView.findViewById(R.id.txtEdit).setOnClickListener(v12 -> {
                infoPopup[0].dismiss();

                if (viewPager.getCurrentItem() == 0) {
                    AllPhotosFragment.mAdapter.enableEdit();
                } else {
                    FolderFragment.adapter.enableEdit();
                }
            });

            relShortby.setOnClickListener(v1 -> {
                infoPopup[0].dismiss();
                openShortbyPopup(imgMenumore);
            });

            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            infoPopup[0].showAtLocation(v, Gravity.TOP, size.x - v.getWidth(), (int) (v.getBottom() + getResources().getDimension(com.intuit.sdp.R.dimen._40sdp)));
        });

        imgCamera.setOnClickListener(view -> {
            photoURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", getpicturefile());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (infoPopup[0] != null) {
                    infoPopup[0].dismiss();
                }
            }

            @Override
            public void onPageSelected(int position) {
                adapterMenu.selectedpos = position;
                adapterMenu.notifyDataSetChanged();


                imgUnlock.setVisibility(View.GONE);

                imgCamera.setVisibility(View.VISIBLE);
                imgMenumore.setVisibility(View.VISIBLE);

                if (position == 0) {
                    imgSearch.setVisibility(View.VISIBLE);

                } else if (position == 1) {

                    imgSearch.setVisibility(View.GONE);
                } else {
                    imgSearch.setVisibility(View.GONE);
                    imgCamera.setVisibility(View.GONE);
                    imgMenumore.setVisibility(View.GONE);

                }

                bottomBar.selectTabAt(position, true);
                txtTitle.setText(titles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private File getpicturefile() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = timeStamp + ".png";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image;
        image = new File(storageDir, pictureFile);

        filepath = image;

//            image.createNewFile();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Utils.getInstance().showWarning(this, "Please grant camera permission to take picture");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            if (SplashActivity.isDataLoaded) {
                Utils.getInstance().addFiles(filepath.getAbsolutePath(), MainActivity.this);
                AllPhotosFragment.mAdapter.adddata(allimages);
                FolderFragment.adapter.notifyDataSetChanged();
                AllPhotosFragment.refresh();
                FolderFragment.refresh();

            } else {
                Utils.getInstance().showImageLoadingDialog(this, () -> {
                    Utils.getInstance().addFiles(filepath.getAbsolutePath(), MainActivity.this);

                    AllPhotosFragment.mAdapter.adddata(allimages);
                    FolderFragment.adapter.notifyDataSetChanged();
                    AllPhotosFragment.refresh();
                    FolderFragment.refresh();
                });
            }
        }
    }


    public static void setupPhotosEditOption() {

        LinearLayout llHide = activity.findViewById(R.id.llHide);
        LinearLayout llDelete = activity.findViewById(R.id.llDelete);
        LinearLayout llShare = activity.findViewById(R.id.llShare);
        LinearLayout llMore = activity.findViewById(R.id.llMore);

        llShare.setAlpha(1);
        llMore.setAlpha(1);

        ImageView imgShare = activity.findViewById(R.id.imgShare);
        ImageView imgMore = activity.findViewById(R.id.imgMore);

        TextView txtShare = activity.findViewById(R.id.txtShare);
        TextView txtMore = activity.findViewById(R.id.txtMore);

        imgShare.setImageResource(R.drawable.ic_share);
        imgMore.setImageResource(R.drawable.ic_menu_more);

        txtShare.setText("Share");
        txtMore.setText("More");

        llHide.setOnClickListener(v -> AllPhotosFragment.mAdapter.hidePhotos());
        llDelete.setOnClickListener(v -> AllPhotosFragment.mAdapter.deletePhotos());
        llShare.setOnClickListener(v -> AllPhotosFragment.mAdapter.sharePhotos());
        llMore.setOnClickListener(v -> AllPhotosFragment.mAdapter.moreOptionClicked(llMore, activity));
    }

    public static void setupFolderEditOption() {

        LinearLayout llHide = activity.findViewById(R.id.llHide);
        LinearLayout llDelete = activity.findViewById(R.id.llDelete);
        llShare = activity.findViewById(R.id.llShare);
        llMore = activity.findViewById(R.id.llMore);

        ImageView imgShare = activity.findViewById(R.id.imgShare);
        ImageView imgMore = activity.findViewById(R.id.imgMore);

        TextView txtShare = activity.findViewById(R.id.txtShare);
        TextView txtMore = activity.findViewById(R.id.txtMore);

        imgShare.setImageResource(R.drawable.ic_edit);
        imgMore.setImageResource(R.drawable.ic_details);
        llShare.setVisibility(View.GONE);
        llMore.setVisibility(View.GONE);

        txtShare.setText("Rename");
        txtMore.setText("Details");

        llHide.setOnClickListener(v -> FolderFragment.adapter.hidePhotos());

        llDelete.setOnClickListener(v -> FolderFragment.adapter.deletePhotos());

        llShare.setOnClickListener(v -> FolderFragment.adapter.renameFolder());

        llMore.setOnClickListener(v -> FolderFragment.adapter.details());

    }

    private void openShortbyPopup(View v) {

        folderDataCopy.clear();
        folderDataCopy.putAll(folderData);

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View popupView = layoutInflater.inflate(R.layout.popup_sort_by, null);

        PopupWindow infoPopup = new PopupWindow(popupView, (int) getResources().getDimension(com.intuit.sdp.R.dimen._100sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
        infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoPopup.setOutsideTouchable(true);

        TextView txtName = popupView.findViewById(R.id.txtName);
        TextView txtCount = popupView.findViewById(R.id.txtCount);
        TextView txtDate = popupView.findViewById(R.id.txtDate);
        TextView txtReverseAll = popupView.findViewById(R.id.txtReverseAll);
        TextView txtReset = popupView.findViewById(R.id.txtReset);

        txtName.setOnClickListener(view -> {
            infoPopup.dismiss();

            TreeMap<String, ArrayList<String>> sorted = new TreeMap<>();
            sorted.putAll(MyApplication.folderData);

            MyApplication.folderData.clear();
            MyApplication.folderData.putAll(sorted);

            FolderFragment.adapter.addData(folderData);
        });

        txtCount.setOnClickListener(view -> {
            infoPopup.dismiss();

            ValueComparator bvc = new ValueComparator(MyApplication.folderData);
            TreeMap<String, ArrayList<String>> sorted_map = new TreeMap<>(bvc);

            sorted_map.putAll(MyApplication.folderData);

            MyApplication.folderData.clear();
            MyApplication.folderData.putAll(sorted_map);

            FolderFragment.adapter.addData(folderData);
        });

        txtDate.setOnClickListener(view -> {
            infoPopup.dismiss();

            MyApplication.folderData.clear();
            MyApplication.folderData.putAll(folderDataCopy);

            FolderFragment.adapter.addData(folderData);
        });

        txtReverseAll.setOnClickListener(view -> {
            infoPopup.dismiss();

            if (MyApplication.isAsc) {
                MyApplication.isAsc = false;
                TreeMap<String, ArrayList<String>> tmap = new TreeMap<>(Collections.reverseOrder());
                tmap.putAll(MyApplication.folderData);

                MyApplication.folderData.clear();
                MyApplication.folderData.putAll(tmap);

            } else {
                MyApplication.isAsc = true;

                MyApplication.folderData.clear();
                MyApplication.folderData.putAll(folderDataCopy);
            }

            FolderFragment.adapter.addData(folderData);
        });

        txtReset.setOnClickListener(view -> {
            infoPopup.dismiss();

            MyApplication.isAsc = true;

            MyApplication.folderData.clear();
            MyApplication.folderData.putAll(folderDataCopy);

            FolderFragment.adapter.addData(folderData);
        });

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        infoPopup.showAtLocation(v, Gravity.TOP, size.x - v.getWidth(), v.getBottom() + 80);
    }

    class ValueComparator implements Comparator<String> {
        Map<String, ArrayList<String>> base;

        public ValueComparator(Map<String, ArrayList<String>> base) {
            this.base = base;
        }

        public int compare(String a, String b) {

            if (base.get(a).size() >= base.get(b).size()) {
                return -1;
            } else {
                return 1;
            }
        }
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
      //  new LargeNativeAds().showNativeAds(this, null);
    }

    @Override
    public void onBackPressed() {

        if (relSelection.getVisibility() == View.VISIBLE) {

            if (viewPager.getCurrentItem() == 0) {
                AllPhotosFragment.mAdapter.selectDeselctAll(true);
                AllPhotosFragment.mAdapter.isSelectionEnable = false;
                AllPhotosFragment.refresh();
            } else if (viewPager.getCurrentItem() == 1) {
                FolderFragment.adapter.selectDeselctAll(true);
                FolderFragment.isSelectionEnable = false;
                FolderFragment.refresh();
            } else {
                HideImageFoldersFragment.mAdapter.disableEdit(true);
                HideImageFoldersFragment.isSelectionEnable = false;
            }

            bottomBar.setVisibility(View.VISIBLE);
            relSelection.setVisibility(View.GONE);
            viewPager.setPagingEnabled(true);
            llHideOptions.setVisibility(View.GONE);
            llSelectedOptions.setVisibility(View.GONE);
            relActionbarMain.setVisibility(View.VISIBLE);

        } else {
            if (viewPager.getCurrentItem() == 1) {

                viewPager.setCurrentItem(0, true);
            } else if (viewPager.getCurrentItem() == 2) {

                HideImageFoldersFragment.mAdapter.onback();
            } else {

               exitdialog();
            }
        }
    }

    private void exitdialog() {
        try {

            Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_exit);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final AppCompatButton btnCancel = dialog.findViewById(R.id.btnCancel);
            final AppCompatButton btnRate = dialog.findViewById(R.id.btnRate);
            final AppCompatButton btnExit = dialog.findViewById(R.id.btnExit);

           /* dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    new LargeNativeAds().showNativeAds(MainActivity.this, dialog);
                }
            });*/

            dialog.show();

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            });

            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, 0);

                    if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleExitSplashInterOnOff, true)) {
                        new InterAds().showInterAds(MainActivity.this, new InterAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                Intent intent = new Intent(MainActivity.this, LeavingAppActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        Intent intent = new Intent(MainActivity.this, LeavingAppActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            btnRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.cancel();
                    Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    startActivity(viewIntent);
                }
            });
        } catch (Exception e) {
            Log.e("Catch", e.getMessage());
        }
    }

    public static class TabAdapter extends SlidingFragmentPagerAdapter {

        private final String[] titles = {
                "Photos",
                "Albums",
                "Hide"
        };

        private final int[] icons = {
                R.drawable.ic_photos,
                R.drawable.ic_album,
                R.drawable.ic_hide
        };

        private final Context context;

        public TabAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new AllPhotosFragment();
            } else if (position == 1) {
                return new FolderFragment();
            } else {
                return new HideImageFoldersFragment();
            }
        }

        @Override
        public int getCount() {
            return icons.length == titles.length ? icons.length : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Drawable getPageDrawable(int position) {
            return ResourcesCompat.getDrawable(context.getResources(), icons[position], null);
        }

        @NonNull
        @Override
        public String getToolbarTitle(int position) {
            return titles[position];
        }
    }

}
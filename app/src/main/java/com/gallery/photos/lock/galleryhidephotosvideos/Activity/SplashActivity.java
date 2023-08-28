package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.DefaultFolderData;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimages;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimagesCopyWithoutDates;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.folderData;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.window.SplashScreen;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.LargeNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.BuildConfig;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.FolderFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.MyApplication;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.gallery.photos.lock.galleryhidephotosvideos.databinding.ActivitySplashBinding;
import com.gallery.photos.lock.galleryhidephotosvideos.library.ViewAnimator.AnimationListener;
import com.gallery.photos.lock.galleryhidephotosvideos.library.ViewAnimator.ViewAnimator;
import com.preference.PowerPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

public class SplashActivity extends AppCompatActivity {

    public static DataLoad dataLoadListener;
    public static boolean isDataLoaded = false;
    public static boolean isFolderLoaded = false;
    public static int millis = 1;
    public static int versioncode = BuildConfig.VERSION_CODE;
    public static int count = 0;
    String flag;

    private static final int REQUEST_PERMISSIONS = 200;
    boolean check = false;

    float oldY;
    int VERSION = 0;

    ActivityResultLauncher<Intent> someActivityResultLauncher;
    ActivitySplashBinding binding;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PowerPreference.getDefaultFile().putBoolean(Constant.isNotify, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PowerPreference.getDefaultFile().putBoolean(Constant.isNotify, false);

        Log.e("test","oncreate");
        setLauncher();
        Updateapi();
        getdata();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }
    private void Updateapi() {
        Log.e("test","updateapi");
        if (checkPermission()) {
            RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);

            StringRequest request = new StringRequest(Request.Method.GET, BuildConfig.BaseURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject object = new JSONObject(response);

                        PowerPreference.getDefaultFile().putInt(Constant.SERVER_INTERVAL_COUNT, Integer.parseInt(object.getString("GoogleIntervalCount")));
                        PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, 0);

                        PowerPreference.getDefaultFile().putInt(Constant.SERVER_BACK_COUNT, Integer.parseInt(object.getString("GoogleBackInterIntervalCount")));
                        PowerPreference.getDefaultFile().putInt(Constant.APP_BACK_COUNT, 0);
                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleInterOnOff, Boolean.parseBoolean(object.getString("GoogleInterOnOff")));
                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleBackInterOnOff, Boolean.parseBoolean(object.getString("GoogleBackInterOnOff")));

                        PowerPreference.getDefaultFile().putString(Constant.PolicyLink, object.getString("PolicyLink"));

                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleExitSplashInterOnOff, Boolean.parseBoolean(object.getString("GoogleExitSplashInterOnOff")));
                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleMiniNativeOnOff, Boolean.parseBoolean(object.getString("GoogleMiniNativeOnOff")));
                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleLargeNativeOnOff, Boolean.parseBoolean(object.getString("GoogleLargeNativeOnOff")));
                        PowerPreference.getDefaultFile().putBoolean(Constant.NativeBackgroundColor, Boolean.parseBoolean(object.getString("NativeBackgroundColor")));

                        PowerPreference.getDefaultFile().putBoolean(Constant.ShowDialogBeforeAds, Boolean.parseBoolean(object.getString("ShowDialogBeforeAds")));
                        PowerPreference.getDefaultFile().putBoolean(Constant.DialogTimeInSec, Boolean.parseBoolean(object.getString("DialogTimeInSec")));

                        PowerPreference.getDefaultFile().putBoolean(Constant.AdsOnOff, Boolean.parseBoolean(object.getString("AdsOnOff")));
                        PowerPreference.getDefaultFile().putBoolean(Constant.FullScreenOnOff, Boolean.parseBoolean(object.getString("FullScreenOnOff")));

                        PowerPreference.getDefaultFile().putBoolean(Constant.GoogleNativeTextOnOff, Boolean.parseBoolean(object.getString("GoogleNativeTextOnOff")));
                        PowerPreference.getDefaultFile().putString(Constant.GoogleNativeText, object.getString("GoogleNativeText"));

                        PowerPreference.getDefaultFile().putString(Constant.INTERID, object.getString("GoogleInterAds"));
                        PowerPreference.getDefaultFile().putString(Constant.NATIVEID, object.getString("GoogleNativeAds"));
                        PowerPreference.getDefaultFile().putString(Constant.OPENAD, object.getString("GoogleAppOpenAds"));

                        flag = object.getString("flag");

                        Dialog dialogupdate = new Dialog(SplashActivity.this);
                        dialogupdate.setContentView(R.layout.dialog_update);
                        dialogupdate.setCancelable(false);

                        TextView btnSkip = (TextView) dialogupdate.findViewById(R.id.btnSkip);
                        TextView btnUpdate = (TextView) dialogupdate.findViewById(R.id.btnUpdate);

                        int version = Integer.parseInt(object.getString("version"));

                        if (flag.equals("NORMAL")) {
                            nextActivity();
                        } else if (flag.equals("SKIP")) {
                            if (version == versioncode) {
                                nextActivity();
                            } else {
                                dialogupdate.show();
                                btnUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        final String appName = getPackageName();
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                                        }
                                    }
                                });

                                btnSkip.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        nextActivity();
                                        dialogupdate.cancel();
                                    }
                                });
                            }

                        } else if (flag.equals("FORCE")) {
                            if (version == versioncode) {
                                nextActivity();
                            } else {
                                dialogupdate.show();
                                btnSkip.setVisibility(View.GONE);

                                btnUpdate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        final String appName = getPackageName();
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Dialog dialog = new Dialog(SplashActivity.this);
                    dialog.setContentView(R.layout.dialog_internet);
                    TextView retry = (TextView) dialog.findViewById(R.id.btnRetry);
                    dialog.setCancelable(false);
                    dialog.show();

                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (Constant.checkInternet(SplashActivity.this)) {
                                Updateapi();
                                dialog.cancel();
                            } else {
                                dialog.show();
                            }
                        }
                    });
                }
            });
            queue.add(request);
        } else {
            setPermission();
        }
    }

    public void setLauncher() {
        Log.e("test","setlauncher");
        if (someActivityResultLauncher == null) {
            someActivityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    Log.e("test","setlanuncher->versionRR");
                                    if (Environment.isExternalStorageManager()) {
                                        Log.e("test","setlanuncher->versionRR->storagemangerallow");
                                        Updateapi();
                                    } else {
                                        oKCancelDialog("You need to allow access to the permissions. Without this permission you can't access your storage. Are you sure deny this permission?",
                                                (dialog, which) -> {
                                                    check = true;
                                                    try {
                                                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                                        intent.addCategory("android.intent.category.DEFAULT");
                                                        intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                                                        someActivityResultLauncher.launch(intent);
                                                    } catch (Exception e) {
                                                        Intent intent = new Intent();
                                                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                                        someActivityResultLauncher.launch(intent);
                                                    }
                                                });
                                    }

                                } else {
                                    check = false;
                                    if (checkPermission()) {
                                        Log.e("test","setlanuncher->elsecheckpermission");
                                        Updateapi();
                                    } else {
                                        Log.e("test","setlanuncher->else->else_permissiondeny");
                                        setPermission();
                                    }
                                }
                            }
                        }
                    });
        }

    }

    private void getdata() {
        Log.e("test","getdata");
        Utils.getInstance().scanMedia(this);
        MyApplication.durations.clear();
        PowerPreference.getDefaultFile().putBoolean("isDataLoaded", false);
        PowerPreference.getDefaultFile().putBoolean("isFolderLoaded", false);
        PowerPreference.getDefaultFile().putBoolean("isDFolderLoaded", false);

        Utils.getInstance().getBinImages(this);
        new movetoHomeAndLoadData().execute();
        new Thread(() -> loadFolders()).start();
    }

    private void loadFolders() {
        Log.e("test","loadfolder");
        Cursor cursor = Utils.getInstance().getAllImagesVideoFromStorageForDate(SplashActivity.this);
        folderData.clear();

        if (cursor != null && cursor.moveToFirst()) {

            while (cursor.moveToNext()) {

                String path;
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

                while (cursor.moveToNext()) {

                    try {
                        path = cursor.getString(column_index_data);

                        if (!path.startsWith("/data") && new File(path).exists() && !path.endsWith("nomedia") && !path.endsWith(".gif")) {

                            ArrayList<String> datatemp = new ArrayList<>();
                            ArrayList<String> durtemp = new ArrayList<>();
                            String foldername = new File(path).getParentFile().getName();

                            if (!foldername.startsWith(".")) {
                                if (foldername.equalsIgnoreCase("Camera") || foldername.equalsIgnoreCase("Screenshots") ||
                                        foldername.equalsIgnoreCase("Videos") || foldername.equalsIgnoreCase("Downloads")) {
                                    if (DefaultFolderData.containsKey(foldername)) {
                                        datatemp.addAll(DefaultFolderData.get(foldername));
                                        datatemp.add(path);
                                    } else {
                                        datatemp.add(path);
                                    }
                                    if (cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                                        if (!MyApplication.durations.containsKey(path)) {
                                            MyApplication.durations.put(path, cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)));
                                        }
                                    }
                                    DefaultFolderData.put(foldername, datatemp);
                                } else {
                                    if (folderData.containsKey(foldername)) {
                                        datatemp.addAll(folderData.get(foldername));
                                        datatemp.add(path);

                                    } else {
                                        datatemp.add(path);
                                    }
                                    if (cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                                        if (!MyApplication.durations.containsKey(path)) {
                                            MyApplication.durations.put(path, cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)));
                                        }
                                    }
                                    folderData.put(foldername, datatemp);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TreeMap<String, ArrayList<String>> sorted = new TreeMap<>();
        sorted.putAll(MyApplication.folderData);

        DefaultFolderData.put(DefaultFolderData.size(), "Favourite", new ArrayList<>());

        MyApplication.folderData.clear();
        MyApplication.folderData.putAll(sorted);

        isFolderLoaded = true;

        runOnUiThread(() -> {
            if (FolderFragment.adapter != null)
                FolderFragment.adapter.addData(folderData);

            if (FolderFragment.adapterDefault != null)
                FolderFragment.adapterDefault.addData(DefaultFolderData);
        });
    }

    private class movetoHomeAndLoadData extends AsyncTask<Void, Void, Void> {

        Cursor cursor = null;

        @Override
        protected Void doInBackground(Void... voids) {

            cursor = Utils.getInstance().getAllImagesVideoFromStorageForDate(SplashActivity.this);

            allimages.clear();
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd,yyyy");

            isDataLoaded = false;
            String Date = "";
            int millis = 1;

            if (cursor != null && cursor.moveToFirst()) {

                cursor.moveToPosition(0);

                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date);

                if (calendar.get(Calendar.YEAR) == 1970) {
                    millis = 1000;
                }

                if (cursor.moveToFirst()) {
                    do {

                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));

                        if (!path.startsWith("/data") && new File(path).exists() && !path.endsWith(".nomedia") && !path.endsWith(".gif")) {

                            calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)) * millis);

                            String datestr = timeStampFormat.format(calendar.getTime());
                            if (!datestr.equals(Date)) {
                                allimages.add(datestr);
                                Date = datestr;
                            }

                            if (cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                                if (!MyApplication.durations.containsKey(path)) {
                                    MyApplication.durations.put(path, cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION)));
                                }
                            }

                            allimages.add(path);
                            allimagesCopyWithoutDates.add(path);

                            count++;
                        }

                    } while (cursor.moveToNext());

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            isDataLoaded = true;
            if (dataLoadListener != null)
                dataLoadListener.onDataLoaded();

        }
    }

    public interface DataLoad {
        void onDataLoaded();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        if (check) {
            Log.e("test","resume");
            check = false;
            if (checkPermission()) {
                Log.e("test","resume->checkpersmission");
                nextActivity();
            } else {
                Log.e("test","resume->checkpersmission->else set permission");
                setPermission();
            }
        }
    }

    public void nextActivity() {
        Log.e("test","nextactivity");

        binding.animationView.setVisibility(View.VISIBLE);
        //getdata();
      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
                Log.e("test","nextactivity->run");

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
           /* }
        }, 3500);*/
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(SplashActivity.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(SplashActivity.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void setPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            oKCancelDialog("You need to allow access to the permissions. Without this permission you can't access your storage.",
                    (dialog, which) -> {
                        check = true;
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                            someActivityResultLauncher.launch(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            someActivityResultLauncher.launch(intent);
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Log.e("test","onrequestpermission");
                    Updateapi();
                } else {
                    Log.e("test","onrequestpermission->else");

                    oKCancelDialog("You need to allow access to the permissions. Without this permission you can't access your storage. Are you sure deny this permission?",
                            (dialog, which) -> {
                                check = true;
                                try {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                                    someActivityResultLauncher.launch(intent);
                                } catch (Exception e) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    someActivityResultLauncher.launch(intent);
                                }
                            });
                }

            } else {
                Log.e("test","onrequestpermission else version R");

                if (grantResults.length > 0) {
                    boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeD = shouldShowRequestPermissionRationale(permissions[0]);

                    boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readD = shouldShowRequestPermissionRationale(permissions[1]);

                    if (write && read) {
                        Log.e("test","write read in onreuestpermission");

                        Updateapi();
                    } else if (!writeD || !readD) {
                        forcePermissionDialog("You need to allow access to the permissions. Without this permission you can't access your storage. Are you sure deny this permission?",
                                (dialog, which) -> {
                                    check = true;
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                });
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                oKCancelDialog("You need to allow access to the permissions",
                                        (dialog, which) -> {
                                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                    REQUEST_PERMISSIONS);
                                        });
                            }
                        }
                    }
                }
            }
        }
    }

    private void oKCancelDialog(String s, DialogInterface.OnClickListener o) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", o)
                .setCancelable(false)
                .create()
                .show();
    }

    private void forcePermissionDialog(String s, DialogInterface.OnClickListener aPackage) {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Permission Denied")
                .setMessage(s)
                .setPositiveButton("Give Permission", aPackage)
                .setNegativeButton("Deny Permission", null)
                .create()
                .show();
    }
}
package com.gallery.photos.lock.galleryhidephotosvideos;

import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.google.android.gms.ads.MobileAds;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.ArrayList;
import java.util.HashMap;

public class MyApplication extends android.app.Application {

    public static MyApplication application;

    public static ArrayList<String> dataArr = new ArrayList<>();
    public static HashMap<String, Long> durations = new HashMap<>();
    public static ListOrderedMap<String, ArrayList<String>> folderData = new ListOrderedMap<>();
    public static ListOrderedMap<String, ArrayList<String>> DefaultFolderData = new ListOrderedMap<>();

    public static boolean isAsc = true;
    public static boolean isReloadHidden = false;

    public static ArrayList<String> binImages = new ArrayList<>();
    public static ArrayList<String> allimages = new ArrayList<>();
    public static ArrayList<String> allimagesCopyWithoutDates = new ArrayList<>();

    public static boolean isEnteredPwd = false;

    public static ArrayList<String> HiddenImages = new ArrayList<>();
    public static ArrayList<String> HiddenImagesWithoutFolder = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> imageListWithDateOfFolder = new HashMap<>();

    static {
        System.loadLibrary("NativeImageProcessor");
       // System.loadLibrary("native-lib");
    }

    public static MyApplication getInstance() {
        if (application == null)
            application = new MyApplication();
        return application;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Utils.getInstance().initializeRenderScript(getApplicationContext());
    }
}
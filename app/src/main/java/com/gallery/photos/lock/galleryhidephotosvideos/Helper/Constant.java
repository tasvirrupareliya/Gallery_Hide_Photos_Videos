package com.gallery.photos.lock.galleryhidephotosvideos.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.gallery.photos.lock.galleryhidephotosvideos.Activity.LeavingAppActivity;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.LargeNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.BuildConfig;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.preference.PowerPreference;


public class Constant {

    public static String ADS_NORMAL = "ADS_NORMAL";
    public static String ADS_TINY = "ADS_TINY";

    public static String DEF_VALUE = "#1a0675FF";

    public static String adsLog = "adsLog";
    public static String errorLog = "errorLog";
    public static final String isNotify = "isNotify";
    public static final String PolicyLink = "PolicyLink";

    public static final String FullScreenOnOff = "FullScreenOnOff";
    public static final String AdsOnOff = "AdsOnOff";

    public static final String GoogleMiniNativeOnOff = "GoogleMiniNativeOnOff";
    public static final String GoogleLargeNativeOnOff = "GoogleLargeNativeOnOff";

    public static final String SERVER_INTERVAL_COUNT = "GoogleIntervalCount";
    public static final String APP_INTERVAL_COUNT = "APP_INTERVAL_COUNT";


    public static final String GoogleBackInterOnOff = "GoogleBackInterOnOff";
    public static final String GoogleInterOnOff = "GoogleInterOnOff";
    public static final String SERVER_BACK_COUNT = "GoogleBackInterIntervalCount";
    public static final String APP_BACK_COUNT = "APP_BACK_COUNT";
    public static final String GoogleExitSplashInterOnOff = "GoogleExitSplashInterOnOff";

    public static final String GoogleNativeTextOnOff = "GoogleNativeTextOnOff";
    public static final String GoogleNativeText = "GoogleNativeText";
    public static final String NativeTrans = "NativeTrans";
    public static final String NativeBackgroundColor = "NativeBackgroundColor";
    public static final String ShowDialogBeforeAds = "ShowDialogBeforeAds";
    public static final String DialogTimeInSec = "DialogTimeInSec";

    public static String OPENAD = "GoogleAppOpenAds";
    public static String INTERID = "GoogleInterAds";
    public static String NATIVEID = "GoogleNativeAds";

    public static void Log(String message) {
        Log.e("errorLog", message);
    }


    public static boolean checkInternet(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void showRateDialog(Activity activity, boolean isStart) {
        try {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.dialog_exit);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final AppCompatButton btnCancel = dialog.findViewById(R.id.btnCancel);
            final AppCompatButton btnRate = dialog.findViewById(R.id.btnRate);
            final AppCompatButton btnExit = dialog.findViewById(R.id.btnExit);

            if (!isStart)
                btnExit.setVisibility(View.GONE);

            btnRate.setOnClickListener(view -> {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                activity.startActivity(i);
            });

            btnExit.setOnClickListener(view -> {
                if (dialog != null) {
                    dialog.dismiss();
                }

               /* if (PowerPreference.getDefaultFile().getBoolean(Constant.GoogleBackInterOnOff)) {
                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, 0);
                    new InterAds().showInterAds(activity, new InterAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            Intent intent = new Intent(activity, LeavingAppActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    });
                } else {*/
                    Intent intent = new Intent(activity, LeavingAppActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                //}
            });

            btnCancel.setOnClickListener(view -> {
                if (dialog != null) {
                    dialog.dismiss();
                }
            });

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    new LargeNativeAds().showNativeAds(activity, dialog);
                }
            });

            dialog.show();


        } catch (Exception e) {
            Log.e("Catch", e.getMessage());
        }

    }


}

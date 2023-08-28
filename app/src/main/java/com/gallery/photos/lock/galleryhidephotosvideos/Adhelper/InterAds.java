package com.gallery.photos.lock.galleryhidephotosvideos.Adhelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.preference.PowerPreference;

import java.util.Objects;

public class InterAds {

    @SuppressLint("StaticFieldLeak")
    public static Activity mActivity;

    public static InterstitialAd mInterstitialAd;
    public static OnAdClosedListener mOnAdClosedListener;

    public Dialog mLoadingDialog;

    protected void ShowProgress(Activity activity) {
        if (mLoadingDialog == null && !activity.isFinishing()) {
            mLoadingDialog = showScreenDataLoader(activity);
        }
        if (!activity.isFinishing() && mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog.show();
    }

    public static Dialog showScreenDataLoader(Activity mActivity) {
        Dialog d = new Dialog(mActivity);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(d.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        d.setContentView(R.layout.custom_dialog_loader);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        d.setCancelable(false);
        d.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = -2;
        lp.height = -2;
        d.show();
        d.getWindow().setAttributes(lp);

        return d;
    }

    protected void HideProgress(Activity activity) {
        if (!activity.isFinishing() && mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    public interface OnAdClosedListener {
        public void onAdClosed();
    }

    public void loadInterAds(Activity activity) {
        final String interAd = PowerPreference.getDefaultFile().getString(Constant.INTERID, "123");
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, interAd, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
                Constant.Log("onAdLoaded");

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Constant.Log("onAdClosed");
                        loadInterAds(activity);

                        if (mOnAdClosedListener != null) {
                            mOnAdClosedListener.onAdClosed();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        Constant.Log("The ad failed to show.");
                        mInterstitialAd = null;
                        loadInterAds(activity);

                            if (mOnAdClosedListener != null)
                                mOnAdClosedListener.onAdClosed();

                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd = null;
                        Constant.Log("The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
                Constant.Log("onAdFailedToLoad");
            }
        });

    }

    public void showInterAds(Activity context, OnAdClosedListener onAdClosedListener) {
        Constant.Log("showInterAds");
        mActivity = context;
        mOnAdClosedListener = onAdClosedListener;

        if (PowerPreference.getDefaultFile().getBoolean(Constant.AdsOnOff, true)) {

            int custGCount = PowerPreference.getDefaultFile().getInt(Constant.SERVER_INTERVAL_COUNT);
            int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);

            if (custGCount != 0 && appGCount % custGCount == 0) {
                watchAds(context, onAdClosedListener);
            } else {
                appGCount++;
                PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);
                if (mOnAdClosedListener != null)
                    mOnAdClosedListener.onAdClosed();
            }
        } else {
            if (mOnAdClosedListener != null)
                mOnAdClosedListener.onAdClosed();
        }
    }

    public void watchAds(Activity context, OnAdClosedListener onAdClosedListener) {
        mActivity = context;
        mOnAdClosedListener = onAdClosedListener;
        if (mInterstitialAd != null) {

            if (PowerPreference.getDefaultFile().getBoolean(Constant.ShowDialogBeforeAds, true)) {
                ShowProgress(context);
                new Handler().postDelayed(() -> {

                    HideProgress(context);
                    int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);

                    appGCount++;
                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);
                    mInterstitialAd.show(context);

                }, (long) (PowerPreference.getDefaultFile().getDouble(Constant.DialogTimeInSec, 1) * 1000L));

            } else {
                int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);

                appGCount++;
                PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);
                mInterstitialAd.show(context);
            }


        } else {
            loadInterAds(context);
                if (PowerPreference.getDefaultFile().getBoolean(Constant.ShowDialogBeforeAds, true)) {
                    ShowProgress(context);
                    new Handler().postDelayed(() -> {

                        HideProgress(context);
                        int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);

                        appGCount++;
                        PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);

                    }, (long) (PowerPreference.getDefaultFile().getDouble(Constant.DialogTimeInSec, 1) * 1000L));

                } else {
                    int appGCount = PowerPreference.getDefaultFile().getInt(Constant.APP_INTERVAL_COUNT);

                    appGCount++;
                    PowerPreference.getDefaultFile().putInt(Constant.APP_INTERVAL_COUNT, appGCount);
                }
        }
    }
}

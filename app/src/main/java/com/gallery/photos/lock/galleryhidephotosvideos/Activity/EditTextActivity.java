package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gallery.photos.lock.galleryhidephotosvideos.Adapter.BgColorAdaptor;
import com.gallery.photos.lock.galleryhidephotosvideos.Adapter.ColorAdaptor;
import com.gallery.photos.lock.galleryhidephotosvideos.Adapter.FontAdaptor;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.onClickRecycle;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.gallery.photos.lock.galleryhidephotosvideos.databinding.ScreenEditTextBinding;
import com.gallery.photos.lock.galleryhidephotosvideos.sticker.DrawableSticker;
import com.gallery.photos.lock.galleryhidephotosvideos.sticker.Sticker;
import com.preference.PowerPreference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditTextActivity extends AppCompatActivity {

    private int shadowProgress = 15;
    private int shadowColor = 0;
    private ScreenEditTextBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ScreenEditTextBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Utils.getInstance().colorStatusBar(this);

        //getId();

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFinalText();
            }
        });

        binding.linFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMenu(binding.linFont);
            }
        });

        binding.linColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMenu(binding.linColor);
            }
        });

        binding.linBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMenu(binding.linBackground);
            }
        });

        binding.linShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectMenu(binding.linShadow);
            }
        });
    }

   /* private void getId() {

        SelectMenu(binding.linFont);
        getFont();
        getTextColor();
        getBackgroundColor();
        getShadowColor();

    }*/

    /*public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.imgBack:
                onBackPressed();
                break;

            case R.id.btnDone:
                getFinalText();
                break;

            case R.id.linFont:
                SelectMenu(binding.linFont);
                break;

            case R.id.linColor:
                SelectMenu(binding.linColor);
                break;

            case R.id.linBackground:
                SelectMenu(binding.linBackground);
                break;

            case R.id.linShadow:
                SelectMenu(binding.linShadow);
                break;
        }
    }*/

    private void SelectMenu(View view) {
        binding.recycleFont.setVisibility(View.GONE);
        binding.recycleColor.setVisibility(View.GONE);
        binding.recycleBackground.setVisibility(View.GONE);
        binding.layShadow.setVisibility(View.GONE);

        binding.LLEdit.getChildAt(binding.LLEdit.indexOfChild(binding.linFont)).setActivated(false);
        binding.LLEdit.getChildAt(binding.LLEdit.indexOfChild(binding.linColor)).setActivated(false);
        binding.LLEdit.getChildAt(binding.LLEdit.indexOfChild(binding.linBackground)).setActivated(false);
        binding.LLEdit.getChildAt(binding.LLEdit.indexOfChild(binding.linShadow)).setActivated(false);
        binding.LLEdit.getChildAt(binding.LLEdit.indexOfChild(view)).setActivated(true);

        if (view == findViewById(R.id.linFont)) {
            binding.recycleFont.setVisibility(View.VISIBLE);
        } else if (view == findViewById(R.id.linColor)) {
            binding.recycleColor.setVisibility(View.VISIBLE);
        } else if (view == findViewById(R.id.linBackground)) {
            binding.recycleBackground.setVisibility(View.VISIBLE);
        } else if (view == findViewById(R.id.linShadow)) {
            binding.layShadow.setVisibility(View.VISIBLE);
        }
    }

    private void getFont() {
        List<String> listfont = new ArrayList<>();
        try {
            String[] list = getAssets().list("font");
            for (String str : list) {
                String stringBuilder = "font" + File.separator + str;
                listfont.add(stringBuilder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.recycleFont.setHasFixedSize(true);
        binding.recycleFont.setLayoutManager(new GridLayoutManager(this, 4));
        binding.recycleFont.setAdapter(new FontAdaptor(this, listfont));
        binding.recycleFont.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> binding.edText.setTypeface(Typeface.createFromAsset(getAssets(), listfont.get(position))))
        );
    }

    private void getTextColor() {
        int[] listTextColor;

        TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.arrayTextColor);
        listTextColor = new int[obtainTypedArray.length()];
        for (int i = 0; i < obtainTypedArray.length(); i++) {
            listTextColor[i] = obtainTypedArray.getColor(i, 0);
        }

        binding.recycleColor.setHasFixedSize(true);
        binding.recycleColor.setLayoutManager(new GridLayoutManager(this, 5));
        binding.recycleColor.setAdapter(new ColorAdaptor(this, listTextColor));

        binding.recycleColor.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> {
                    binding.edText.setTextColor(listTextColor[position]);
                    binding.edText.setHintTextColor(listTextColor[position]);
                })
        );
    }

    private void getBackgroundColor() {
        int[] listBackgroundColor;
        TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.arrayBackgroundColor);
        listBackgroundColor = new int[obtainTypedArray.length()];
        for (int i = 0; i < obtainTypedArray.length(); i++) {
            listBackgroundColor[i] = obtainTypedArray.getColor(i, 0);
        }

        binding.recycleBackground.setHasFixedSize(true);
        binding.recycleBackground.setLayoutManager(new GridLayoutManager(this, 5));
        binding.recycleBackground.setAdapter(new BgColorAdaptor(this, listBackgroundColor));

        binding.recycleBackground.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> binding.edText.setBackgroundColor(listBackgroundColor[position]))
        );
    }

    private void getShadowColor() {
        int[] listShadowColor;
        TypedArray obtainTypedArray = getResources().obtainTypedArray(R.array.arrayShadowColor);
        listShadowColor = new int[obtainTypedArray.length()];
        for (int i = 0; i < obtainTypedArray.length(); i++) {
            listShadowColor[i] = obtainTypedArray.getColor(i, 0);
        }

        binding.seekOpacity.setProgress(15);
        binding.seekOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.w("progress", String.valueOf(progress));
                shadowProgress = progress;
                binding.edText.setShadowLayer(shadowProgress, -1.0f, 1.0f, listShadowColor[shadowColor]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.recycleShadowColor.setHasFixedSize(true);
        binding.recycleShadowColor.setLayoutManager(new GridLayoutManager(this, 5));
        binding.recycleShadowColor.setAdapter(new ColorAdaptor(this, listShadowColor));

        binding.recycleShadowColor.addOnItemTouchListener(
                new onClickRecycle(this, (view, position) -> {
                    shadowColor = position;
                    binding.edText.setShadowLayer(shadowProgress, -1.0f, 1.0f, listShadowColor[position]);
                })
        );
    }

    private void getFinalText() {
        if (binding.edText.getText().toString().length() == 0) {
            Utils.getInstance().showWarning(this, "Please Enter Some Text");
        } else {
            binding.edText.setCursorVisible(false);
            Bitmap bitmapSS = takeScreenshot(binding.linSS);

            Drawable d = new BitmapDrawable(getResources(), bitmapSS);
            EditImageActivity.stickerView.addSticker(new DrawableSticker(d), Sticker.Position.CENTER);

            finish();
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

    public Bitmap takeScreenshot(View view) {
        Bitmap createBitmap = null;
        try {
            createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createBitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PowerPreference.getDefaultFile().getBoolean(Constant.FullScreenOnOff, true)) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        new MiniNativeAds().showNativeAds(this,null);
    }
}

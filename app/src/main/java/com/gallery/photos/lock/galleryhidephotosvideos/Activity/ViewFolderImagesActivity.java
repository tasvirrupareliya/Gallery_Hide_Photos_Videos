package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.DefaultFolderData;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.folderData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.FolderFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Database;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.GridRecyclerView;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.MyApplication;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.gallery.photos.lock.galleryhidephotosvideos.library.ViewAnimator.ViewAnimator;
import com.gallery.photos.lock.galleryhidephotosvideos.stickyheader.StickyHeaders;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Database;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.GridRecyclerView;
import com.gallery.photos.lock.galleryhidephotosvideos.MyApplication;
import com.preference.PowerPreference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ViewFolderImagesActivity extends AppCompatActivity {

    private ImageView imgSelectAll;
    private LinearLayout llSelectedOptions;
    private AllPhotosAdapter mAdapter;
    private LottieAnimationView loader;
    private GridRecyclerView listPhotos;
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<String> dataWithoutDates = new ArrayList<>();

    private String path = null;
    private TextView txttitle;
    private ArrayList<String> selectedItems = new ArrayList<>();

    Dialog mLoadingDialog;

    private LinearLayout llNoDataFound;

    public static ViewFolderImagesActivity activity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_folder_images);
        activity = this;
        //  getData();

        setview();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    public void dismissLoader() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    private void showLoader(Activity mActivity) {

        try {
            mLoadingDialog = new Dialog(mActivity);
            mLoadingDialog.setContentView(R.layout.custom_dialog_loader);
            mLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            mLoadingDialog.show();
            TextView textView = mLoadingDialog.findViewById(R.id.txt_Msg);
            textView.setText("Delete in Progress");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getData() {

        data.clear();
        dataWithoutDates.clear();

        if (getIntent().hasExtra("name")) {
            path = getIntent().getStringExtra("name");

            String foldername = new File(path).getName();

            if (folderData.containsKey(foldername)) {
                data.addAll(folderData.get(foldername));
            } else if (DefaultFolderData.containsKey(foldername)) {
                data.addAll(DefaultFolderData.get(foldername));
            }
        } else {
            data.addAll(Database.getInstance(this).getFavouriteList());
            dataWithoutDates.addAll(Database.getInstance(this).getFavouriteListWithoutDate());
        }

    }

    private void setview() {
        if (getIntent().hasExtra("name")) {
            path = getIntent().getStringExtra("name");
        }
        txttitle = findViewById(R.id.txttitle);
        llNoDataFound = findViewById(R.id.llNoDataFound);


        if (path == null)
            txttitle.setText("Favourite");
        else
            txttitle.setText(new File(path).getName());

        findViewById(R.id.imgBack).setOnClickListener(v -> onBackPressed());

        imgSelectAll = findViewById(R.id.imgSelectAll);
        imgSelectAll.setOnClickListener(v -> mAdapter.selectDeselctAll(false));

        llSelectedOptions = findViewById(R.id.llSelectedOptions);

        listPhotos = findViewById(R.id.listPhotos);
        mAdapter = new AllPhotosAdapter(this);
        loader = findViewById(R.id.loader);

        if (data.size() > 0) {
            llNoDataFound.setVisibility(View.GONE);
            listPhotos.setVisibility(View.VISIBLE);
        } else {
            llNoDataFound.setVisibility(View.VISIBLE);
            listPhotos.setVisibility(View.GONE);
        }


        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.isStickyHeader(position)) {
                    return 3;
                }
                return 1;
            }
        });


     /*   StickyHeadersGridLayoutManager<AllPhotosAdapter> layoutManager = new StickyHeadersGridLayoutManager<>(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                if (mAdapter.isStickyHeader(position)) {
                    return 3;
                }
                return 1;
            }
        });*/

        listPhotos.setLayoutManager(manager);
        listPhotos.setAdapter(mAdapter);

        LinearLayout llHide = findViewById(R.id.llHide);
        LinearLayout llDelete = findViewById(R.id.llDelete);
        LinearLayout llShare = findViewById(R.id.llShare);
        LinearLayout llMore = findViewById(R.id.llMore);

        llHide.setOnClickListener(v -> mAdapter.hidePhotos());
        llDelete.setOnClickListener(v -> mAdapter.deletePhotos());
        llShare.setOnClickListener(v -> mAdapter.sharePhotos());
        llMore.setOnClickListener(v -> mAdapter.moreOptionClicked(llMore, this));
    }

    @Override
    public void onBackPressed() {

        if (imgSelectAll.getVisibility() == View.VISIBLE) {

            mAdapter.selectDeselctAll(true);
            mAdapter.isSelectionEnable = false;

            imgSelectAll.setVisibility(View.GONE);
            llSelectedOptions.setVisibility(View.GONE);

            if (path == null)
                txttitle.setText("Favourite");
            else
                txttitle.setText(new File(path).getName());
        } else {
            new BackInterAds().showInterAds(this, new BackInterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    finish();
                }
            });
        }
    }

    public class AllPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaders, StickyHeaders.ViewSetup {

        HashMap<Integer, Integer> params = new HashMap<>();

        long DURATION = 100;
        private boolean on_attach = true;

        private static final int HEADER_ITEM = 123;
        private static final int SUB_ITEM = 124;
        private final Activity context;
        private String TodayDate;
        private String YesterdayDate;

        public boolean isSelectionEnable = false;

        public AllPhotosAdapter(Activity context) {
            this.context = context;
            TodayDate = getTodayDate(true);
            YesterdayDate = getTodayDate(false);
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    Log.d("TAG", "onScrollStateChanged: Called " + newState);
                    on_attach = false;
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            super.onAttachedToRecyclerView(recyclerView);
        }

        private void setAnimation(View itemView, int i) {
            if (!params.containsValue(i)) {

                params.put(i, i);

                if (!on_attach) {
                    i = -1;
                }
                boolean isNotFirstItem = i == -1;
                i++;

                itemView.setScaleX(0.f);
                itemView.setScaleY(0.f);

                ViewAnimator.animate(itemView)
                        .scale(0.f, 1.0f)
                        .startDelay(isNotFirstItem ? DURATION / 2 : (i * DURATION / 3))
                        .duration(100)
                        .start();
            }
        }


        private String getTodayDate(boolean isToday) {

            Calendar calendar = Calendar.getInstance();
            if (!isToday)
                calendar.add(Calendar.DATE, -1);

            SimpleDateFormat timeStampFormat = new SimpleDateFormat("MMM dd,yyyy");

            return timeStampFormat.format(calendar.getTime());
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == HEADER_ITEM) {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
                return new HeaderViewHolder(inflate);
            } else {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos, parent, false);
                return new MyViewHolder(inflate);
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            if (holder instanceof HeaderViewHolder) {
                if (data.get(position).equals(TodayDate)) {
                    ((HeaderViewHolder) holder).textHeader.setText("Today");
                } else if (data.get(position).equals(YesterdayDate)) {
                    ((HeaderViewHolder) holder).textHeader.setText("Yesterday");
                } else {
                    try {
                        String dates = data.get(position);
                        SimpleDateFormat spf = new SimpleDateFormat("MMM dd,yyyy");
                        Date newDate = spf.parse(dates);
                        spf = new SimpleDateFormat("dd MMMM yyyy");
                        dates = spf.format(newDate);
                        ((HeaderViewHolder) holder).textHeader.setText(dates);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (holder instanceof MyViewHolder) {
                setAnimation(holder.itemView, holder.getAdapterPosition());

                Glide.with(context).load(R.drawable.img_placeholder).into(((MyViewHolder) holder).imgThumbnail);

                boolean isImage = Utils.getInstance().isImageType(data.get(position), context);

                if (isImage) {
                    ((MyViewHolder) holder).llVideo.setVisibility(View.GONE);
                } else {
                    ((MyViewHolder) holder).llVideo.setVisibility(View.VISIBLE);
                /*    if (Application.durations.containsKey(data.get(position))) {

                        ((MyViewHolder) holder).txtVideoTime.setText("" + Utils.convertMillieToHMmSs(Application.durations.get(data.get(position))));
                    } else
                        ((MyViewHolder) holder).txtVideoTime.setText("" + Utils.getInstance().getvideoDurationForhidden(context, data.get(position)));
             */
                }

                Glide.with(context).load(data.get(position)).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        ((MyViewHolder) holder).imgThumbnail.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ((MyViewHolder) holder).imgThumbnail.setVisibility(View.GONE);
                        return false;
                    }

                }).into(((MyViewHolder) holder).imgthumb);


                if (isSelectionEnable) {
                    ((MyViewHolder) holder).imgSelection.setVisibility(View.VISIBLE);

                    if (selectedItems.contains(data.get(position))) {
                        ((MyViewHolder) holder).imgSelection.setImageResource(R.drawable.ic_check);
                    } else {
                        ((MyViewHolder) holder).imgSelection.setImageResource(R.drawable.ic_allselecticon);
                    }
                } else {
                    ((MyViewHolder) holder).imgSelection.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(v -> {
                    if (isSelectionEnable) {

                        if (selectedItems.contains(data.get(position))) {
                            selectedItems.remove(data.get(position));
                        } else {
                            selectedItems.add(data.get(position));
                        }

                        notifyItemChanged(position);
                        txttitle.setText(selectedItems.size() + " Selected");

                    } else {

                        if (getIntent().hasExtra("name")) {
                            MyApplication.dataArr.clear();
                            MyApplication.dataArr.addAll(data);

                            new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                                @Override
                                public void onAdClosed() {
                                    context.startActivity(new Intent(context, ViewImageActivity.class)
                                            .putExtra("position", position).
                                                    putExtra("path", data.get(position)).
                                                    putExtra("type", 3));
                                }
                            });

                        } else {
                            MyApplication.dataArr.clear();
                            MyApplication.dataArr.addAll(dataWithoutDates);
                            new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                                @Override
                                public void onAdClosed() {
                                    context.startActivity(new Intent(context, ViewImageActivity.class)
                                            .putExtra("position", position)
                                            .putExtra("path", data.get(position)).
                                                    putExtra("type", 3));
                                }
                            });
                        }

                    }
                });

                holder.itemView.setOnLongClickListener(v -> {

                    if (!isSelectionEnable) {
                        enableEdit();

                        selectedItems.add(data.get(position));
                        notifyDataSetChanged();
                        txttitle.setText(selectedItems.size() + " Selected");
                    }

                    return false;
                });
            }
        }

        public void enableEdit() {

            selectedItems.clear();
            isSelectionEnable = true;

            txttitle.setText(selectedItems.size() + " Selected");

            imgSelectAll.setVisibility(View.VISIBLE);
            llSelectedOptions.setVisibility(View.VISIBLE);

            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (data.get(position).contains("storage")) {
                return SUB_ITEM;
            } else {
                return HEADER_ITEM;
            }
        }

        @Override
        public boolean isStickyHeader(int position) {
            return getItemViewType(position) == HEADER_ITEM;
        }

        @Override
        public void setupStickyHeaderView(View stickyHeader) {
            ViewCompat.setElevation(stickyHeader, 10);
        }

        @Override
        public void teardownStickyHeaderView(View stickyHeader) {
            ViewCompat.setElevation(stickyHeader, 0);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (isStickyHeader(holder.getLayoutPosition())) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                    p.setFullSpan(true);
                }
            }
        }

        public void selectDeselctAll(boolean isBack) {
            if (isBack || selectedItems.size() == data.size()) {
                selectedItems.clear();
            } else {
                selectedItems.clear();
                selectedItems.addAll(data);
            }

            txttitle.setText(selectedItems.size() + " Selected");

            notifyDataSetChanged();
        }

        public void hidePhotos() {
            if (selectedItems.size() > 0) {

                boolean isFromFav = !getIntent().hasExtra("name");

                Utils.getInstance().hidePhotos(selectedItems, context, false, () -> {

                    for (int i = 0; i < selectedItems.size(); i++) {

                        if (isFromFav) {
                            Database.getInstance(ViewFolderImagesActivity.this).addToFavourite(selectedItems.get(i), false);
                        }

                        int index = data.indexOf(selectedItems.get(i));
                        data.remove(index);

                        if (index - 1 >= 0 && !data.get(index - 1).contains("storage")) {
                            data.remove(index - 1);
                        }
                    }

                    if (data.size() > 0) {
                        llNoDataFound.setVisibility(View.GONE);
                        listPhotos.setVisibility(View.VISIBLE);
                    } else {
                        llNoDataFound.setVisibility(View.VISIBLE);
                        listPhotos.setVisibility(View.GONE);
                    }

                    FolderFragment.adapterDefault.notifyDataSetChanged();

                    disableEdit();
                });
            } else {

                Utils.getInstance().showWarning(context, "Please select photos");
            }
        }

        public void deletePhotos() {

            if (SplashActivity.isDataLoaded) {
                if (selectedItems.size() > 0) {

                    boolean isFromFav = !getIntent().hasExtra("name");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete this file?");
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        showLoader(context);
                        dialog.dismiss();

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                for (String path : selectedItems) {

                                    Utils.getInstance().recycleImage(path, context);
                                    Utils.getInstance().removeImage(path, context);

                                    if (isFromFav) {
                                        Database.getInstance(ViewFolderImagesActivity.this).addToFavourite(path, false);
                                    }


                                    int prev, post;

                                    int index = data.indexOf(path);
                                    data.remove(index);

                                    if (index - 1 >= 0 && !data.get(index - 1).contains("storage")) {
                                        data.remove(index - 1);
                                    }

                                 /*   if (data.size() <= 1) {
                                        data.clear();
                                    } else {

                                        prev = index - 1;
                                        post = index + 1;

                                        if (prev > 0) {
                                            if (post > data.size() - 1) {
                                                if (!data.get(prev).contains("storage")) {
                                                    data.remove(prev);
                                                }
                                            } else {
                                                if (!data.get(prev).contains("storage") && !data.get(post).contains("storage")) {
                                                    data.remove(prev);
                                                }
                                            }

                                        } else if (prev == 0) {
                                            if (!data.get(post).contains("storage")) {
                                                data.remove(0);
                                            }
                                        }
                                        data.remove(path);
                                    }

                                    if (data.size() == 1)
                                        data.clear();
                                    */
                                }

                                Utils.getInstance().showSuccess(context, selectedItems.size() + " items deleted.");

                                selectedItems.clear();

                                if (data.size() > 0) {
                                    llNoDataFound.setVisibility(View.GONE);
                                    listPhotos.setVisibility(View.VISIBLE);
                                } else {
                                    llNoDataFound.setVisibility(View.VISIBLE);
                                    listPhotos.setVisibility(View.GONE);
                                }

                                disableEdit();
                                dismissLoader();

                                FolderFragment.adapterDefault.notifyDataSetChanged();
                            }
                        }, 1000);

                    });

                    builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    Utils.getInstance().showWarning(context, "Please select photos");
                }
            } else {
                Utils.getInstance().showImageLoadingDialog(context, () -> deletePhotos());
            }
        }

        public void sharePhotos() {
            if (selectedItems.size() > 0) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

                ArrayList<Uri> files = new ArrayList<>();

                for (String path : selectedItems) {
                    files.add(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(path)));
                }

                shareIntent.setType("image/*");
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

                context.startActivity(Intent.createChooser(shareIntent, "Share Images Using"));

                disableEdit();
            } else {

                Utils.getInstance().showWarning(context, "Please select photos");
            }
        }

        private void disableEdit() {
            selectedItems.clear();

            notifyDataSetChanged();
            onBackPressed();
        }

        public void moreOptionClicked(View v, Activity activity) {
            if (selectedItems.size() > 0) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View popupView = layoutInflater.inflate(R.layout.popup_more_options, null);

                PopupWindow infoPopup;
                infoPopup = new PopupWindow(popupView, (int) getResources().getDimension(com.intuit.sdp.R.dimen._130sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
                infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                infoPopup.setOutsideTouchable(true);

                TextView txtFavourite = popupView.findViewById(R.id.txtFavourite);
                TextView txtAddtoAlbum = popupView.findViewById(R.id.txtAddtoAlbum);
                TextView txtNewAlbum = popupView.findViewById(R.id.txtNewAlbum);

                if (path == null) {
                    txtFavourite.setText("unfavourite");
                }

                txtFavourite.setOnClickListener(v1 -> {

                    infoPopup.dismiss();

                    Database db = new Database(context);
                    for (int i = 0; i < selectedItems.size(); i++) {
                        if (path == null) {
                            db.addToFavourite(selectedItems.get(i), false);

                            int index = data.indexOf(selectedItems.get(i));

                            data.remove(selectedItems.get(i));
                            try {

                                if (data.size() > index + 1) {
                                    if (index - 1 >= 0 && !data.get(index - 1).contains("storage") && !data.get(index + 1).contains("storage")) {
                                        data.remove(index - 1);
                                    }
                                } else {
                                    if (index - 1 >= 0 && !data.get(index - 1).contains("storage")) {
                                        data.remove(index - 1);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else
                            db.addToFavourite(selectedItems.get(i), true);
                    }

                    if (path != null) {
                        Utils.getInstance().showSuccess(context, "Added to favourite Successfully");
                    } else {
                        Utils.getInstance().showConfusing(context, "Removed from favourite Successfully");
                    }

                    if (data.size() == 1) {
                        if (!data.contains("storage")) {
                            data.clear();
                        }
                    }

                    disableEdit();

                    notifyDataSetChanged();
                    FolderFragment.adapterDefault.notifyDataSetChanged();

                    if (data.size() > 0) {
                        llNoDataFound.setVisibility(View.GONE);
                        listPhotos.setVisibility(View.VISIBLE);
                    } else {
                        llNoDataFound.setVisibility(View.VISIBLE);
                        listPhotos.setVisibility(View.GONE);
                    }

                });

                txtAddtoAlbum.setOnClickListener(v15 -> {
                    infoPopup.dismiss();
                    new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            context.startActivity(new Intent(context, AddtoAlbumActivity.class).putExtra("selected", selectedItems).putExtra("isFromView", true));

                        }
                    });
                });

                txtNewAlbum.setOnClickListener(v14 -> {
                    infoPopup.dismiss();
                    Utils.getInstance().createAlbumPopup(context, name -> {

                        File file = new File(Utils.getInstance().getImageSaveDirectory(), name);
                        file.mkdirs();

                        final Dialog dialog = new Dialog(context, R.style.dialog);
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);

                        dialog.setContentView(R.layout.dialog_confirm_copy);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        TextView txtOk = dialog.findViewById(R.id.txtOk);
                        TextView txtCancel = dialog.findViewById(R.id.txtCancel);
                        TextView txttitle = dialog.findViewById(R.id.txttitle);
                        RadioButton radioDeleteFromOrig = dialog.findViewById(R.id.radioDeleteFromOrig);

                        txttitle.setText(selectedItems.size() + " files copy to " + name);

                        txtCancel.setOnClickListener(v12 -> dialog.dismiss());

                        txtOk.setOnClickListener(v13 -> {
                            dialog.dismiss();

                            if (SplashActivity.isDataLoaded) {
                                copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked());
                            } else {
                                Utils.getInstance().showImageLoadingDialog(ViewFolderImagesActivity.this, () -> copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked()));
                            }

                        });
                        dialog.show();
                    });
                });

                Point size = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(size);
                infoPopup.showAtLocation(v, Gravity.BOTTOM, size.x - v.getWidth(), v.getHeight() + (int) getResources().getDimension(com.intuit.sdp.R.dimen._40sdp));
            } else {

                Utils.getInstance().showWarning(context, "Please select photos");
            }
        }

        private void copyPhotosToAlbum(File file, boolean isDelete) {

            final Dialog dialogProgress = new Dialog(context, R.style.dialog);
            dialogProgress.setCancelable(false);
            dialogProgress.setCanceledOnTouchOutside(false);

            dialogProgress.setContentView(R.layout.dialog_progress);
            dialogProgress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            ProgressBar progress = dialogProgress.findViewById(R.id.progress);
            TextView txtPerc = dialogProgress.findViewById(R.id.txtPerc);
            TextView txtTotal = dialogProgress.findViewById(R.id.txtTotal);
            TextView txtCancelCopy = dialogProgress.findViewById(R.id.txtCancel);

            txtTotal.setText("0/" + selectedItems.size());
            txtPerc.setText("0%");

            progress.setMax(selectedItems.size());

            startCopyFiles task = new startCopyFiles(dialogProgress, progress, txtPerc, txtTotal, file, isDelete);
            task.execute();

            txtCancelCopy.setOnClickListener(v15 -> {
                dialogProgress.dismiss();
                task.cancel(true);
            });
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {

            TextView textHeader;

            public HeaderViewHolder(View itemView) {
                super(itemView);

                textHeader = itemView.findViewById(R.id.text1);
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imgthumb;
            ImageView imgThumbnail;
            ImageView imgSelection;

            LinearLayout llVideo;

            public MyViewHolder(View itemView) {
                super(itemView);

                imgthumb = itemView.findViewById(R.id.imgThumb);
                imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
                imgSelection = itemView.findViewById(R.id.imgSelection);

                llVideo = itemView.findViewById(R.id.llVideo);
            }
        }

        private class startCopyFiles extends AsyncTask<Void, Void, Void> {

            boolean isDeleteFromOrigin;
            Dialog dialog;
            TextView txtPercentage;
            TextView txtTotalVal;
            File file;
            ProgressBar progress;

            public startCopyFiles(Dialog dialogProgress, ProgressBar progressBar, TextView txtPerc, TextView txtTotal, File file, boolean isDelete) {
                dialog = dialogProgress;
                txtPercentage = txtPerc;
                txtTotalVal = txtTotal;
                this.file = file;
                progress = progressBar;
                isDeleteFromOrigin = isDelete;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                context.runOnUiThread(() -> dialog.show());
            }

            @Override
            protected Void doInBackground(Void... voids) {

                ArrayList<String> copiedlist = new ArrayList<>();
                for (int i = 0; i < selectedItems.size(); i++) {

                    String path = selectedItems.get(i);
                    File dest = new File(file, new File(path).getName());
                    Utils.getInstance().copyImageFile(ViewFolderImagesActivity.this, new File(path), dest);

                    copiedlist.add(dest.getAbsolutePath());

                    if (isDeleteFromOrigin) {

                        Utils.getInstance().recycleImage(path, context);
                        Utils.getInstance().removeImage(path, context);
                    }

                    int temp = ((i + 1) * 100);
                    temp = temp / selectedItems.size();

                    int finalI = i;
                    int finalTemp = temp;

                    context.runOnUiThread(() -> {

                        progress.setProgress(finalI);
                        txtPercentage.setText(finalTemp + "%");
                        txtTotalVal.setText(finalI + 1 + "/" + selectedItems.size());

                        if (finalI == selectedItems.size() - 1) {

                            folderData.put(file.getName(), copiedlist);

                            Utils.getInstance().showSuccess(context, "Copied " + selectedItems.size() + " items");

                            dialog.dismiss();
                            disableEdit();
                            FolderFragment.adapter.addData(folderData);

                        }

                        Utils.getInstance().scanMedia(context);
                    });
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);

                context.runOnUiThread(() -> dialog.dismiss());
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

        if (mAdapter != null) {

            if (mAdapter.isSelectionEnable) {
                mAdapter.disableEdit();
            }

            getData();
            mAdapter.notifyDataSetChanged();

            if (data.size() > 0) {
                llNoDataFound.setVisibility(View.GONE);
                listPhotos.setVisibility(View.VISIBLE);
            } else {
                llNoDataFound.setVisibility(View.VISIBLE);
                listPhotos.setVisibility(View.GONE);
            }
        }
    }
}

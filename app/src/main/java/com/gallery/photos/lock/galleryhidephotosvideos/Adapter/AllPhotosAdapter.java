package com.gallery.photos.lock.galleryhidephotosvideos.Adapter;

import static com.gallery.photos.lock.galleryhidephotosvideos.Activity.MainActivity.txtSelectedTitle;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimagesCopyWithoutDates;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.folderData;

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
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gallery.photos.lock.galleryhidephotosvideos.Activity.AddtoAlbumActivity;
import com.gallery.photos.lock.galleryhidephotosvideos.Activity.MainActivity;
import com.gallery.photos.lock.galleryhidephotosvideos.Activity.SplashActivity;
import com.gallery.photos.lock.galleryhidephotosvideos.Activity.ViewImageActivity;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.FolderFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Database;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.MyApplication;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.gallery.photos.lock.galleryhidephotosvideos.library.ViewAnimator.ViewAnimator;
import com.gallery.photos.lock.galleryhidephotosvideos.stickyheader.StickyHeaders;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AllPhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaders, StickyHeaders.ViewSetup {


    HashMap<Integer, Integer> params = new HashMap<>();

    Dialog mLoadingDialog;
    long DURATION = 100;
    private boolean on_attach = true;

    private static final int HEADER_ITEM = 123;
    private static final int SUB_ITEM = 124;
    private final Activity context;
    private final String TodayDate;
    private final String YesterdayDate;

    public boolean isSelectionEnable = false;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<String> allimages = new ArrayList<>();

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

    public AllPhotosAdapter(Activity context, ArrayList<String> data) {
        this.context = context;

        allimages.addAll(data);

        TodayDate = getTodayDate(true);
        YesterdayDate = getTodayDate(false);
    }

    public void adddata(ArrayList<String> data) {
        allimages.clear();
        allimages.addAll(data);

        notifyDataSetChanged();

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (holder instanceof HeaderViewHolder) {
            if (allimages.get(pos).equals(TodayDate)) {
                ((HeaderViewHolder) holder).textHeader.setText("Today");
            } else if (allimages.get(pos).equals(YesterdayDate)) {
                ((HeaderViewHolder) holder).textHeader.setText("Yesterday");
            } else {
                String dates = allimages.get(pos);
                if (dates.startsWith("0")) {
                    dates = dates.substring(1, dates.length());
                }

                try {
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

            MyViewHolder holder1 = (MyViewHolder) holder;

            if (isSelectionEnable) {
                holder1.imgSelection.setVisibility(View.VISIBLE);

                if (selectedItems.contains(allimages.get(pos))) {
                    holder1.imgSelection.setImageResource(R.drawable.ic_check);
                } else {
                    holder1.imgSelection.setImageResource(R.drawable.ic_allselecticon);
                }
            } else {
                holder1.imgSelection.setVisibility(View.GONE);
            }

            Glide.with(context).load(R.drawable.img_placeholder).into(holder1.imgThumbnail);
            boolean isImage = Utils.getInstance().isImageType(allimages.get(pos), context);

            if (isImage) {
                holder1.llVideo.setVisibility(View.GONE);
            } else {
                holder1.llVideo.setVisibility(View.VISIBLE);
              /*  if (Application.durations.containsKey(allimages.get(position))) {
                    holder1.txtVideoTime.setText("" + Utils.convertMillieToHMmSs(Application.durations.get(allimages.get(position))));
                } else
                    holder1.txtVideoTime.setText("" + Utils.getInstance().getvideoDurationForhidden(context, allimages.get(position)));*/
            }

            Glide.with(context).load(allimages.get(pos)).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
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

            holder.itemView.setOnClickListener(v -> {
                if (isSelectionEnable) {

                    if (selectedItems.contains(allimages.get(pos))) {
                        selectedItems.remove(allimages.get(pos));
                    } else {
                        selectedItems.add(allimages.get(pos));
                    }

                    notifyItemChanged(pos);
                    txtSelectedTitle.setText(selectedItems.size() + " Selected");

                } else {
                    new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            Intent intent = new Intent(context, ViewImageActivity.class);
                            intent.putExtra("position", pos);
                            intent.putExtra("path", allimages.get(pos));
                            intent.putExtra("type", 1);
                            context.startActivity(intent);
                        }
                    });

                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (!isSelectionEnable) {
                    enableEdit();

                    selectedItems.add(allimages.get(pos));
                    notifyDataSetChanged();
                    txtSelectedTitle.setText(selectedItems.size() + " Selected");
                }
                return false;
            });

        }
    }

    public void enableEdit() {

        selectedItems.clear();
        isSelectionEnable = true;
        txtSelectedTitle.setText(selectedItems.size() + " Selected");

        MainActivity.imgselectall.setVisibility(View.VISIBLE);
        MainActivity.setupPhotosEditOption();

        MainActivity.relSelection.setVisibility(View.VISIBLE);
        MainActivity.llSelectedOptions.setVisibility(View.VISIBLE);
        MainActivity.relActionbarMain.setVisibility(View.GONE);
        MainActivity.llHideOptions.setVisibility(View.VISIBLE);
        MainActivity.bottomBar.setVisibility(View.GONE);

        MainActivity.viewPager.setPagingEnabled(false);

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return allimages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (allimages.get(position).contains("storage")) {
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
        if (isBack || selectedItems.size() == allimagesCopyWithoutDates.size()) {
            selectedItems.clear();
        } else {
            selectedItems.clear();
            selectedItems.addAll(allimagesCopyWithoutDates);
        }

        txtSelectedTitle.setText(selectedItems.size() + " Selected");

        notifyDataSetChanged();
    }

    public void hidePhotos() {
        if (selectedItems.size() > 0) {
            Utils.getInstance().hidePhotos(selectedItems, context, true, () -> {
                disableEdit();
            });
        } else {
            Utils.getInstance().showWarning(context, "Please select photos");
        }
    }

    public void deletePhotos() {

        if (SplashActivity.isDataLoaded) {

            if (selectedItems.size() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this file?");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    showLoader(context);
                    dialog.dismiss();
                    new Handler(Looper.getMainLooper())
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for (String path : selectedItems) {
                                        allimages.remove(path);
                                        Utils.getInstance().recycleImage(path, context);
                                        Utils.getInstance().removeImage(path, context);
                                    }

                                    Utils.getInstance().showSuccess(context, selectedItems.size() + " items deleted.");

                                    dismissLoader();
                                    disableEdit();
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

            Utils.getInstance().showImageLoadingDialog(context, () -> {

                deletePhotos();
            });
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

    public void disableEdit() {


        selectedItems.clear();

        this.allimages.clear();
        this.allimages.addAll(MyApplication.allimages);

        notifyDataSetChanged();
        MainActivity.activity.onBackPressed();
    }

    public void moreOptionClicked(View v, Activity activity) {
        if (selectedItems.size() > 0) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View popupView = layoutInflater.inflate(R.layout.popup_more_options, null);

            PopupWindow infoPopup;
            infoPopup = new PopupWindow(popupView, (int) context.getResources().getDimension(com.intuit.sdp.R.dimen._130sdp), ViewGroup.LayoutParams.WRAP_CONTENT);
            infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            infoPopup.setOutsideTouchable(true);

            TextView txtFavourite = popupView.findViewById(R.id.txtFavourite);
            TextView txtAddtoAlbum = popupView.findViewById(R.id.txtAddtoAlbum);
            TextView txtNewAlbum = popupView.findViewById(R.id.txtNewAlbum);

            txtFavourite.setOnClickListener(v1 -> {

                infoPopup.dismiss();
                Database db = new Database(context);

                for (int i = 0; i < selectedItems.size(); i++) {
                    db.addToFavourite(selectedItems.get(i), true);
                }

                Utils.getInstance().showSuccess(context, "Added to favourite Successfully");
                disableEdit();

            });

            txtAddtoAlbum.setOnClickListener(v15 -> {
                infoPopup.dismiss();

                if (SplashActivity.isDataLoaded) {
                    new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                        @Override
                        public void onAdClosed() {
                            context.startActivity(new Intent(context, AddtoAlbumActivity.class).putExtra("selected", selectedItems));

                        }
                    });
                } else {
                    Utils.getInstance().showImageLoadingDialog(context, () -> {
                        new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                context.startActivity(new Intent(context, AddtoAlbumActivity.class).putExtra("selected", selectedItems));

                            }
                        });
                    });
                }
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

                        if (SplashActivity.isDataLoaded) {
                            copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked());
                            dialog.dismiss();

                        } else {
                            Utils.getInstance().showImageLoadingDialog(context, () -> {
                                new InterAds().showInterAds(context, new InterAds.OnAdClosedListener() {
                                    @Override
                                    public void onAdClosed() {
                                        copyPhotosToAlbum(file, radioDeleteFromOrig.isChecked());
                                        dialog.dismiss();
                                    }
                                });
                            });
                        }
                    });
                    dialog.show();
                });
            });

            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            infoPopup.showAtLocation(v, Gravity.BOTTOM, size.x - v.getWidth(), v.getHeight() + (int) context.getResources().getDimension(com.intuit.sdp.R.dimen._40sdp));

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

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView textHeader;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            textHeader = itemView.findViewById(R.id.text1);
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgthumb;
        ImageView imgThumbnail;
        ImageView imgSelection;
        LinearLayout llVideo;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            imgthumb = itemView.findViewById(R.id.imgThumb);
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
                Utils.getInstance().copyImageFile(context, new File(path), dest);

                copiedlist.add(dest.getAbsolutePath());

                if (isDeleteFromOrigin) {

                    Utils.getInstance().recycleImage(path, context);
                    Utils.getInstance().removeImage(path, context);
                }

                int temp = ((i + 1) * 100);
                temp = temp / selectedItems.size();

                int finalI = i;
                int finalTemp = temp;

                Utils.getInstance().scanMedia(context, file.getAbsolutePath());

                context.runOnUiThread(() -> {

                    Utils.getInstance().addFiles(dest.getAbsolutePath(), context);

                    progress.setProgress(finalI);
                    txtPercentage.setText(finalTemp + "%");
                    txtTotalVal.setText(finalI + 1 + "/" + selectedItems.size());

                    if (finalI == selectedItems.size() - 1) {

                        folderData.put(file.getName(), copiedlist);

                        Utils.getInstance().showSuccess(context, "Copied " + selectedItems.size() + " items");

                        FolderFragment.adapter.addData(folderData);

                        dialog.dismiss();
                        disableEdit();
                    }

                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            Utils.getInstance().scanMedia(context);

            context.runOnUiThread(() -> dialog.dismiss());
        }
    }
}


package com.gallery.photos.lock.galleryhidephotosvideos.Activity;

import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.DefaultFolderData;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.allimages;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.binImages;
import static com.gallery.photos.lock.galleryhidephotosvideos.MyApplication.folderData;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.BackInterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.InterAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Adhelper.MiniNativeAds;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.AllPhotosFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Fragment.FolderFragment;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Constant;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Database;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.GridRecyclerView;
import com.gallery.photos.lock.galleryhidephotosvideos.Helper.Utils;
import com.gallery.photos.lock.galleryhidephotosvideos.MyApplication;
import com.gallery.photos.lock.galleryhidephotosvideos.R;
import com.gallery.photos.lock.galleryhidephotosvideos.library.ViewAnimator.ViewAnimator;
import com.preference.PowerPreference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class RecyclebinActivity extends AppCompatActivity {

    private ArrayList<String> selectedItems = new ArrayList<>();
    private TextView txtSelectedTitle;
    private boolean isSelectionEnable;
    private AlbumAdapter adapter;
    private LinearLayout llSelectedOptions;
    private LinearLayout llNotrash;
    private GridRecyclerView listBin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recycle_bin);
        setview();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    private void setview() {

        findViewById(R.id.imgBack).setOnClickListener(v -> onBackPressed());

        listBin = findViewById(R.id.listBin);
        txtSelectedTitle = findViewById(R.id.txtSelectedTitle);
        llSelectedOptions = findViewById(R.id.llSelectedOptions);

        llNotrash = findViewById(R.id.llNotrash);
        llSelectedOptions.setVisibility(View.GONE);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == 1) {
                    return 3;
                }
                return 1;
            }
        });

        listBin.setLayoutManager(manager);


        if (binImages.size() > 0) {
            listBin.setVisibility(View.VISIBLE);
            llNotrash.setVisibility(View.GONE);
        } else {
            listBin.setVisibility(View.GONE);
            llNotrash.setVisibility(View.VISIBLE);
        }

        adapter = new AlbumAdapter();
        listBin.setAdapter(adapter);

        if (adapter.getItemCount() > 0) {
            listBin.setVisibility(View.VISIBLE);
            llNotrash.setVisibility(View.GONE);
        } else {
            listBin.setVisibility(View.GONE);
            llNotrash.setVisibility(View.VISIBLE);
        }

        LinearLayout llDelete = findViewById(R.id.llDelete);
        LinearLayout llRestore = findViewById(R.id.llRestore);


        llDelete.setOnClickListener(view -> {
            if (selectedItems.size() > 0) {
                deleteFiles();
            } else
                Utils.getInstance().showWarning(this, "Please select photos to delete");
        });

        llRestore.setOnClickListener(view -> {
            if (selectedItems.size() > 0) {
                if (SplashActivity.isDataLoaded) {
                    restoreImages();
                } else {
                    Utils.getInstance().showImageLoadingDialog(RecyclebinActivity.this, () -> restoreImages());
                }
            } else
                Utils.getInstance().showWarning(this, "Please select photos to restore");
        });

    }

    private void restoreImages() {

        final Dialog dialogProgress = new Dialog(this, R.style.dialog);
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

        RestoreFiles task = new RestoreFiles(dialogProgress, progress, txtPerc, txtTotal);
        task.execute();

        txtCancelCopy.setOnClickListener(v15 -> {
            dialogProgress.dismiss();
            task.cancel(true);
        });
    }

    public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        HashMap<Integer, Integer> params = new HashMap<>();
        long DURATION = 100;
        private boolean on_attach = true;

        public AlbumAdapter() {

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos, parent, false);
            return new Viewholder(inflate);
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
            if (!params.containsKey(i)) {

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

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, int position) {


                Viewholder holder = (Viewholder) holderMain;

                setAnimation(holder.itemView, position);
                if (isSelectionEnable) {
                    holder.imgSelection.setVisibility(View.VISIBLE);

                    if (selectedItems.contains(binImages.get(position))) {
                        holder.imgSelection.setImageResource(R.drawable.ic_check);
                    } else {
                        holder.imgSelection.setImageResource(R.drawable.ic_allselecticon);
                    }

                } else {
                    holder.imgSelection.setVisibility(View.GONE);
                }

                Glide.with(RecyclebinActivity.this).load(R.drawable.img_placeholder).into(holder.imgThumbnail);

                boolean isImage = Utils.getInstance().isImageTypeForHidden(binImages.get(position));

                if (isImage) {
                    holder.llVideo.setVisibility(View.GONE);
                } else {
                    holder.llVideo.setVisibility(View.VISIBLE);

               /* if (Application.durations.containsKey(binImages.get(position))) {
                    holder.txtVideoTime.setText(Utils.convertMillieToHMmSs(Application.durations.get(binImages.get(position))));
                } else
                    holder.txtVideoTime.setText("" + Utils.getInstance().getvideoDurationForhidden(RecyclebinActivity.this, binImages.get(position)));
*/
                }

                Glide.with(RecyclebinActivity.this).load(binImages.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.imgThumbnail.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.imgThumbnail.setVisibility(View.GONE);
                        return false;
                    }

                }).into(holder.imgthumb);


                holder.itemView.setOnLongClickListener(view -> {
                    if (!isSelectionEnable) {

                        isSelectionEnable = true;
                        llSelectedOptions.setVisibility(View.VISIBLE);

                        selectedItems.add(binImages.get(position));
                        notifyDataSetChanged();
                    }

                    return false;
                });

                holder.itemView.setOnClickListener(view -> {
                    if (isSelectionEnable) {
                        if (selectedItems.contains(binImages.get(position))) {
                            selectedItems.remove(binImages.get(position));
                        } else {
                            selectedItems.add(binImages.get(position));
                        }

                        txtSelectedTitle.setText(selectedItems.size() + " selected");
                        notifyItemChanged(position);

                    } else {
                        new InterAds().showInterAds(RecyclebinActivity.this, new InterAds.OnAdClosedListener() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(RecyclebinActivity.this, ViewRecycledImageActivity.class).putExtra("path", binImages.get(position)));
                            }
                        });
                    }
                });
        }

        @Override
        public int getItemCount() {
            return binImages.size();
        }


        class Viewholder extends RecyclerView.ViewHolder {
            ImageView imgthumb;
            ImageView imgThumbnail;
            ImageView imgSelection;

            LinearLayout llVideo;

            public Viewholder(View itemView) {
                super(itemView);

                imgthumb = itemView.findViewById(R.id.imgThumb);
                imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
                imgSelection = itemView.findViewById(R.id.imgSelection);

                llVideo = itemView.findViewById(R.id.llVideo);

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
        if (adapter != null) {
            adapter.notifyDataSetChanged();

            if (binImages.size() > 0) {
                listBin.setVisibility(View.VISIBLE);
                llNotrash.setVisibility(View.GONE);
            } else {
                listBin.setVisibility(View.GONE);
                llNotrash.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isSelectionEnable) {
            isSelectionEnable = false;
            selectedItems.clear();
            adapter.notifyDataSetChanged();
            llSelectedOptions.setVisibility(View.GONE);
            txtSelectedTitle.setText("Recycle bin");
        } else {
            new BackInterAds().showInterAds(this, new BackInterAds.OnAdClosedListener() {
                @Override
                public void onAdClosed() {
                    finish();
                }
            });
        }
    }

    private void deleteFiles() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this file?");
        builder.setPositiveButton("OK", (dialog, which) -> {

            final Dialog dialogProgress = new Dialog(this, R.style.dialog);
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

            startDeleteFiles task = new startDeleteFiles(dialogProgress, progress, txtPerc, txtTotal);
            task.execute();

            txtCancelCopy.setOnClickListener(v15 -> {
                dialogProgress.dismiss();
                task.cancel(true);
            });
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private class startDeleteFiles extends AsyncTask<Void, Void, Void> {

        Dialog dialog;
        TextView txtPercentage;
        TextView txtTotalVal;

        ProgressBar progress;

        public startDeleteFiles(Dialog dialogProgress, ProgressBar progressBar, TextView txtPerc, TextView txtTotal) {
            dialog = dialogProgress;
            txtPercentage = txtPerc;
            txtTotalVal = txtTotal;
            progress = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            runOnUiThread(() -> dialog.show());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < selectedItems.size(); i++) {
                String path = selectedItems.get(i);
                new File(path).delete();

                binImages.remove(path);

                int temp = ((i + 1) * 100);
                temp = temp / selectedItems.size();

                int finalI = i;
                int finalTemp = temp;

                runOnUiThread(() -> {
                    if (path.endsWith(".nomedia")) {
                        String mpath = Database.getInstance(RecyclebinActivity.this).getHiddenFileFoldername(path);
                        if (mpath != null) {
                            Database.getInstance(RecyclebinActivity.this).addToFavourite(path, false);
                        }
                    } else {
                        Database.getInstance(RecyclebinActivity.this).addToFavourite(path, false);
                    }
                    progress.setProgress(finalI);
                    txtPercentage.setText(finalTemp + "%");
                    txtTotalVal.setText(finalI + 1 + "/" + selectedItems.size());
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            Database.getInstance(RecyclebinActivity.this).deleteFromBin(selectedItems);

            runOnUiThread(() -> {

                adapter.notifyDataSetChanged();
                if (binImages.size() > 0) {
                    listBin.setVisibility(View.VISIBLE);
                    llNotrash.setVisibility(View.GONE);
                } else {
                    listBin.setVisibility(View.GONE);
                    llNotrash.setVisibility(View.VISIBLE);
                }

                dialog.dismiss();

                Utils.getInstance().showSuccess(RecyclebinActivity.this, "Deleted " + selectedItems.size() + " items");
                adapter.notifyDataSetChanged();

                onBackPressed();

                if (binImages.size() > 0) {
                    listBin.setVisibility(View.VISIBLE);
                    llNotrash.setVisibility(View.GONE);
                } else {
                    listBin.setVisibility(View.GONE);
                    llNotrash.setVisibility(View.VISIBLE);
                }

            });
        }
    }

    private class RestoreFiles extends AsyncTask<Void, Void, Void> {

        Dialog dialog;
        TextView txtPercentage;
        TextView txtTotalVal;

        ProgressBar progress;

        public RestoreFiles(Dialog dialogProgress, ProgressBar progressBar, TextView txtPerc, TextView txtTotal) {
            dialog = dialogProgress;
            txtPercentage = txtPerc;
            txtTotalVal = txtTotal;
            progress = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            runOnUiThread(() -> dialog.show());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < selectedItems.size(); i++) {

                String path = selectedItems.get(i);

                File destFile = new File(Database.getInstance(RecyclebinActivity.this).getBinInfo(new File(path).getName()));

                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }

                Utils.getInstance().copyImageFile(RecyclebinActivity.this, new File(path), destFile);

                Utils.getInstance().addFiles(destFile.getAbsolutePath(), RecyclebinActivity.this);
                binImages.remove(path);

                new File(path).delete();

                int temp = ((i + 1) * 100);
                temp = temp / selectedItems.size();

                int finalI = i;
                int finalTemp = temp;

                runOnUiThread(() -> {

                    progress.setProgress(finalI);
                    txtPercentage.setText(finalTemp + "%");
                    txtTotalVal.setText(finalI + 1 + "/" + selectedItems.size());
                });

                Utils.getInstance().scanMedia(RecyclebinActivity.this, destFile.getAbsolutePath());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            Database.getInstance(RecyclebinActivity.this).deleteFromBin(selectedItems);

            TreeMap<String, ArrayList<String>> sorted = new TreeMap<>();
            sorted.putAll(MyApplication.folderData);

            folderData.clear();
            folderData.putAll(sorted);

            runOnUiThread(() -> {

                adapter.notifyDataSetChanged();

                AllPhotosFragment.mAdapter.adddata(allimages);
                AllPhotosFragment.mAdapter.notifyDataSetChanged();

                FolderFragment.adapter.addData(folderData);
                FolderFragment.adapterDefault.addData(DefaultFolderData);

                if (binImages.size() > 0) {
                    listBin.setVisibility(View.VISIBLE);
                    llNotrash.setVisibility(View.GONE);
                } else {
                    listBin.setVisibility(View.GONE);
                    llNotrash.setVisibility(View.VISIBLE);
                }

                dialog.dismiss();

                Utils.getInstance().showSuccess(RecyclebinActivity.this, "Restored " + selectedItems.size() + " items");
                adapter.notifyDataSetChanged();

                onBackPressed();

                if (binImages.size() > 0) {
                    listBin.setVisibility(View.VISIBLE);
                    llNotrash.setVisibility(View.GONE);
                } else {
                    listBin.setVisibility(View.GONE);
                    llNotrash.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}

package com.gallery.photos.lock.galleryhidephotosvideos.crop.callback;

import android.graphics.Bitmap;

public interface CropCallback extends Callback {
  void onSuccess(Bitmap cropped);
}

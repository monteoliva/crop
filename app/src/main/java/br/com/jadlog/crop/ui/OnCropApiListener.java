package br.com.jadlog.crop.ui;

import android.graphics.Bitmap;

public interface OnCropApiListener {
    void onCropBitmap(Bitmap bitmap);
    void onCropHash(String hash);
}

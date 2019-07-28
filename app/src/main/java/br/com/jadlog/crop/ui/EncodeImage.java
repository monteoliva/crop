package br.com.jadlog.crop.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class EncodeImage {
    public byte[] encodeImage(Bitmap bitmap){
        byte[] retorno = null;

        if (bitmap == null) { return null; }

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);

            retorno = stream.toByteArray();
        }
        catch (Exception e) {
            Log.d("Signature", "Error encodeImage - " + e.getMessage());
        }

        // retorna em branco
        return retorno;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        try {
            final int height = bmpOriginal.getHeight();
            final int width  = bmpOriginal.getWidth();

            final Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bmpGrayscale);
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            Paint paint = new Paint();
            paint.setColorFilter(f);
            c.drawBitmap(bmpOriginal, 0, 0, paint);
            return bmpGrayscale;
        }
        catch (NullPointerException ne) { return null; }
    }
}
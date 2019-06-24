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
    public String encodeImage(Bitmap bitmap){
        String retorno = "";

        if (bitmap == null) { return ""; }

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);

            byte[] bytes = stream.toByteArray();
            retorno = encodeImage(bytes);
        }
        catch (Exception e) {
            Log.d("Signature", "Error encodeImage - " + e.getMessage());
            //e.printStackTrace();
        }

        // retorna em branco
        return retorno;
    }

    public String encodeImage(byte[] bytes){

        try {
            String s = Base64.encodeToString(bytes, Base64.NO_WRAP);
            return s;
        }
        catch (NullPointerException ne) { return ""; }

    }

    public Bitmap decodeImageBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public Bitmap decodedBitmap(byte[] source, int reqWidth, int reqHeight) {
        final Bitmap old = BitmapFactory.decodeByteArray(source, 0, source.length);
        final Bitmap bmp = Bitmap.createScaledBitmap(old, reqWidth, reqHeight, false);

        // retorna o Bitmap
        return bmp;
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
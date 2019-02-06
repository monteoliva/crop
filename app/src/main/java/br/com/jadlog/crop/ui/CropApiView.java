package br.com.jadlog.crop.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.vision.CameraSource;

public class CropApiView extends View {
    private static final String TAG = "CropAPI";

    private CameraSource cameraSource;

    private Rect rect = new Rect(0, 0, 0, 0);

    /**
     * Constructor
     * @param context
     */
    public CropApiView(Context context) { super(context); init(); }
    public CropApiView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() { }

    public Rect getRect() { return this.rect; }

    /**********************************************************************
     * Draw
     **********************************************************************/
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int width  = canvas.getWidth();
        int height = canvas.getHeight();

        int top    = (height / 2) - 100;
        int left   = 0; //70;
        int right  = width; // - left;
        int bottom = top;

        if (height > 540) { bottom += 200; }
        else              { bottom += 120; }

        //rect.setWidht(width);
        //rect.setHeight(height);
        rect.top    = top;
        rect.left   = left;
        rect.right  = right;
        rect.bottom = bottom;

        setBackground(canvas, 0, 0, width, top);
		//setBackground(canvas, 0, top, left, bottom);
		//setBackground(canvas, right, top, width, bottom);
		setBackground(canvas, 0, bottom, width, height);

        Paint paint = new Paint();
              paint.setColor(Color.TRANSPARENT);
              paint.setAntiAlias(true);
		      paint.setStyle(Paint.Style.STROKE);
              paint.setStrokeWidth(1);

        canvas.drawRect(left  ,
                        top   ,
                        right ,
                        bottom,
                        paint);
    }

    private void setBackground(Canvas canvas, int left, int top, int right, int bottom) {
        Paint rect_paint = new Paint();
              rect_paint.setAntiAlias(true);
              rect_paint.setStyle(Paint.Style.FILL);
              rect_paint.setColor(Color.BLACK);
              rect_paint.setAlpha(0x95);

        canvas.drawRect(left, top, right, bottom, rect_paint);
    }
}
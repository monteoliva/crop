package br.com.jadlog.crop.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CropApiView extends View {
    private Rect rect = new Rect(0, 0, 0, 0);

    /**
     * Constructor
     * @param context
     */
    public CropApiView(Context context) { super(context); }
    public CropApiView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    public Rect getRect() { return this.rect; }

    /**********************************************************************
     * Draw
     **********************************************************************/
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final int width  = canvas.getWidth();
        final int height = canvas.getHeight();

        rect.top    = (height / 2) + 120;
        rect.left   = 110;
        rect.right  = 0;
        rect.bottom = rect.top + 105;

        final int top    = (height / 2) - 200;
        final int left   = 70;
        final int right  = width - 70;
        final int bottom = top + 250;

		setBackground(canvas, 0, 0, width, top);
		setBackground(canvas, 0, top, left, bottom);
		setBackground(canvas, right, top, width, bottom);
		setBackground(canvas, 0, bottom, width, height);

        Paint paint = new Paint();
              paint.setColor(Color.WHITE);
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
        rect_paint.setAlpha(0x80); // optional

        canvas.drawRect(left, top, right, bottom, rect_paint);
    }
}

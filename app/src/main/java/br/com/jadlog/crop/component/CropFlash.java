package br.com.jadlog.crop.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.com.jadlog.crop.R;
import br.com.jadlog.crop.ui.CropApiCamera;

public class CropFlash extends LinearLayout {
    private ImageView imgFlash;
    private CropApiCamera cropApiCamera;

    private boolean isFlash = false;

    private static final int[] resources = new int[] {
            R.drawable.ic_flash_on,
            R.drawable.ic_flash_off
    };

    /**
     * Constructor
     *
     * @param context
     */
    public CropFlash(Context context)                               { super(context);        init(); }
    public CropFlash(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        imgFlash = new ImageView(getContext());
        imgFlash.setAdjustViewBounds(true);
        imgFlash.setImageResource(resources[1]);

        addView(imgFlash, layoutParams);

    }

    public void setFlash() {
        if (cropApiCamera != null) {
            isFlash = !isFlash;

            cropApiCamera.setFlash(isFlash);

            imgFlash.setImageResource((!isFlash) ? resources[1] : resources[0]);
        }
    }

    public void setCropApiCamera(CropApiCamera cropApiCamera) { this.cropApiCamera = cropApiCamera; }
}

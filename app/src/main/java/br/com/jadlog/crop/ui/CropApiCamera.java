package br.com.jadlog.crop.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.IOException;

public class CropApiCamera extends FrameLayout {
    private static final String TAG = "CropApi";

    private TextureView mTextureView;
    private Camera mCamera;
    private CropApiView mView;

    public static final int NR_CAMERA_BACK  = 0;

    private boolean isPreview = false;

    /**
     * Constructor
     *
     * @param context
     */
    public CropApiCamera(Context context) { super(context); }
    public CropApiCamera(Context context, AttributeSet attrs) { super(context, attrs); }

    public void start() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mTextureView = new TextureView(getContext());
        mTextureView.setSurfaceTextureListener(callback2);

        mView = new CropApiView(getContext());

        addView(mTextureView, layoutParams);
        addView(mView, layoutParams);
    }

    public void release() {
        if (mCamera != null && isPreview) {
            mCamera.stopPreview();
            mCamera.release();
            isPreview = false;
        }
    }

    private void open(@NonNull SurfaceTexture surface) {
        if (isPreview) { return; }

        // pega a tela
        final Activity tela = (Activity) getContext();

        // pega a orientacao da camera
        final int orientacao = getCameraDisplayOrientation(tela, NR_CAMERA_BACK);

        mCamera = Camera.open(NR_CAMERA_BACK);

        try {
            Camera.Parameters param = mCamera.getParameters();

            if (param.getSupportedFocusModes().contains("continuous-video")) {
                param.setFocusMode("continuous-video");
            }

            mCamera.setParameters(param);
            mCamera.setDisplayOrientation(orientacao);
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();

            isPreview = true;
        }
        catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void takePicture(@NonNull OnCropApiListener listener) {
        if (mCamera != null) {
            if (mTextureView.isAvailable()) {
                if (listener != null) { listener.onCropBytes(crop()); }
            }
        }
    }

    private byte[] crop() {
        final Bitmap source = mTextureView.getBitmap(mTextureView.getWidth(), mTextureView.getHeight());
        final Rect rect     = mView.getRect();
        final int bottom    = rect.bottom - rect.top;

        final Bitmap output = Bitmap.createBitmap(source,
					rect.left,
					rect.top,
					rect.right,
					bottom);

        final int width  = (output.getWidth()  > 640) ? 640 : output.getWidth();
        final int height = (output.getHeight() > 200) ? 200 : output.getHeight();
        final Bitmap bmp = Bitmap.createScaledBitmap(output, width, height, false);
        final Bitmap ret = new EncodeImage().toGrayscale(bmp);

        isPreview = false;

		return new EncodeImage().encodeImage(ret);
    }

    /**********************************************************************
     * TextureView Listener
     **********************************************************************/
    TextureView.SurfaceTextureListener callback2 = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            open(surface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            release();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    /**********************************************************************
     * Image Utils
     **********************************************************************/
    private int getCameraDisplayOrientation(Activity tela, int cameraId) {
        // seta a rotacao de retorno
        int rotacao = 0;

        // pega informacoes da Camera
        final Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();

        // pega informacoes da Camera
        Camera.getCameraInfo(cameraId, cameraInfo);

        // pega a Orientacao atual da Activity
        final int rotation = tela.getWindowManager().getDefaultDisplay().getRotation();

        // pega a rotacao da SurfaceView
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:   degrees =   0; break;
            case Surface.ROTATION_90:  degrees =  90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        // calcula a rotacao (orientacao)
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotacao = (cameraInfo.orientation + degrees) % 360;
            rotacao = (360 - rotacao) % 360;  // compensate the mirror
        }
        else {
            rotacao = (cameraInfo.orientation - degrees + 360) % 360;
        }

        // retorna a rotacao
        return rotacao;
    }
}
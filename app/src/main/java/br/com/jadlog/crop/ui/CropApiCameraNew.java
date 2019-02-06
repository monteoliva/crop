package br.com.jadlog.crop.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

public class CropApiCameraNew extends FrameLayout {
    private static final String TAG = "CropApi";

    private TextureView mTextureView;
    private Camera mCamera;
    private OnCropApiListener listener;
    private CropApiView mView;

    public static final int NR_CAMERA_BACK  = 0;

    /**
     * Constructor
     *
     * @param context
     */
    public CropApiCameraNew(Context context) { super(context); }
    public CropApiCameraNew(Context context, AttributeSet attrs) { super(context, attrs); }

    public void start() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mTextureView = new TextureView(getContext());
        mTextureView.setSurfaceTextureListener(callback2);

        addView(mTextureView, layoutParams);
    }

    public void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    private void open(@NonNull SurfaceTexture surface) {
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
        }
        catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void takePicture(@NonNull OnCropApiListener listener) {
        if (mCamera != null) {
            if (mTextureView.isAvailable()) {
                Bitmap bmp = mTextureView.getBitmap();

                release();

                listener.onCropHash(new EncodeImage().encodeImage(bmp));

                Log.d(TAG, "Bitmap width: " + bmp.getWidth());
            }
        }
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
            Log.d(TAG, "onSurfaceTextureUpdated");


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
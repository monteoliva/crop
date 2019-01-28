package br.com.jadlog.crop.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

import br.com.jadlog.crop.R;

public class CropApi extends RelativeLayout {
    private CameraSource mCameraSource;
    private CropApiCamera mPreview;
    private View view;
    private ImageView imgResult;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 200;

    // pega os Uses Permissions
    private static final String[] permissions = new String[] {
            Manifest.permission.CAMERA
    };

    /**
     * Constructor
     *
     * @param context
     */
    public CropApi(Context context)                     { super(context);         init(); }
    public CropApi(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        // seta o Background
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

        // pega o inflater
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // pega a View
        view = inflater.inflate(R.layout.crop_api, this);

        imgResult = view.findViewById(R.id.imgResult);
        mPreview  = view.findViewById(R.id.facePreview);

        final int rc1 = ActivityCompat.checkSelfPermission(getContext(), permissions[0]);
        if (rc1 == PackageManager.PERMISSION_GRANTED) { startCameraSource(); }
        else { requestCameraPermission(); }
    }

    /*******************************************************************************
     * Camera Source
     *******************************************************************************/
        public void createCameraSource() {
            FaceDetector detector = new FaceDetector.Builder(getContext())
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setTrackingEnabled(true)
                    .setMode(FaceDetector.FAST_MODE)
                    .setProminentFaceOnly(true)
                    .setMinFaceSize(0.30f)
                    .build();

        detector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());

        if (!detector.isOperational()) {
        }

        // start Google Vision
        mCameraSource = new CameraSource.Builder(getContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if (code != ConnectionResult.SUCCESS) {}

        // verifi
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            }
            catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
        else {
            createCameraSource();
            startCameraSource();
        }
    }

    private void stopCameraSource() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    public void takePicture() {
        if (mPreview != null) {
            mPreview.takePicture(new OnCropApiListener() {
                @Override
                public void onCrop(Bitmap bitmap) {
                    imgResult.setImageBitmap(bitmap);
                }
            });
        }
    }

    /*******************************************************************************
     * Tracker Factory
     *******************************************************************************/
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) { return null; }
    }

    /*******************************************************************************
     * Camera Permission
     *******************************************************************************/
    private void requestCameraPermission() {
        Activity activity = (Activity) getContext();
        ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RC_HANDLE_CAMERA_PERM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraSource();
            }
        }
    }

    /*******************************************************************************
     * Life Circle
     *******************************************************************************/
    public void onPause() {
        if (mPreview != null) { mPreview.stop(); }
    }

    public void onDestroy() { stopCameraSource(); }
}
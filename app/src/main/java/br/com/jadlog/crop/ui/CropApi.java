package br.com.jadlog.crop.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import br.com.jadlog.crop.R;

public class CropApi extends FrameLayout {
    private CropApiCamera mPreview;
    private View view;

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
    public CropApi(Context context)                     { super(context);        init(); }
    public CropApi(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        // seta o Background
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

        // pega o inflater
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // pega a View
        view = inflater.inflate(R.layout.crop_api, this);

        mPreview = view.findViewById(R.id.facePreview);

        final int rc1 = ActivityCompat.checkSelfPermission(getContext(), permissions[0]);
        if (rc1 == PackageManager.PERMISSION_GRANTED) { mPreview.start(); }
        else { requestCameraPermission(); }
    }

    public void takePicture(@NonNull final OnCropApiListener listener) {
        if (mPreview != null) {
            mPreview.takePicture(new OnCropApiListener() {
                @Override
                public void onCropHash(String hash) {
                    listener.onCropHash(hash);
                }
            });
        }
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
                mPreview.start();
            }
        }
    }

    /*******************************************************************************
     * Life Circle
     *******************************************************************************/
    public void onPause()   { mPreview.release(); }
    public void onDestroy() { mPreview.release(); }
}
package br.com.jadlog.crop.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CropApiCamera extends FrameLayout {
	private static final String TAG = "CropAPI";

	private SurfaceView mSurfaceView;
	private CameraSource mCameraSource;
	private OnCropApiListener listener;
	private CropApiView mView;

	private boolean mStartRequested;
	private boolean mSurfaceAvailable;

	/**
	 * Constructor
	 */
	public CropApiCamera(Context context)                     { super(context);        init(); }
	public CropApiCamera(Context context, AttributeSet attrs) { super(context, attrs); init(); }

	/**
	 * Metodo de inicializacao
	 */
	private void init() {
		// seta o Background
		setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mSurfaceView = new SurfaceView(getContext());
		mSurfaceView.getHolder().addCallback(callback);

		mView = new CropApiView(getContext());

		addView(mSurfaceView, layoutParams);
		addView(mView, layoutParams);
	}

	public void start(CameraSource cameraSource) throws IOException {
		if (cameraSource == null) { stop(); }

		mCameraSource = cameraSource;

		if (mCameraSource != null) {
			mStartRequested = true;
			startIfReady();
		}
	}

	public void stop() {
		if (mCameraSource != null) { mCameraSource.stop(); }
	}

	public void release() {
		if (mCameraSource != null) {
			mCameraSource.release();
			mCameraSource = null;
		}
	}

	public void takePicture(@NonNull OnCropApiListener listener) {
		if (mCameraSource != null) {
			this.listener = listener;
			mCameraSource.takePicture(myShutterCallback, myPictureCallback);
		}
	}

	private void startIfReady() throws IOException, SecurityException {
		if (mStartRequested && mSurfaceAvailable) {
			SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
			surfaceHolder.setFixedSize(getWidth(), getHeight());
			mCameraSource.start(surfaceHolder);

			mStartRequested = false;
		}
	}

	/**********************************************************************
	 * SurfaceHolder
	 **********************************************************************/
	SurfaceHolder.Callback2 callback = new SurfaceHolder.Callback2() {
		@Override
		public void surfaceRedrawNeeded(SurfaceHolder holder) {}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceAvailable = true;
			try {
				startIfReady();
			}
			catch (IOException e) {
				Log.d(TAG, "Could not start camera source.", e);
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mSurfaceAvailable = false;
		}
	};

	/**********************************************************************
	 * Shutter Callback
	 **********************************************************************/
	CameraSource.ShutterCallback myShutterCallback = new CameraSource.ShutterCallback(){
		@Override
		public void onShutter() { }
	};
	CameraSource.PictureCallback myPictureCallback = new CameraSource.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] bytes) {
			EncodeImage encodeImage = new EncodeImage();
			CropiApiRect rect       = mView.getRect();

			int top    = rect.getTop();
			int bottom = rect.getTop();

			if (rect.getHeight() > 540) {
				bottom -= 150;
			}

			Bitmap source = encodeImage.decodedBitmap(bytes, rect.getWidht(), rect.getHeight());
			Bitmap output = Bitmap.createBitmap(source,
					rect.getLeft(),
					top,
					rect.getRight(),
					bottom);

			String hash = encodeImage.encodeImage(output);

			if (listener != null) {
				listener.onCropBitmap(output);
				listener.onCropHash(hash);
			}
		}
	};
}
package br.com.jadlog.crop.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.FrameLayout;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

public class CropApiCamera extends FrameLayout {
	private static final String TAG = "CropApi";

	private TextureView mTextureView;
	private SurfaceView mSurfaceView;
	private CameraSource mCameraSource;
	private OnCropApiListener listener;
	private CropApiView mView;
	private byte[] byteArray;

	private boolean mStartRequested;
	private boolean mSurfaceAvailable;

	private int previewWidht  = 0;
	private int previewHeight = 0;

	/**
	 * Constructor
	 */
	public CropApiCamera(Context context) {
		super(context);
		init();
	}

	public CropApiCamera(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Metodo de inicializacao
	 */
	private void init() {
		// seta o Background
		setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

//		mTextureView = new TextureView(getContext());
//		mTextureView.setSurfaceTextureListener(callback2);

//		mSurfaceView = new SurfaceView(getContext());
//		mSurfaceView.getHolder().addCallback(callback);

		mView = new CropApiView(getContext());

		//addView(mSurfaceView, layoutParams);
		addView(mTextureView, layoutParams);
		addView(mView, layoutParams);
	}

	public void start(CameraSource cameraSource) throws IOException {
		if (cameraSource == null) {
			stop();
		}

		mCameraSource = cameraSource;

		if (mCameraSource != null) {
			mStartRequested = true;
			startIfReady();
		}
	}

	public void stop() {
		if (mCameraSource != null) {
			mCameraSource.stop();
		}
	}

	public void release() {
		if (mCameraSource != null) {
			mCameraSource.release();
			mCameraSource = null;
		}
	}


	public void takePicture(@NonNull OnCropApiListener listener) {
//		if (mCameraSource != null) {
//			this.listener = listener;
//
//			Bitmap bmp = mTextureView.getBitmap();
//
//			Log.d(TAG, "Bitmap width: " + bmp.getWidth());
//
//			//mCameraSource.takePicture(myShutterCallback, myPictureCallback);
//		}
	}

	private void startIfReady() throws IOException, SecurityException {
		//if (mStartRequested && mSurfaceAvailable) {
//			previewWidht  = getWidth();
//			previewHeight = getHeight();
//			SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
//			surfaceHolder.setFixedSize(previewWidht, previewHeight);

			mCameraSource = mCameraSource.start();

			mStartRequested = false;
		//}
	}

	/**********************************************************************
	 * SurfaceHolder
	 **********************************************************************/
//	SurfaceHolder.Callback2 callback = new SurfaceHolder.Callback2() {
//		@Override
//		public void surfaceRedrawNeeded(SurfaceHolder holder) {}
//
//		@Override
//		public void surfaceCreated(SurfaceHolder holder) {
//			mSurfaceAvailable = true;
//			try {
//				startIfReady();
//			}
//			catch (IOException e) {
//				Log.d(TAG, "Could not start camera source.", e);
//			}
//		}
//
//		@Override
//		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//			Log.d(TAG, "surfaceChanged - width: " + width);
//
//
//		}
//
//		@Override
//		public void surfaceDestroyed(SurfaceHolder holder) {
//			mSurfaceAvailable = false;
//		}
//	};

	/**********************************************************************
	 * Shutter Callback
	 **********************************************************************/
//	CameraSource.ShutterCallback myShutterCallback = new CameraSource.ShutterCallback(){
//		@Override
//		public void onShutter() { }
//	};
//	CameraSource.PictureCallback myPictureCallback = new CameraSource.PictureCallback() {
//		@Override
//		public void onPictureTaken(byte[] data) {
//
//
//
//
//			EncodeImage encodeImage = new EncodeImage();
////			CropiApiRect rect       = mView.getRect();
////			String device           = Build.MODEL.toLowerCase();
////
////			int top    = rect.getTop();
////			int bottom = rect.getBottom();
////
//////			if (device.contains("zenfone")) {
//////				bottom += 200;
//////			}
//////			else if (rect.getHeight() > 540) {
//////				bottom -= 200;
//////			}
//////			else {
//////				bottom -= 20;
//////			}
////
////			Bitmap source = encodeImage.decodedBitmap(bytes, rect.getWidht(), rect.getHeight());
////			Bitmap output = Bitmap.createBitmap(source,
////					rect.getLeft(),
////					rect.getTop(),
////					rect.getRight(),
////					bottom);
////
////			int width  = (output.getWidth() > 860) ? 860 : output.getWidth();
////			Bitmap bmp = Bitmap.createScaledBitmap(output, width, output.getHeight(), false);
////
////			if (listener != null) {
////			    listener.onCropHash(encodeImage.encodeImage(bmp));
////			}
//		}
//	};

	/**********************************************************************
	 * Camera Hardware
	 **********************************************************************/
	private static Camera getCamera(@NonNull CameraSource cameraSource) {
		Field[] declaredFields = CameraSource.class.getDeclaredFields();

		for (Field field : declaredFields) {
			if (field.getType() == Camera.class) {
				field.setAccessible(true);
				try {
					Camera camera = (Camera) field.get(cameraSource);
					if (camera != null) {
						return camera;
					}
					return null;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return null;
	}
}
package br.com.jadlog.crop.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;
import java.util.List;

public class CropApiCameraNew extends FrameLayout {
	private static final String TAG = "CropAPI";

	private Camera mCamera;
	private SurfaceHolder mHolder;

	public static final int NR_CAMERA_BACK  = 0;

	private boolean isPreviewing = false;

	private OnCropApiListener listener;
	private CropApiView mView;

	/**
	 * Constructor
	 */
	public CropApiCameraNew(Context context)                     { super(context);        init(); }
	public CropApiCameraNew(Context context, AttributeSet attrs) { super(context, attrs); init(); }

	/**
	 * Metodo de inicializacao
	 */
	private void init() {
		// seta o Background
		setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		SurfaceView mSurfaceView = new SurfaceView(getContext());

		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(callback);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHolder.setFormat(ImageFormat.NV21);

		mView = new CropApiView(getContext());

		addView(mSurfaceView, layoutParams);
		addView(mView, layoutParams);
	}

	/**
	 * Metodo que instancia a Camera
	 *
	 * @return
	 */
	private Camera getCameraInstance() {
		Camera c = null;
		try {
			// pega a tela
			final Activity tela = (Activity) getContext();

			// pega a orientacao da camera
			final int orientacao = getCameraDisplayOrientation(tela, NR_CAMERA_BACK);

			// abre a camera
			c = Camera.open(NR_CAMERA_BACK);
			c.setDisplayOrientation(orientacao);
		}
		catch (Exception e) {
			Log.d("OPEN CAMERA", e.getMessage());
		}

		// returns null if camera is unavailable
		return c;
	}

	/**
	 * Metodo de release da Camera
	 */
	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			isPreviewing = false;
		}
	}

	/**********************************************************************
	 * SurfaceHolder
	 **********************************************************************/
	SurfaceHolder.Callback2 callback = new SurfaceHolder.Callback2() {
		@Override
		public void surfaceRedrawNeeded(SurfaceHolder holder) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// abre a camera
			mCamera = getCameraInstance();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// verifica
			if (isPreviewing) {
				mCamera.stopPreview();

				// seta o preview
				isPreviewing = false;
			}

			// verifica a camera
			if (mCamera != null) {
				// pega os parametros
				Camera.Parameters p = mCamera.getParameters();

				// otmiza
				List<Camera.Size> sizes = p.getSupportedPreviewSizes();
				Camera.Size optimalSize = getOptimalPreviewSize(sizes, width, height);
				p.setPreviewSize(optimalSize.width, optimalSize.height);
				p.setPictureSize(optimalSize.width, optimalSize.height);

				// seta os parametros
				mCamera.setParameters(p);

				try {
					// seta o Display
					mCamera.setPreviewDisplay(holder);
					mCamera.startPreview();
				}
				catch (IOException e) { }

				// seta o preview
				isPreviewing = true;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) { releaseCamera(); }
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
			Bitmap source = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

			Rect rect       = mView.getRect();
			     rect.right = source.getWidth() - rect.left;

			Bitmap output = Bitmap.createBitmap (source, rect.left, rect.top, rect.right, rect.bottom);

			if (listener != null) { listener.onCrop(output); }
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

	public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null) return null;

		Camera.Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Camera.Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}
}
package br.com.jadlog.crop;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import br.com.jadlog.crop.ui.CropApi;
import br.com.jadlog.crop.ui.OnCropApiListener;

public class CropActivity extends CordovaActivity implements View.OnClickListener {
    private static final String TAG = "CropApi";

    private CropApi cropApi;
    private ImageView btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_activity);

        cropApi = findViewById(R.id.crop);

        btn = findViewById(R.id.takePicture);
        btn.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cropApi != null) { cropApi.onPause(); }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cropApi != null) { cropApi.onDestroy(); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (cropApi != null) {
            cropApi.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn) {
            cropApi.takePicture(new OnCropApiListener() {
                @Override
                public void onCropHash(String hash) {
                    Log.d(TAG, "HASH: " + hash);

//                    Intent intent = new Intent();
//                    intent.putExtra("hash", hash);
//                    setResult(0, intent);
//                    finish();

                    openFinally(hash);
                }
            });
        }
    }

    private void openFinally(String hash) {
        Intent intent = new Intent(this, FinallyActivity_.class);
//        intent.putExtra("HASH", hash);
//
        startActivity(intent);
        finish();
        overridePendingTransition( R.anim.righttoleft, R.anim.stable );
    }
}
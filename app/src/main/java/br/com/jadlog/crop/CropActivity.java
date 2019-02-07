package br.com.jadlog.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import br.com.jadlog.crop.bean.CropHashBean;
import br.com.jadlog.crop.ui.CropApi;
import br.com.jadlog.crop.ui.EncodeImage;
import br.com.jadlog.crop.ui.OnCropApiListener;

public class CropActivity extends CordovaActivity implements View.OnClickListener {
    private CropApi cropApi;
    private ImageView btn;
    private RelativeLayout layoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_activity);

        cropApi = findViewById(R.id.crop);

        layoutView = findViewById(R.id.layoutView);

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
        if (v == btn) { takePicture(); }
    }

    private void takePicture() {
        cropApi.takePicture(new OnCropApiListener() {
            @Override
            public void onCropHash(String hash) {
                CropHashBean cropHashBean = new CropHashBean();
                cropHashBean.setHash(hash);

                Bundle bundle = new Bundle();
                bundle.putSerializable("HASH", cropHashBean);

                final Bitmap bitmap = new EncodeImage().decodeImageBase64(hash);
                Intent intent = new Intent();
                intent.putExtra("HASH_BUNDLE", bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { finish(); return false; }
        else                                  { return super.onKeyDown(keyCode, event); }
    }
}
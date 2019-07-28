package br.com.jadlog.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends CordovaActivity implements View.OnClickListener {
    private Button btnCamera;
    private ImageView imageView;

    private static final int CODE_BACK = 300;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        imageView = findViewById(R.id.imgResult);
        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);
    }

    private void openNewActivity() {
        Intent intent = new Intent(this, CropActivity.class);
        startActivityForResult(intent, CODE_BACK);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == CODE_BACK) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getBundleExtra("HASH_BUNDLE");
                byte[] cropHash = bundle.getByteArray("HASH");
                final Bitmap bitmap = BitmapFactory.decodeByteArray(cropHash, 0, cropHash.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnCamera) { openNewActivity(); }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { finish(); return false; }
        else                                  { return super.onKeyDown(keyCode, event); }
    }
}
package br.com.jadlog.crop;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import br.com.jadlog.crop.ui.CropApi;
import br.com.jadlog.crop.ui.OnCropApiListener;

public class CropActivity extends CordovaActivity implements View.OnClickListener {
    private CropApi cropApi;
    private LinearLayout btn;
    private RelativeLayout layoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_activity);

        cropApi    = findViewById(R.id.crop);
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
            public void onCropBytes(byte[] hash) { confirm(hash); }
        });
    }

    private void setResultData(@NonNull byte[] hash) {
        Bundle bundle = new Bundle();
        bundle.putByteArray("HASH", hash);

        Intent intent = new Intent();
        intent.putExtra("HASH_BUNDLE", bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void confirm(@NonNull final byte[] hash) {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.crop_confirm, null);

        final Bitmap bitmap = BitmapFactory.decodeByteArray(hash, 0, hash.length);

        ((ImageView) view.findViewById(R.id.imgResult)).setImageBitmap(bitmap);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.setNegativeButton(R.string.preview_btn1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialog.setPositiveButton(R.string.preview_btn2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                setResultData(hash);
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { finish(); return false; }
        else                                  { return super.onKeyDown(keyCode, event); }
    }
}
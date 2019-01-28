package br.com.jadlog.crop;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import br.com.jadlog.crop.ui.CropApi;

public class MainActivity extends AppCompatActivity {
    private CropApi cropApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cropApi = findViewById(R.id.crop);

        ImageView btn = findViewById(R.id.takePicture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropApi.takePicture();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (cropApi != null) {
            cropApi.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

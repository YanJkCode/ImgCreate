package com.android.imgcreatedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.android.imgcreate.ImgCreateAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImg = findViewById(R.id.img);
        imgAdapter adapter = new imgAdapter(this, "123");
        adapter.setRatio(5, 4);//按照一定比例去截取
        adapter.getBitMap(new ImgCreateAdapter.OnBitmapBackListener() {
            @Override
            public void onBitmapBack(File imgFile) {
                mImg.setImageBitmap(ImgCreateAdapter.fileToBitmap(imgFile));
            }
        });
    }
}

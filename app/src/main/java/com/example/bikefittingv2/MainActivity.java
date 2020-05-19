package com.example.bikefittingv2;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivity extends AppCompatActivity{

    public static final int SET_PHOTO_REQUEST = 123;

    private Button mBtnMain;
    private ImageView mIpic;
    private BFPhotoSelectUtils mBFPhotoSelectUtils;




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnMain=(Button) findViewById(R.id.btn_main);
        mIpic=(ImageView) findViewById(R.id.imageid1);
//        Intent intent= getIntent();
//        String get_photo = intent.getStringExtra()

        mBtnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MainButtonActivity.class);
                startActivityForResult(intent,SET_PHOTO_REQUEST);
            }
        });

//        init();

    }


    @Override
    //control+o
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == SET_PHOTO_REQUEST) {
//            Log.e("Test","123");
            String geturi = data.getStringExtra("photo_uri");
            Glide.with(MainActivity.this).load(geturi).into(mIpic);
//            Log.e("Test",AAA);

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }



    /**
    private  void init(){
        mBFPhotoSelectUtils = new BFPhotoSelectUtils(this, new BFPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                // Log.e("test",outputUri.toString());
                Glide.with(MainActivity.this).load(outputUri).into(mIpic);
            }
        },false);//裁剪
    }
*/




}


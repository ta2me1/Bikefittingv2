package com.example.bikefittingv2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;


public class MainButtonActivity extends AppCompatActivity {

    private Button mBtnAlbum;
    private BFPhotoSelectUtils mBFPhotoSelectUtils;
    private ImageView mIpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        mBtnAlbum =(Button)findViewById(R.id.btn_album);
        mIpic =(ImageView) findViewById(R.id.imageid1);

        init();
        initListener();
    }


    private  void init(){
        mBFPhotoSelectUtils = new BFPhotoSelectUtils(this, new BFPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
//                Log.e("test",outputUri.toString());
                Intent intent = new Intent();
                intent.putExtra("photo_uri",outputUri.toString());
                setResult(RESULT_OK,intent);
                finish();
//                Glide.with(MainButtonActivity.this).load(outputUri).into(mIpic);
            }
        },false);//裁剪
    }

    private void initListener(){
        mBtnAlbum.setOnClickListener(new View.OnClickListener() {
            //从图库选图
            @Override
            public void onClick(View v) {

//                Log.e("test",toString());
                mBFPhotoSelectUtils.selectPhoto();
//                PermissionGen.needPermission(MainButtonActivity.this,BFPhotoSelectUtils.REQ_SELECT_PHOTO,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}
//                        );
//                Intent intent= new Intent(MainButtonActivity.this,MainActivity.class);
//                intent.putExtra("get_photo","?????");
//                startActivity(intent);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 2、在Activity中的onActivityResult()方法里与LQRPhotoSelectUtils关联
        mBFPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }

    public void showDialog() {
        //创建对话框创建器
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置对话框显示小图标
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        //设置标题
        builder.setTitle("权限申请");
        //设置正文
        builder.setMessage("在设置-应用-虎嗅-权限 中开启相机、存储权限，才能正常使用拍照或图片选择功能");

        //添加确定按钮点击事件
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {//点击完确定后，触发这个事件

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里用来跳到手机设置页，方便用户开启权限
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + MainButtonActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //添加取消按钮点击事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //使用构建器创建出对话框对象
        AlertDialog dialog = builder.create();
        dialog.show();//显示对话框
    }
}





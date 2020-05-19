package com.example.bikefittingv2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;


import java.io.File;

public class BFPhotoSelectUtils {
    public static final int REQ_TAKE_PHOTO = 10001;
    public static final int REQ_SELECT_PHOTO = 10002;
    public static final int REQ_ZOOM_PHOTO= 10003;

    private Activity mActivity;
    //剪切后图片的存放位置
    private String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpg";
    //FileProvider的主机名
    private  String AUTHORITIES = "packageName" + "fileprovider";
    private  boolean mShouldCrop = false;//剪裁（默认不剪裁）
    private Uri mOutputUri = null;
    private File mInputfile;
    private File mOutputFile = null;

    //剪裁图片宽高比
    private  int mAspectX =1;
    private  int mAspectY =1;
    //剪裁图片大小
    private int mOutputX = 800;
    private int mOutputY =480;
    PhotoSelectListener mListener;

    /**
     * 可指定是否在拍照或从图库选取照片后进行裁剪
     * <p>
     * 默认裁剪比例1:1，宽度为800，高度为480
     *
     * @param activity   上下文
     * @param listener   选取图片监听
     * @param shouldCrop 是否裁剪
     */

    public BFPhotoSelectUtils(Activity activity, PhotoSelectListener listener, boolean shouldCrop){
        mActivity = activity;
        mListener = listener;
        mShouldCrop = shouldCrop;
        AUTHORITIES = activity.getPackageName()+".fileprovider";
        imgPath =  generateImgePath();
    }
    /**
     * 从图库选择照片后剪裁
     * @param activity 上下文
     * @param listener 选取图片监听
     * @param aspectX  宽度比例
     * @param aspectY 高度比例
     * @param outputX    宽度
     * @param outputY  高度
     */
    public BFPhotoSelectUtils(Activity activity, PhotoSelectListener listener, int aspectX, int aspectY,int outputX, int outputY){
        this(activity,listener,true);
        mAspectX = aspectX;
        mAspectY = aspectY;
        mOutputX = outputX;
        mOutputY = outputY;
    }

    /**
     * @param authorities FileProvider的主机名
     *
     */
    public void setAuthorities(String authorities) {
        this.AUTHORITIES = authorities;
    }

    /**
     * @param imgPath 图片的存储路径（文件名和后缀名）
     */
    public void setImgPath(String imgPath){
        this.imgPath = imgPath;
    }

    /**
     * 从图库获取
     */
    public void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK,null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        mActivity.startActivityForResult(intent,REQ_SELECT_PHOTO);
    }

    private void zoomPhoto(File inputFile, File outputFIle){
        File parentFile = outputFIle.getParentFile();
        if(!parentFile.exists()){
            parentFile.mkdir();
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setDataAndType(getImageContenUri(mActivity,inputFile),"image/*");
        }else{
            intent.setDataAndType(Uri.fromFile(inputFile),"image/*");
        }
        intent.putExtra("crop","true");

        //设置剪裁图片宽高比
        intent.putExtra("mAspectX",mAspectX);
        intent.putExtra("mAspectY",mAspectY);
        //设置剪裁图片大小
        intent.putExtra("mOutputX",mOutputX);
        intent.putExtra("mOutputY",mOutputY);

        //返回uri
        intent.putExtra("return-data",false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(outputFIle));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        mActivity.startActivityForResult(intent,REQ_ZOOM_PHOTO);
    }

    public void attachToActivityForResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case BFPhotoSelectUtils.REQ_SELECT_PHOTO://图库
                    if(data != null){
                        Uri sourceUri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mActivity.managedQuery(sourceUri,proj,null,null,null);
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String imgPath = cursor.getString(columnIndex);
                        mInputfile = new File(imgPath);

                        if(mShouldCrop) {//剪裁
                            mOutputFile = new File(generateImgePath());
                            mOutputUri = Uri.fromFile(mOutputFile);
                            zoomPhoto(mInputfile, mOutputFile);
                        }else{//不剪裁
                            mOutputUri = Uri.fromFile(mInputfile);
                            if(mListener != null){
                                mListener.onFinish(mInputfile,mOutputUri);
                            }
                        }
                    }
                    break;
            }
        }

    }

    /**
     * ７．０裁剪根据位件路径获取uri
     */
    private Uri getImageContenUri(Context context, File imageFile){
        String filePath = imageFile.getPath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "+?", new String[]{filePath}, null);

        if(cursor != null && cursor.moveToFirst()){
            int id =cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri =  Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri,""+id);
        }else{
            if(imageFile.exists()){
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA,filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
            }else{
                return null;
            }
        }
    }



    /**
     *generate一个图片路径，单文件夹和文件名，文件名位当前毫秒数
     */
    private String generateImgePath(){
        return getExternalStoragePath() + File.separator + String.valueOf(System.currentTimeMillis())+".jpg";
    }

    /**
     * 获取SD下面的目录
     */
    private String getExternalStoragePath(){
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        String ROOT_DIR = "Android/date/" + mActivity.getPackageName();
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        return sb.toString();
    }

    public interface PhotoSelectListener{
        void onFinish(File outputFile, Uri outputUri);
    }

}

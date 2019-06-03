package com.chenhan.huiliaoclient;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import java.io.ByteArrayOutputStream;

public class UtilHandleImg {


    //处理图片，系统版本在4.4以上使用
    @TargetApi(19)
    public static byte[] handleImageOnKitKat(Intent data,Context context,String account) {
        Uri uri = data.getData();
        String imagePath = null;
        // 如果是document类型的Uri，，则通过document id处理
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                // 解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection,context);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null,context);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径
            imagePath = uri.getPath();
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，使用普通方式处理
            imagePath = getImagePath(uri, null,context);
        }

        // 将图片转化为2进制
        return byteHeadView(imagePath,account);
    }

    //处理图片，系统版本在4.4以下使用
    public static byte[] handleImageBeforeKitKat(Intent data,Context context,String account) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null,context);
        // 将图片以二进制形式存进数据库
        return byteHeadView(imagePath,account);
    }

    //获取图片文件的真实路径
    private static String getImagePath(Uri uri, String selection,Context context) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
            }
        }
        return path;
    }


    // 将相册图片转化为byte[]流
    private static byte[] byteHeadView(String imagePath, String account){
        // 图片压缩
        BitmapFactory.Options options = new BitmapFactory.Options();// 解析位图的附加条件
        options.inJustDecodeBounds = true;    // 不去解析位图，只获取位图头文件信息
        Bitmap bitmap= BitmapFactory.decodeFile(imagePath,options);
        //headView.setImageBitmap(bitmap);
        int btWidth = options.outWidth;     // 获取图片的宽度
        int btHeight = options.outHeight;   //获取图片的高度

        int dx = btWidth/100;    // 获取水平方向的缩放比例
        int dy = btHeight/100;    // 获取垂直方向的缩放比例
        int sampleSize = 1; // 设置默认缩放比例
        // 如果是水平方向
        if (dx>dy&&dy>1) {
            sampleSize = dx;
        }

        //如果是垂直方向
        else if (dy>dx&&dx>1) {
            sampleSize = dy;
        }

        else{
            if(dx==dy&&dx>1){
                sampleSize = dx;
            }
        }

        options.inSampleSize = sampleSize;       // 设置图片缩放比例
        options.inJustDecodeBounds = false;     // 真正解析位图
        // 把图片的解析条件options在创建的时候带上
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        ByteArrayOutputStream BAOStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, BAOStream);// (0-100)压缩文件
        byte[] image = BAOStream.toByteArray();
        return image;

    }

    //由Bitmap获得byte[]流
    public static byte[] getBytesByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return outputStream.toByteArray();
    }


    //保存进数据库
    public static void saveInSql(byte[]image ,String account){
        if(MainActivity.databaseManagerInMainActivity.queryIsHeadViewAccountExist(account)){
            MainActivity.databaseManagerInMainActivity.updateHeadView(new HeadViewTable(image,account));
        }
        else {
            MainActivity.databaseManagerInMainActivity.insertHeadView(new HeadViewTable(image, account));
        }
    }

    //从byte流中 获得Bitmap
    public static Bitmap getHeadView(byte [] image){
        // 把图片转为bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        return bitmap;
    }

    //获取图片在本地数据库
    public static Bitmap getHeadViewInSQLite(String headViewAccount){
        HeadViewTable headViewTable = MainActivity.databaseManagerInMainActivity.queryHeadView(headViewAccount);
        // 把图片转为bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(headViewTable.getHeadView(), 0, headViewTable.getHeadView().length);
        return bitmap;
    }

    //发送到服务器
    public static void updateHeadViewToSever(webSocket webSocket,String account){
        byte [] img  = MainActivity.databaseManagerInMainActivity.queryHeadView(account).getHeadView();
        webSocket.sendHeadView(img);
    }



}

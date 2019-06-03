package com.chenhan.huiliaoclient;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentUpdateMySettings extends Fragment implements View.OnClickListener{

    private DialogSelectPhotoItem dialog;
    private Dialog systemPhotoDialog;
    private Bundle bundle = new Bundle();
    private CircleImageView headView;
    private static final int CHOOSE_PHOTO = 13;
    private webSocket listen;
    private MySettingsTable mySettingsUpdatedTable = new MySettingsTable();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_update_mysettings,null);
        bundle = this.getArguments();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.Update_MySettings_Update_Btn).setOnClickListener(this);
        getActivity().findViewById(R.id.Update_MySettings_HeadId_CircleImageView).setOnClickListener(this);
        headView = getActivity().findViewById(R.id.Update_MySettings_HeadId_CircleImageView);
        initFriendSettings();
        final MainActivity mainActivity = (MainActivity) getActivity();
        final webSocket listener = mainActivity.getListener();
        listen = listener;

}

    //初始化个人信息
    private void initFriendSettings(){
        if(MainActivity.databaseManagerInMainActivity.queryMySettings(bundle.getString("account")).getHeadId() == 1) {
            CircleImageView circleImageView = getActivity().findViewById(R.id.Update_MySettings_HeadId_CircleImageView);
            circleImageView.setImageBitmap(UtilHandleImg.getHeadViewInSQLite(bundle.getString("account")));
        }
        else {
            Resources res=getResources();
            Bitmap bmp=BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
            CircleImageView circleImageView = getActivity().findViewById(R.id.Update_MySettings_HeadId_CircleImageView);
            circleImageView.setImageBitmap(bmp);
        }
    MySettingsTable mySettingsTable = MainActivity.databaseManagerInMainActivity.queryMySettings(bundle.getString("account"));
    //账号
        ((TextView)(getActivity().findViewById(R.id.Update_MySettings_Username_TextView))).setText(mySettingsTable.getAccount());
    //昵称
        ((EditText)(getActivity().findViewById(R.id.Update_MySettings_NikeName_EditText))).setText(mySettingsTable.getNikeName());
    //地址
        ((EditText)(getActivity().findViewById(R.id.Update_MySettings_Address_EditText))).setText(mySettingsTable.getAddress());
    //年龄
        if(mySettingsTable.getAge() == -1) {
        ((EditText) (getActivity().findViewById(R.id.Update_MySettings_Age_EditText))).setText("未设置");
    }
        else {
        ((EditText) (getActivity().findViewById(R.id.Update_MySettings_Age_EditText))).setText(String.valueOf(mySettingsTable.getAge()));
    }
    //性别
        if(mySettingsTable.getSex() == 0) {
        ((EditText) (getActivity().findViewById(R.id.Update_MySettings_Sex_EditText))).setText("未设置");
    }
        else if(mySettingsTable.getSex() == 1){
        ((EditText) (getActivity().findViewById(R.id.Update_MySettings_Sex_EditText))).setText("男");
    }
        else if(mySettingsTable.getSex() == 2){
        ((EditText) (getActivity().findViewById(R.id.Update_MySettings_Sex_EditText))).setText("女");
    }
    //电话
        ((EditText)(getActivity().findViewById(R.id.Update_MySettings_PhoneNum_EditText))).setText(String.valueOf(mySettingsTable.getPhoneNum()));
}
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //头像更改
            case R.id.Update_MySettings_HeadId_CircleImageView:
                dialog = new DialogSelectPhotoItem(getContext());
                //显示对话框
                dialog.show();
                dialog.photoTextView.setOnClickListener(this);
                dialog.systemTextView.setOnClickListener(this);
                dialog.backTextView.setOnClickListener(this);
                dialog.getWindow().setGravity(Gravity.BOTTOM|Gravity.CENTER);
                break;

            //更新个人设置按钮
            case R.id.Update_MySettings_Update_Btn:
                //其他设置确认，全部审核完成，保存数据
                if(finalCheck()==false)
                    break;
                //个人设置保存进本地数据库
                saveSettingsUpdate();
                saveMySettingsInSql();
                //头像储存
                Bitmap bitmap = ((BitmapDrawable)headView.getDrawable()).getBitmap();
                mySettingsUpdatedTable.setHeadId(1);
                MainActivity.databaseManagerInMainActivity.updateMySettings(mySettingsUpdatedTable);
                listen.sendUpdateMySettings(mySettingsUpdatedTable);
                byte [] image = UtilHandleImg.getBytesByBitmap(bitmap);
                UtilHandleImg.saveInSql(image,bundle.getString("account"));
                UtilHandleImg.updateHeadViewToSever(listen,bundle.getString("account"));
                Toast.makeText(getContext(),"修改成功",Toast.LENGTH_SHORT);
                FragmentManager fragmentManager=getFragmentManager();
                fragmentManager.popBackStack();
                break;
            //在相册中选择
            case R.id.Update_MySettings_Dialog_Photo_TextView:
                //查看权限
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //无权限，获取权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //有权限，进入相册
                    openAlbum();
                }
                dialog.dismiss();
                break;

            //在系统默认中选择
            case R.id.Update_MySettings_Dialog_System_TextView:
                dialog.dismiss();
                systemPhotoDialog = new Dialog(getContext());
                View viewSelDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_system_headview, null);
                viewSelDialog.findViewById(R.id.HeadView_ImageView1).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView2).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView3).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView4).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView5).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView6).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView7).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView8).setOnClickListener(this);
                viewSelDialog.findViewById(R.id.HeadView_ImageView9).setOnClickListener(this);
                systemPhotoDialog.setContentView(viewSelDialog);
                WindowManager.LayoutParams params_judge = systemPhotoDialog.getWindow().getAttributes();
                systemPhotoDialog.getWindow().setAttributes(params_judge);
                //获取屏幕长 宽
                DisplayMetrics displayMetrics = new DisplayMetrics();
                //设置对话框长宽
                if (displayMetrics.heightPixels != 0 && displayMetrics.widthPixels != 0) {
                    systemPhotoDialog.getWindow().setGravity(Gravity.CENTER);
                    params_judge.width = (int) (displayMetrics.widthPixels * 0.8); // 高度设置为屏幕的0.9
                } else {
                    systemPhotoDialog.getWindow().setGravity(Gravity.CENTER);
                    params_judge.width = 1200;
                }
                //show之前设置返回键无效，触摸屏无效
                systemPhotoDialog.setCancelable(false);dialog.dismiss();
                //显示对话框
                systemPhotoDialog.show();
                break;


            //返回
            case R.id.Update_MySettings_Dialog_Back_TextView:
                dialog.dismiss();
                break;

            case R.id.HeadView_ImageView1:
                recordHeadView(R.id.HeadView_ImageView1);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView2:
                recordHeadView(R.id.HeadView_ImageView2);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView3:
                recordHeadView(R.id.HeadView_ImageView3);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView4:
                recordHeadView(R.id.HeadView_ImageView4);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView5:
                recordHeadView(R.id.HeadView_ImageView5);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView6:
                recordHeadView(R.id.HeadView_ImageView6);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView7:
                recordHeadView(R.id.HeadView_ImageView7);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView8:
                recordHeadView(R.id.HeadView_ImageView8);
                systemPhotoDialog.dismiss();
                break;
            case R.id.HeadView_ImageView9:
                recordHeadView(R.id.HeadView_ImageView9);
                systemPhotoDialog.dismiss();
                break;
        }

    }

    //重写 获取权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    // 用户拒绝授权
                    Toast.makeText(getActivity(), "您已禁止访问相册，请授权", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        // 打开相册
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    //处理返回的相册图片，代号CHOOSE_PHOTO = 13
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                // 判断手机系统版本号,Activity RESULT_OK = -1
                if (resultCode == getActivity().RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 手机系统在4.4及以上的才能使用这个方法处理图片
                        byte[] img = UtilHandleImg.handleImageOnKitKat(data,getContext(),bundle.getString("account"));
                        headView.setImageBitmap(UtilHandleImg.getHeadView(img));
                    } else {
                        // 手机系统在4.4以下的使用这个方法处理图片
                        byte[] img = UtilHandleImg.handleImageBeforeKitKat(data,getContext(),bundle.getString("account"));
                        headView.setImageBitmap(UtilHandleImg.getHeadView(img));
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean checkNikeName(){
        String NikeName = ((EditText)(getActivity().findViewById(R.id.Update_MySettings_NikeName_EditText))).getText().toString();
        if(NikeName.length() < 1){
            Toast.makeText(getContext(),"您输入的昵称长度小于1",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(NikeName.length()>20){
            Toast.makeText(getContext(),"您输入的昵称长度大于20",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkAge(){
        String Age = ((EditText)(getActivity().findViewById(R.id.Update_MySettings_Age_EditText))).getText().toString();
        int age = Integer.parseInt(Age);
        if(age < 0){
            Toast.makeText(getContext(),"您输入的年龄过小",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(age >150){
            Toast.makeText(getContext(),"您输入的年龄过大",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean checkSex(){
        String sex = ((EditText)(getActivity().findViewById(R.id.Update_MySettings_Sex_EditText))).getText().toString();
        if(sex.equals("男")){
            return true;
        }
        else if(sex.equals("女")){
            return false;
        }
        else {
            Toast.makeText(getContext(),"请输入正确的性别，男/女",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean checkAddress(){
        String address = ((EditText)(getActivity().findViewById(R.id.Update_MySettings_Address_EditText))).getText().toString();
        if(address.length()>255){
            Toast.makeText(getContext(),"您输入的地址过长",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }

    }

    private boolean checkPhoneNum(){
        String phoneNum = ((EditText)(getActivity().findViewById(R.id.Update_MySettings_PhoneNum_EditText))).getText().toString();
        if(phoneNum.length() == 11){
            return true;
        }
        else {
            Toast.makeText(getContext(),"请输入正确的手机号",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean finalCheck(){
        if(!checkNikeName())  {
            return false;
        }
        if(!checkSex())  {
            return false;
        }
        if(!checkAge())  {
            return false;
        }
        if(!checkAddress())  {
            return false;
        }
        if(!checkPhoneNum())  {
            return false;
        }
        return true;
    }
    private void saveSettingsUpdate(){

        mySettingsUpdatedTable.setAccount(((TextView)(getActivity().findViewById(R.id.Update_MySettings_Username_TextView))).getText().toString());
        mySettingsUpdatedTable.setAddress(((EditText)(getActivity().findViewById(R.id.Update_MySettings_Address_EditText))).getText().toString());
        mySettingsUpdatedTable.setAge(Integer.parseInt(((EditText)(getActivity().findViewById(R.id.Update_MySettings_Age_EditText))).getText().toString()));
        mySettingsUpdatedTable.setNikeName(((TextView)(getActivity().findViewById(R.id.Update_MySettings_NikeName_EditText))).getText().toString());
        mySettingsUpdatedTable.setPhoneNum((((EditText)(getActivity().findViewById(R.id.Update_MySettings_PhoneNum_EditText))).getText().toString()));
        if(((EditText)(getActivity().findViewById(R.id.Update_MySettings_Sex_EditText))).getText().toString().equals("男"))
            mySettingsUpdatedTable.setSex(1);
        else if(((EditText)(getActivity().findViewById(R.id.Update_MySettings_Sex_EditText))).getText().toString().equals("女"))
            mySettingsUpdatedTable.setSex(2);

    }

    private void recordHeadView(int id){
        Drawable drawable;
        drawable = getResources().getDrawable(R.mipmap.woman_show_layer);
        Bitmap image = ((BitmapDrawable) drawable).getBitmap();
        if(id == R.id.HeadView_ImageView1) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
        }
        else if(id == R.id.HeadView_ImageView2) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_init_7);
        }
        else if(id == R.id.HeadView_ImageView3) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_5);
        }
        else if(id == R.id.HeadView_ImageView4) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_7);
        }
        else if(id == R.id.HeadView_ImageView5) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_8);
        }
        else if(id == R.id.HeadView_ImageView6) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_9);
        }
        else if(id == R.id.HeadView_ImageView7) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_init_4);
        }
        else if(id == R.id.HeadView_ImageView8) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_init_7);
        }
        else if(id == R.id.HeadView_ImageView9) {
            Resources res=getResources();
            image = BitmapFactory.decodeResource(res, R.mipmap.headview_man_6);
        }
        headView.setImageBitmap(image);
    }

    private void saveMySettingsInSql(){
        MainActivity.databaseManagerInMainActivity.updateMySettings(mySettingsUpdatedTable);
    }



}

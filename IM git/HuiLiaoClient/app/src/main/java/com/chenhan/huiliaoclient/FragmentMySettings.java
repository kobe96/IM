package com.chenhan.huiliaoclient;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentMySettings extends Fragment implements View.OnClickListener{

    private Dialog dialog;
    private Bundle bundle = new Bundle();
    private CircleImageView circleImageView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_mysettings,null);
        bundle = this.getArguments();
        //Menu可视
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initFriendSettings();
        getActivity().findViewById(R.id.MySettings_Close_Btn).setOnClickListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fix_my_settings,menu);
        final MenuItem item = menu.findItem(R.id.Menu_Fix_MySettings_Item);
//        getActionView()返回你自定义的菜单布局，设置单击事件的目的是，让其单击时执行onOptionsItemSelected，
//          从而只需统一在onOptionsItemSelected处理即可
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentUpdateMySettings fragmentUpdateMySettings = new FragmentUpdateMySettings();
        fragmentUpdateMySettings.setArguments(bundle);
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.frameLayout,fragmentUpdateMySettings)
                .commit();
        return super.onOptionsItemSelected(item);
    }


    //初始化个人信息
    void initFriendSettings(){
        //获取头像图片
        circleImageView = (CircleImageView) getActivity().findViewById(R.id.MySettings_HeadId_CircleImageView);
        if(MainActivity.databaseManagerInMainActivity.queryMySettings(bundle.getString("account")).getHeadId() == 1) {
            Bitmap bitmap = UtilHandleImg.getHeadViewInSQLite(bundle.getString("account"));
            circleImageView.setImageBitmap(bitmap);
        }
        else {
            Resources res=getResources();
            Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
            circleImageView.setImageBitmap(bmp);
        }
        MySettingsTable mySettingsTable = MainActivity.databaseManagerInMainActivity.queryMySettings(bundle.getString("account"));
        //账号
        ((TextView)(getActivity().findViewById(R.id.MySettings_Username_TextView))).setText(mySettingsTable.getAccount());
        //昵称
        ((TextView)(getActivity().findViewById(R.id.MySettings_NikeName_TextView))).setText(mySettingsTable.getNikeName());
        //地址
        ((TextView)(getActivity().findViewById(R.id.MySettings_Address_TextView))).setText(mySettingsTable.getAddress());
       //年龄
        if(mySettingsTable.getAge() == -1) {
            ((TextView) (getActivity().findViewById(R.id.MySettings_Age_TextView))).setText("未设置");
        }
        else {
            ((TextView) (getActivity().findViewById(R.id.MySettings_Age_TextView))).setText(String.valueOf(mySettingsTable.getAge()));
        }
        //性别
        if(mySettingsTable.getSex() == 0) {
            ((TextView) (getActivity().findViewById(R.id.MySettings_Sex_TextView))).setText("未设置");
        }
        else if(mySettingsTable.getSex() == 1){
            ((TextView) (getActivity().findViewById(R.id.MySettings_Sex_TextView))).setText("男");
        }
        else if(mySettingsTable.getSex() == 2){
            ((TextView) (getActivity().findViewById(R.id.MySettings_Sex_TextView))).setText("女");
        }
        //电话
        ((TextView)(getActivity().findViewById(R.id.MySettings_PhoneNum_TextView))).setText(String.valueOf(mySettingsTable.getPhoneNum()));
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.MySettings_Close_Btn:
                dialog = new Dialog(getContext());
                View viewCloseDialog= LayoutInflater.from(getContext()).inflate(R.layout.dialog_judge_finish,null);
                viewCloseDialog.findViewById(R.id.MySettings_Dialog_Yes_Btn).setOnClickListener(this);
                viewCloseDialog.findViewById(R.id.MySettings_Dialog_No_Btn).setOnClickListener(this);
                dialog.setContentView(viewCloseDialog);
                WindowManager.LayoutParams params_judge = dialog.getWindow().getAttributes();
                dialog.getWindow().setAttributes(params_judge);
                //获取屏幕长 宽
                DisplayMetrics displayMetrics = new DisplayMetrics();
                //设置对话框长宽
                if(displayMetrics.heightPixels != 0 && displayMetrics.widthPixels != 0) {
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    params_judge.width = (int) (displayMetrics.widthPixels * 0.8); // 宽度设置为屏幕的0.8
                }
                else {
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    params_judge.width = 1100;
                }
                //show之前设置返回键无效，触摸屏无效
                dialog.setCancelable(false);
                //显示对话框
                dialog.show();
                break;
            case R.id.MySettings_Dialog_Yes_Btn:
                dialog.dismiss();
                startActivity(new Intent(getActivity(),RegLogin.class));
                getActivity().finish();
                break;
            case R.id.MySettings_Dialog_No_Btn:
                dialog.dismiss();

                break;

        }
    }

}

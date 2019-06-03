package com.chenhan.huiliaoclient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class DialogSelectPhotoItem extends Dialog  {

    Context mContext;
    public TextView photoTextView;
    public TextView systemTextView;
    public TextView backTextView;
    public DialogSelectPhotoItem(Context context) {
        super(context, R.style.MyDialogFromBottom);
        this.mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_picture_postion);

        //设置textView，公有调用使用，在show之后
        photoTextView = findViewById(R.id.Update_MySettings_Dialog_Photo_TextView);
        systemTextView = findViewById(R.id.Update_MySettings_Dialog_System_TextView);
        backTextView = findViewById(R.id.Update_MySettings_Dialog_Back_TextView);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity= Gravity.BOTTOM;
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }


}

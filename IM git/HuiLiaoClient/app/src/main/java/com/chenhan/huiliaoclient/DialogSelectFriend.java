package com.chenhan.huiliaoclient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogSelectFriend extends Dialog {

    public Button button_send;
    public Button button_back;
    private boolean [] isOnclick;
    private Activity activity;
    private String[] mAccount;
    private Bitmap [] mBitmaps;
    private Bundle bundle;
    public static boolean isOk;
    private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    Context mContext;
    public ListView listView;
    public Button button;

    public DialogSelectFriend(Context context, Bundle bundle, Activity activity) {
        super(context, R.style.MyDialog);
        this.bundle = bundle;
        this.mContext=context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_to);
        listView = findViewById(R.id.Dialog_Send_To_ListView);
        button_send = findViewById(R.id.Dialog_Send_To_Btn);
        button_back = findViewById(R.id.Dialog_Send_To_Back_Btn);
        initDialog();
        //设置textView，公有调用使用，在show之后

        button = findViewById(R.id.Dialog_Send_To_RadioBtn);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final RadioButton radioButton = view.findViewById(R.id.Dialog_Send_To_RadioBtn);
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isOnclick[position]) {
                            radioButton.setChecked(false);
                            isOnclick[position] = false;
                        }
                        else {
                            radioButton.setChecked(true);
                            isOnclick[position] = true;
                        }
                    }
                });

                if(radioButton.isChecked()) {
                    radioButton.setChecked(false);
                    isOnclick[position] = false;
                }
                else {
                    radioButton.setChecked(true);
                    isOnclick[position] = true;
                }
            }
        });
    }



    private void initDialog(){
        mAccount = MainActivity.databaseManagerInMainActivity.queryFriendAccount(bundle.getString("account"));
        mBitmaps = new Bitmap[mAccount.length];
        isOnclick = new boolean[mAccount.length];

        for (int i = 0; i < mAccount.length; i++) {
            //添加头像控件
            if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                mBitmaps[i] = UtilHandleImg.getHeadView(headViewTable.getHeadView());
            }
            else{
                Resources res = activity.getResources();
                Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                mBitmaps[i] = bmp;
            }

            if(!mAccount[i].equals(bundle.getString("account"))) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("account", mAccount[i]);
                item.put("headView", mBitmaps[i]);
                mData.add(item);
            }
            isOnclick[i] = false;
        }
        /**
         * SimpleAdapter的五个参数：
         * Context:上下文
         * data：数据源（List<Map<String,?>>data）一个有Map组成的集合
         *       每一个Map都会对应ListView列表中的一行
         *       每一个Map(键-值对)中的键必须包含所有在from参数中所指定的键
         *resource: ListView中每一项的布局文件ID
         *from：Map中的键名
         *to：绑定item.xml文件中的view控件ID，与from，Map的键形成对应
         */
        SimpleAdapter adapter = new SimpleAdapter(activity, mData, R.layout.assembly_fragment_find_the_truth_select_send_to,
                new String[]{"account", "headView"}, new int[]{R.id.Dialog_Send_To_Name_TextView, R.id.Dialog_Send_To_ImageView});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                // TODO Auto-generated method stub
                if(view instanceof ImageView && data instanceof Bitmap){
                    ImageView iv = (ImageView)view;
                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }else{
                    return false; }
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity= Gravity.CENTER;
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }

    public List<String> getAccount(){
        int count = getCount();
            List<String> account = new ArrayList<String>();
            for(int i = 0;i< mAccount.length;i++) {
                if (isOnclick[i]) {
                    account.add(mAccount[i]);
                }
            }
            return account;
    }

    public int getCount(){
        int count = 0;
        for(int i = 0;i<mAccount.length;i++){
            if(isOnclick[i])
                count++;
        }
        return count;
    }

}

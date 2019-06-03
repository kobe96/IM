package com.chenhan.huiliaoclient;
/*
 * 工程名:    慧聊
 * 创建日期: 2019.3.1
 * 作者:     陈涵
 * */

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public final static String IP = "172.20.10.3:8080";
    //记录当前的Fragment是哪张
    private int lastIndex;
    //Fragment列表
    List<Fragment> mFragments;
    //声明DatabaseHelper
    DatabaseHelper databaseHelper;
    //账号
    public static String account;
    //界面的传值包
    private Bundle bundle;
    //webSocket
    public static webSocket listener;
    //静态变量供非activity调用
    public static DatabaseManager databaseManagerInMainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        bundle = intent.getExtras();
        account = bundle.getString("account");
        setContentView(R.layout.activity_main);
        //建立本地数据库
        databaseHelper = new DatabaseHelper(MainActivity.this);
        databaseManagerInMainActivity = new DatabaseManager(this);
        connect();
        waitForMessage();
        initData();
//        initFriendList();
//        initOfflineMessageData();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.navigation).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void finish() {
        super.finish();
        listener.sendCloseMessage(1000);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /*******************************************UI线程*************************************************/


    private void waitForMessage() {

        while(listener.isInitDataFinish())
        {

        }
    }

//     初始化Fragment,创建三张Fragment
    public void initData() {
        mFragments = new ArrayList<>();
        FragmentMessage fragmentMessage = new FragmentMessage();
        fragmentMessage.setArguments(bundle);
        mFragments.add(fragmentMessage);
        FragmentFriendList fragmentFriendList = new FragmentFriendList();
        fragmentFriendList.setArguments(bundle);
        mFragments.add(fragmentFriendList);
        FragmentFind fragmentFind = new FragmentFind();
        fragmentFind.setArguments(bundle);
        mFragments.add(fragmentFind);
        // 初始化展示MessageFragment
        setFragmentPosition(0);
    }

//    实现跳转到指定的Fragment
    public void setFragmentPosition(int position) {
//        创建FragmentManager的Transaction对象
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        指定跳转的对象
        Fragment currentFragment = mFragments.get(position);
//        指定当前对象
        Fragment lastFragment = mFragments.get(lastIndex);
        lastIndex = position;
//        隐藏当前对象
        ft.hide(lastFragment);
//        添加过，则删除掉
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.frameLayout, currentFragment);
        }
//        展示当前Fragment
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }

//    实现三个按钮的转换功能
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_recent_message:
                    setTitle("消息");
                    setFragmentPosition(0);
                    break;
                case R.id.navigation_friend:
                    //切换
                    setTitle("联系人");
                    setFragmentPosition(1);
                    break;
                case R.id.navigation_settings:
                    //切换
                    setTitle("发现");
                    setFragmentPosition(2);
                    break;
            }
            return true;
        }
    };

    private void connect() {
        listener = new webSocket(bundle.getString("account"));
        Request request = new Request.Builder()
                .url("ws://" +IP + "/recSendMsgWebSocket")
//                .url("ws://172.26.93.186:8080/recSendMsgWebSocket")
//                .url("ws://echo.websocket.org")
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    //获得webSocket操作
    public webSocket getListener(){
        return listener;
    }
    //

}

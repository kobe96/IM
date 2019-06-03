package com.chenhan.huiliaoclient;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class FragmentMessage extends Fragment {

    private ArrayList<Map<String, Object>> messageContentArrayList = new ArrayList<Map<String, Object>>();
    private Bundle bundle;
    private Timer timer;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //导航栏可视
        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);
        //Menu可视
        setHasOptionsMenu(true);
        bundle = this.getArguments();
        View v = inflater.inflate(R.layout.fragment_message, null);
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        initMessageData(listView);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("消息");
        refreshList(bundle.getString("account"));
        mListView = getView().findViewById(android.R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //第position行
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendAccount = ((TextView)view.findViewById(R.id.text1)).getText().toString();
                Bundle doubleContent = new Bundle();
                doubleContent.putString("myAccount",bundle.getString("account"));
                doubleContent.putString("friendAccount",friendAccount);
                FragmentDialog fragmentDialog= new FragmentDialog();
                fragmentDialog.setArguments(doubleContent);
                getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frameLayout,fragmentDialog)
                        .commit();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroyView() {
        //清除ListView
        super.onDestroyView();
        messageContentArrayList.clear();
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), messageContentArrayList, R.layout.assembly_fragment_message_listview_content,
                new String[]{"name", "message", "heaView"}, new int[]{R.id.text1, R.id.text2, R.id.list_View_Pic});
        ListView listView = getView().findViewById(android.R.id.list);
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
        //timer线程的销毁
        if(timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_friend,menu);
        final MenuItem item = menu.findItem(R.id.Menu_Add_Friend_Item);
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
        getActivity().findViewById(R.id.navigation).setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), AddFriendActivity.class);
        intent.putExtras(this.getArguments());
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }


    private void initMessageData(ListView listView) {
        String [] mAccount;
        mAccount = MainActivity.databaseManagerInMainActivity.queryMessageAccount(bundle.getString("account"));
        if(mAccount == null) {
            System.out.println("没有"+"账户与本账号有消息");
            return;
        }
        System.out.println("共有"+ mAccount.length+"个账户与本账号有消息");
        MsgListContent [] msgListContent = new MsgListContent[mAccount.length];
        SimpleAdapter adapter;
        for (int i = 0; i < mAccount.length; i++) {
            if(MainActivity.databaseManagerInMainActivity.queryMessageReceiveType(bundle.getString("account"),mAccount[i]) == 0) {
                Bitmap headView;
                if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                    HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                    headView = UtilHandleImg.getHeadView(headViewTable.getHeadView());
                }
                else{
                    Resources res=getResources();
                    Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                    headView = bmp;
                }
                msgListContent[i] = new MsgListContent(headView, mAccount[i], "有未读的离线消息", MainActivity.databaseManagerInMainActivity.queryMessageNum(bundle.getString("account"), mAccount[i]));
            }
            else if(MainActivity.databaseManagerInMainActivity.queryMessageReceiveType(bundle.getString("account"),mAccount[i]) == 1) {
                Bitmap headView;
                if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                    HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                    headView = UtilHandleImg.getHeadView(headViewTable.getHeadView());
                }
                else{
                    Resources res=getResources();
                    Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                    headView = bmp;
                }
                msgListContent[i] = new MsgListContent(headView, mAccount[i], "有未读的在线消息", MainActivity.databaseManagerInMainActivity.queryMessageNum(bundle.getString("account"), mAccount[i]));
            }
            else if(MainActivity.databaseManagerInMainActivity.queryMessageReceiveType(bundle.getString("account"),mAccount[i]) == 2) {
                Bitmap headView;
                if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                    HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                    headView = UtilHandleImg.getHeadView(headViewTable.getHeadView());
                }
                else{
                    Resources res=getResources();
                    Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                    headView = bmp;
                }
                msgListContent[i] = new MsgListContent(headView, mAccount[i], "暂无未读消息", MainActivity.databaseManagerInMainActivity.queryMessageNum(bundle.getString("account"), mAccount[i]));
            }
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("account", msgListContent[i].getFriendAccount());
                item.put("message", msgListContent[i].getMessageContent());
                item.put("headView", msgListContent[i].getHeadView());
                messageContentArrayList.add(item);
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
        adapter = new SimpleAdapter(getActivity(), messageContentArrayList, R.layout.assembly_fragment_message_listview_content,
                new String[]{"account", "message", "headView"}, new int[]{R.id.text1, R.id.text2, R.id.list_View_Pic});
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


    public void refreshList(final String myAccount){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                AsyncTask asyncTaskInDialog =  new AsyncTask<String,Void,String>() {

                    protected String doInBackground(String... strings) {
                        String [] mAccount;
                        mAccount = MainActivity.databaseManagerInMainActivity.queryMessageAccount(strings[0]);
                        if(mAccount == null)
                            return "Not_Need";
                        for(int i = 0;i<mAccount.length;i++){
                            if(MainActivity.databaseManagerInMainActivity.queryMessageReceiveType(strings[0],mAccount[i])!= 2)
                                return "Need_Update";
                        }
                       return "Not_Need";
                    }
                    //以下为沟通UI线程的操作方式
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }
                    //doInBackGround返回值
                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if(s.equals("Need_Update")){
                            messageContentArrayList.clear();
                            String [] mAccount;
                            mAccount = MainActivity.databaseManagerInMainActivity.queryMessageAccount(bundle.getString("account"));
                            MsgListContent [] msgListContent = new MsgListContent[mAccount.length];
                            for(int i = 0;i<mAccount.length;i++){
                                if(MainActivity.databaseManagerInMainActivity.queryMessageReceiveType(bundle.getString("account"),mAccount[i]) == 0) {
                                    Bitmap headView;
                                    if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                                        HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                                        headView = UtilHandleImg.getHeadView(headViewTable.getHeadView());
                                    }
                                    else{
                                        Resources res=getResources();
                                        Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                                        headView = bmp;
                                    }
                                    msgListContent[i] = new MsgListContent(headView, mAccount[i], "有未读的离线消息", MainActivity.databaseManagerInMainActivity.queryMessageNum(bundle.getString("account"), mAccount[i]));
                                }
                                else if(MainActivity.databaseManagerInMainActivity.queryMessageReceiveType(bundle.getString("account"),mAccount[i]) == 1) {
                                    Bitmap headView;
                                    if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                                        HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                                        headView = UtilHandleImg.getHeadView(headViewTable.getHeadView());
                                    }
                                    else{
                                        Resources res=getResources();
                                        Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                                        headView = bmp;
                                    }
                                    msgListContent[i] = new MsgListContent(headView, mAccount[i], "有未读的在线消息", MainActivity.databaseManagerInMainActivity.queryMessageNum(bundle.getString("account"), mAccount[i]));
                                }
                                else if(MainActivity.databaseManagerInMainActivity.queryMessageReceiveType(bundle.getString("account"),mAccount[i]) == 2) {
                                    Bitmap headView;
                                    if(MainActivity.databaseManagerInMainActivity.queryMySettings(mAccount[i]).getHeadId() == 1){
                                        HeadViewTable headViewTable =  MainActivity.databaseManagerInMainActivity.queryHeadView(mAccount[i]);
                                        headView = UtilHandleImg.getHeadView(headViewTable.getHeadView());
                                    }
                                    else{
                                        Resources res=getResources();
                                        Bitmap bmp= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
                                        headView = bmp;
                                    }
                                    msgListContent[i] = new MsgListContent(headView, mAccount[i], "暂无未读消息", MainActivity.databaseManagerInMainActivity.queryMessageNum(bundle.getString("account"), mAccount[i]));
                                }
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("account", msgListContent[i].getFriendAccount());
                                item.put("message", msgListContent[i].getMessageContent());
                                item.put("headView", msgListContent[i].getHeadView());
                                messageContentArrayList.add(item);
                                }
                            SimpleAdapter adapter;
                            adapter = new SimpleAdapter(getActivity(), messageContentArrayList, R.layout.assembly_fragment_message_listview_content,
                                    new String[]{"account", "message", "headView"}, new int[]{R.id.text1, R.id.text2, R.id.list_View_Pic});
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
                            adapter.notifyDataSetChanged();
                            mListView.setAdapter(adapter);
                        }
                    }
                }.execute(myAccount);
            }
        },0,1000);
    }

}


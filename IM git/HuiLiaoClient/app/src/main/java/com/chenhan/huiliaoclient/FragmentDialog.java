package com.chenhan.huiliaoclient;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentDialog extends Fragment {

    private int messageCount = 0;
    private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private Bundle bundle;
    private String friendAccount;
    private String myAccount;
    private Timer timer;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    private  ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, null);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivity mainActivity = (MainActivity) getActivity();
        final webSocket listener = mainActivity.getListener();
        initMsg();
        timerRefresh();
        getActivity().setTitle(friendAccount);
        getActivity().findViewById(R.id.Message_Content_EditText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setSelection(adapter.getCount()-1);
            }
        });
        getActivity().findViewById(R.id.Message_Send_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getEditTextMessage()!=null&&getEditTextMessage().length()<=1000) {
                    //发送，webSocket
                    listener.sendMessage(friendAccount, getEditTextMessage());
                    //显示在列表上,内容分别为头像，消息，消息类型
                    Msg sendMessage = new Msg(getEditTextMessage(), Msg.SENT,getBitmap(bundle.getString("myAccount")));
                    //适配器加入新消息
                    adapter.add(sendMessage);
                    //适配器更新显示
                    adapter.notifyDataSetChanged();
                    //显示到最下方
                    listView.setSelection(adapter.getCount()-1);
                    //存入数据库
                    MainActivity.databaseManagerInMainActivity = new DatabaseManager(getActivity());
                    MainActivity.databaseManagerInMainActivity.insertMessage(new MessageTable(MainActivity.databaseManagerInMainActivity.queryMessageCount()+1,getEditTextMessage(),bundle.getString("myAccount"),friendAccount,1,2));
                    clearEditTextMessage();
                    messageCount++;
                }
                else if(getEditTextMessage() == null)
                    Toast.makeText(getActivity(),"不能输入空字符",Toast.LENGTH_SHORT);
                else if(getEditTextMessage().length()>1000)
                    Toast.makeText(getActivity(),"请输入小于1000的字符",Toast.LENGTH_SHORT);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //timer线程的销毁
        if(timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
        MainActivity.databaseManagerInMainActivity.updateMessageReceiveType(bundle.getString("myAccount"),bundle.getString("friendAccount"));
    }

    private void clearEditTextMessage(){
        ((EditText)getActivity().findViewById(R.id.Message_Content_EditText)).setText("");
    }

    private String getEditTextMessage(){
        String s = ((EditText)getActivity().findViewById(R.id.Message_Content_EditText)).getText().toString();
        if(s!=null&&!s.equals(""))
            return s;
        else
            return null;
    }

    //初始化聊天者的之前消息，完成基本的赋值语句
    private void initMsg() {
        bundle = getArguments();
        friendAccount = getArguments().getString("friendAccount");
        myAccount = getArguments().getString("myAccount");
        MessageTable [] messageTable = MainActivity.databaseManagerInMainActivity.queryMessage(bundle.getString("myAccount"),bundle.getString("friendAccount"));
//        MainActivity.databaseManagerInMainActivity.updateMessageReceiveType(bundle.getString("myAccount"),bundle.getString("friendAccount"));
        Msg[] messageContent = new Msg[messageTable.length];
        for(int i = 0 ;i <messageTable.length;i++) {
            if(messageTable[i].getMessageType() == Msg.SENT)
            messageContent[i] = new Msg(messageTable[i].getMessageContent(), messageTable[i].getMessageType(),getBitmap(bundle.getString("myAccount")));
            else
            messageContent[i] = new Msg(messageTable[i].getMessageContent(), messageTable[i].getMessageType(),getBitmap(bundle.getString("friendAccount")));
            msgList.add(messageContent[i]);
            messageCount++;
        }
        //参数说明 activity 活动，item项目布局，填充的类数组
        adapter = new MsgAdapter(getActivity(), R.layout.assembly_fragment_dialog_message_list_content, msgList);
        listView = getActivity().findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setSelection(adapter.getCount()-1);

    }

    public void timerRefresh() {
        timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
            AsyncTask asyncTaskInDialog = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    MessageTable [] messageTable = MainActivity.databaseManagerInMainActivity.queryMessage(bundle.getString("myAccount"),bundle.getString("friendAccount"));
                    if(messageTable.length > messageCount)
                        return true;
                    else
                        return false;
                }
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if((boolean)o == true) {
                        MessageTable[] messageTable = MainActivity.databaseManagerInMainActivity.queryMessage(bundle.getString("myAccount"), bundle.getString("friendAccount"));
                        for (int i = messageCount; i < messageTable.length; i++) {
                            Msg messageContent;
                            if(messageTable[i].getMessageType() == Msg.SENT)
                                messageContent= new Msg(messageTable[i].getMessageContent(), messageTable[i].getMessageType(),getBitmap(myAccount));
                            else
                                messageContent = new Msg(messageTable[i].getMessageContent(), messageTable[i].getMessageType(),getBitmap(friendAccount));
                            adapter.add(messageContent);
                            adapter.notifyDataSetChanged();
                            listView.setSelection(adapter.getCount() - 1);
                            messageCount++;
                        }
                    }
                }
            }.execute();
            }
        },0,1000);
    }

    private Bitmap getBitmap(String account){
        Bitmap bitmap;
        if(MainActivity.databaseManagerInMainActivity.queryMySettings(account).getHeadId() == 1) {
            bitmap = UtilHandleImg.getHeadView(MainActivity.databaseManagerInMainActivity.queryHeadView(account).getHeadView());
        }
        else{
            Resources res=getResources();
            bitmap= BitmapFactory.decodeResource(res, R.mipmap.headview_man_4);
        }
        return bitmap;
    }


}

package com.chenhan.huiliaoclient;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class AddFriendActivity extends AppCompatActivity {


    private Bundle bundle;
    private AsyncTask asyncTask;
    private String[] mAccount, mName;
    private int[] mPic;
    private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initIntentData();
        //按钮搜索的事件
        findViewById(R.id.Search_Friend_ImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!((EditText)findViewById(R.id.Search_Friend_EditText)).getText().equals(""))
                    searchFriendSync(reResearchAccount());
                    else
                        Toast.makeText(AddFriendActivity.this,"输入的账号不能为空",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //ListView的事件
        ((ListView)findViewById(R.id.Research_Friend_ListView)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

           /* X, Y两个listview，X里有1,2,3,4这4个item，Y里有a,b,c,d这4个item。如果你点了b这个item。如下：
            arg0相当于listview Y适配器的一个指针，可以通过它来获得Y里装着的一切东西，再通俗点就是说告诉你，你点的是Y，不是X
            arg1是你点的b这个view的句柄，就是你可以用这个view，来获得b里的控件的id后操作控件
            arg2是b在Y适配器里的位置（生成listview时，适配器一个一个的做item，然后把他们按顺序排好队，在放到listview里，意思就是这个b是第position号做好的）
            arg3是b在listview Y里的第几行的位置（很明显是第2行），大部分时候position和id的值是一样的，如果需要的话，你可以自己加个log把position和id都弄出来在logcat里瞅瞅
*/
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = ((TextView)view.findViewById(R.id.text1)).getText().toString();
                ((TextView)findViewById(R.id.Search_Friend_TextView)).setText(text);
            }


        });

        //添加好友按钮的事件
        findViewById(R.id.Add_Friend_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendAccount = ((TextView)findViewById(R.id.Search_Friend_TextView)).getText().toString();
                if(!friendAccount.equals("未选择")&&!friendAccount.equals("")&&friendAccount!=null)
                    try {
                        addFriendSync(bundle.getString("account"),friendAccount,friendAccount);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }

    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
//        intent.putExtras(bundle);
        finish();
        return true;
    }

    //初始化数据
    private void initIntentData() {
        Intent intent = getIntent();
        bundle = intent.getExtras();
    }

    //读取EditText数据
    private String reResearchAccount() {
        return ((EditText) findViewById(R.id.Search_Friend_EditText)).getText().toString();
    }


    @SuppressLint("StaticFieldLeak")
    protected void searchFriendSync(final String account) throws IOException, JSONException {
        //建立新的线程，完成URL的工作
        asyncTask = new AsyncTask<String, Void, String>() {
            //后台线程的操作
            protected String doInBackground(String... strings) {
                //打包json格式文件准备发送服务器
                JSONObject jsonObject = new JSONObject();
                String receiveData = null;
                String result = "";
                try {
                    jsonObject.put("account", strings[0]);
                } catch (JSONException e) {
                    System.out.println("JSON Text Exception\n");
                    e.printStackTrace();
                }
                //建立连接，并发送给服务器
                String strUrl = "http://"+ MainActivity.IP + "/searchFriendJsonData";
//                String strUrl = "http://172.26.93.186:8080/searchFriendJsonData";
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(strUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);//输入采用字节流
                    urlConnection.setDoOutput(true);//输出采用字节流
                    urlConnection.setRequestMethod("POST");//设置为POST向服务器发送数据
                } catch (IOException e) {
                    System.out.println("urlConnection Set Information Exception\n");
                    e.printStackTrace();
                }
                urlConnection.setUseCaches(false);//设置缓存
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                //设置格式为json格式文件
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //设置data输出流，写入字节流
                try {
                    OutputStream outputStream = urlConnection.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    String contentData = jsonObject.toString();
                    dataOutputStream.writeBytes(contentData);
                    //传送数据
                    dataOutputStream.flush();
                    dataOutputStream.close();
                } catch (IOException e) {
                    System.out.println("urlConnection Connection Exception\n");
                    e.printStackTrace();
                }
                //发送完毕，接收应答
                try {
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //如果成功，则将返回的消息，存入BufferedReader中
                        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        while ((receiveData = bufferedReader.readLine()) != null) {
                            result += receiveData + "\n";
                        }
                        inputStreamReader.close();
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    System.out.println("urlConnection Back Information Exception\n");
                    e.printStackTrace();
                }
                return result;
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



                mAccount = new String[100];
                mPic = new int[100];
                mName = new String[100];
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    //存在此人
                    if (jsonObject.getString("message").equals("Succeed")) {
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("accounts"));
                        System.out.println(jsonArray.length());
                        for (int i = 0; i < jsonArray.length()&&i<100; i++) {
                            jsonObject = (JSONObject) jsonArray.get(i);
                            mAccount[i] = new String();
                            mAccount[i] = jsonObject.getString("account");
                            mName[i] = new String();
                            mName[i] = "";
                            //头像随机
                            Random r = new Random();
                            if (r.nextInt() % 2 == 0)
                                mPic[i] = R.mipmap.woman_show_round;
                            else
                                mPic[i] = R.mipmap.man_show_round;
                        }
                        for (int i = 0; i < mName.length&&mAccount[i]!=null; i++) {
                            if(mAccount[i].equals(""))
                                break;
                            Map<String, Object> item = new HashMap<String, Object>();
                            item.put("account", mAccount[i]);
                            item.put("num", mName[i]);
                            item.put("pic", mPic[i]);
                            mData.add(item);
                        }
                        SimpleAdapter adapter = new SimpleAdapter(AddFriendActivity.this, mData, R.layout.assembly_fragment_message_listview_content,
                                new String[]{"account", "num", "pic"}, new int[]{R.id.text1, R.id.text2, R.id.list_View_Pic});
                        ((ListView)findViewById(R.id.Research_Friend_ListView)).setAdapter(adapter);
                    } else {
                        //不存在此人
                        ((TextView) findViewById(R.id.Search_Friend_TextView)).setText("");
                        Toast.makeText(AddFriendActivity.this, "不存在此用户，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            //对应BackGround strings[0],[1]参数为account,password
        }.execute(account);
    }

    @SuppressLint("StaticFieldLeak")
    protected void addFriendSync(final String myAccount,final String friendAccount,final String reName) throws IOException, JSONException {
        //建立新的线程，完成URL的工作
        asyncTask = new AsyncTask<String, Void, String>() {
            //后台线程的操作
            protected String doInBackground(String... strings) {
                //打包json格式文件准备发送服务器
                JSONObject jsonObject = new JSONObject();
                String receiveData = null;
                String result = "";
                try {
                    jsonObject.put("account", strings[0]);
                    jsonObject.put("friendAccount",strings[1]);
                    jsonObject.put("reName",strings[2]);
                } catch (JSONException e) {
                    System.out.println("JSON Text Exception\n");
                    e.printStackTrace();
                }
                //建立连接，并发送给服务器
                String strUrl = "http://"+MainActivity.IP+"/insertFriendJsonData";
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(strUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true);//输入采用字节流
                    urlConnection.setDoOutput(true);//输出采用字节流
                    urlConnection.setRequestMethod("POST");//设置为POST向服务器发送数据
                } catch (IOException e) {
                    System.out.println("urlConnection Set Information Exception\n");
                    e.printStackTrace();
                }
                urlConnection.setUseCaches(false);//设置缓存
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                //设置格式为json格式文件
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //设置data输出流，写入字节流
                try {
                    OutputStream outputStream = urlConnection.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    String contentData = jsonObject.toString();
                    dataOutputStream.writeBytes(contentData);
                    //传送数据
                    dataOutputStream.flush();
                    dataOutputStream.close();
                } catch (IOException e) {
                    System.out.println("urlConnection Connection Exception\n");
                    e.printStackTrace();
                }
                //发送完毕，接收应答
                try {
                    if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //如果成功，则将返回的消息，存入BufferedReader中
                        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        while ((receiveData = bufferedReader.readLine()) != null) {
                            result += receiveData + "\n";
                        }
                        inputStreamReader.close();
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    System.out.println("urlConnection Back Information Exception\n");
                    e.printStackTrace();
                }
                return result;
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
                MainActivity.listener.sendInitMySettings();
                if(s.equals("friend_Exist"+"\n")) {
                    Toast.makeText(AddFriendActivity.this,"朋友已经存在无需添加",Toast.LENGTH_SHORT).show();
                    clearListView();
                }
                else if(s.equals("insert_Friend_Succeed"+"\n")){
                    FriendListTable [] friends = MainActivity.databaseManagerInMainActivity.queryFriend();
                    int lengthFriend = friends.length;
                    MainActivity.databaseManagerInMainActivity.insertFriend(new FriendListTable(myAccount,++lengthFriend,friendAccount,reName));
                    MainActivity.databaseManagerInMainActivity.insertMySettings(new MySettingsTable(
                           0,
                        friendAccount,
                        reName,
                        0,
                        "",
                            "",
                        0
                    ));
                    Toast.makeText(AddFriendActivity.this,"已经加入好友列表",Toast.LENGTH_SHORT).show();
                    clearListView();
                }
                else if(s.equals("account_Not_Exist"+"\n")){
                    Toast.makeText(AddFriendActivity.this,"账户不存在",Toast.LENGTH_SHORT).show();
                    clearListView();
                }

            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            private void clearListView(){
                mData.clear();
                SimpleAdapter adapter = new SimpleAdapter(AddFriendActivity.this, mData, R.layout.assembly_fragment_message_listview_content,
                        new String[]{"account", "num", "pic"}, new int[]{R.id.text1, R.id.text2, R.id.list_View_Pic});
                ListView listView = findViewById(R.id.Research_Friend_ListView);
                listView.setAdapter(adapter);
                ((TextView)findViewById(R.id.Search_Friend_TextView)).setText("");
            }
            //对应BackGround strings[0]参数为account
        }.execute(myAccount,friendAccount,reName);
    }

}

package com.chenhan.huiliaoclient;
/*
 * 工程名:    慧聊
 * 创建日期: 2019.3.1
 * 作者:     陈涵
 * */
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegLogin extends AppCompatActivity implements View.OnClickListener{

    private AsyncTask asyncTask;
    private Dialog dialog;
    private EditText editText;

    /*********************************************UI线程活动**************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_login);
//      设置账号密码的图片大小，初始化标题
//      3.13,通过XML，解决了适配的问题
//      setAccPswSize();
        setTitle("登录注册慧聊");
//      注册按钮的功能实现
        findViewById(R.id.RegLogin_Reg_Btn).setOnClickListener(this);
        findViewById(R.id.RegLogin_Login_Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkAccPassword()) {
                    try {
                        loginSync(getAccount(), getPassword());
                    } catch (IOException e) {
                        System.out.println("url IOException\n");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        System.out.println("JSON Text Exception\n");
                        e.printStackTrace();
                    }
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
        if(asyncTask != null) {
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }


    /****************************************活动方法**********************************************/

//    账号密码图片的显示
    protected void setAccPswSize(){
        //账号图片的显示
        EditText editTextAccount = (EditText) findViewById(R.id.RegLogin_Account_EditText);
        Drawable drawableAccount = getResources().getDrawable(R.mipmap.account);
        drawableAccount.setBounds(0, 0, 160, 160);//第一0是距左边距离，第二0是距上边距离，80分别是长宽
        editTextAccount.setCompoundDrawables(drawableAccount, null, null, null);//只放左边
        //密码图片的显示
        EditText editTextPassword = (EditText) findViewById(R.id.RegLogin_Password_EditText);
        Drawable drawablePassword = getResources().getDrawable(R.mipmap.password);
        drawablePassword.setBounds(0, 0, 160, 160);//第一0是距左边距离，第二0是距上边距离，80分别是长宽
        editTextPassword.setCompoundDrawables(drawablePassword, null, null, null);//只放左边
    }

//    前端控制字符的正确
    protected boolean chkAccPassword(){
        if(((EditText) findViewById(R.id.RegLogin_Account_EditText)).getText().toString().length() >= 20){
            Toast.makeText(RegLogin.this,"失败，请输入的账号字符少于20",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.RegLogin_Account_EditText)).getText().toString().length() < 4){
            Toast.makeText(RegLogin.this,"失败，请输入的账号字符高于3",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.RegLogin_Password_EditText)).getText().toString().length() >= 20){
            Toast.makeText(RegLogin.this,"失败，请输入的密码字符少于20",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(((EditText) findViewById(R.id.RegLogin_Password_EditText)).getText().toString().length() < 6){
            Toast.makeText(RegLogin.this,"失败，请输入的密码字符高于5",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

//    获取Account值
    protected String getAccount(){
        return ((EditText) findViewById(R.id.RegLogin_Account_EditText)).getText().toString();
    }

//    获取Password值
    protected String getPassword(){
        return ((EditText) findViewById(R.id.RegLogin_Password_EditText)).getText().toString();
    }

//    使用dialog确认密码
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.RegLogin_Reg_Btn:
                if(chkAccPassword()) {
                    dialog = new Dialog(this);
                    View viewRegDialog = LayoutInflater.from(this).inflate(R.layout.dialog_judge_password, null);
                    viewRegDialog.findViewById(R.id.RegLogin_Dialog_Yes_Btn).setOnClickListener(this);
                    viewRegDialog.findViewById(R.id.RegLogin_Dialog_No_Btn).setOnClickListener(this);
                    editText = (EditText) viewRegDialog.findViewById(R.id.RegLogin_Dialog_EditText);
                    dialog.setContentView(viewRegDialog);
                    WindowManager.LayoutParams params_judge = dialog.getWindow().getAttributes();
                    dialog.getWindow().setAttributes(params_judge);
                    //获取屏幕长 宽
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    //设置对话框长宽
                    if (displayMetrics.heightPixels != 0 && displayMetrics.widthPixels != 0) {
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        params_judge.width = (int) (displayMetrics.widthPixels * 0.8); // 高度设置为屏幕的0.9
                    } else {
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        params_judge.width = 1200;
                    }
                    //show之前设置返回键无效，触摸屏无效
                    dialog.setCancelable(false);
                    //显示对话框
                    dialog.show();
                }
                    break;
            case R.id.RegLogin_Dialog_Yes_Btn:
                if(editText.getText().toString().equals(getPassword())) {
                    try {
                        regSync(getAccount(), getPassword());
                    } catch (IOException e) {
                        System.out.println("url IOException\n");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        System.out.println("JSON Text Exception\n");
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(RegLogin.this,"密码输入不一致，注册失败",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.RegLogin_Dialog_No_Btn:
                dialog.dismiss();

                break;

        }
    }

    /*****************************************线程操作*********************************************/
//    注册功能使用
//    通过AsyncTask轻量级线程进行注册操作
    @SuppressLint("StaticFieldLeak")
    protected void regSync(final String account, final String password) throws IOException, JSONException {
        //建立新的线程，完成URL的工作
        asyncTask = new AsyncTask<String,Void,String>() {
            //后台线程的操作
            protected String doInBackground(String... strings) {
                //打包json格式文件准备发送服务器
                JSONObject jsonObject = new JSONObject();
                String receiveData = null;
                String result = "";
                try {
                    jsonObject.put("account",strings[0]);
                    jsonObject.put("password",strings[1]);
                } catch (JSONException e) {
                    System.out.println("JSON Text Exception\n");
                    e.printStackTrace();
                }
                //建立连接，并发送给服务器
//                String strUrl = "http://172.26.93.186:8080/regJsonData";
                String strUrl = "http://"+ MainActivity.IP +"/regJsonData";
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(strUrl);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setDoInput(true);//输入采用字节流
                    urlConnection.setDoOutput(true);//输出采用字节流
                    urlConnection.setRequestMethod("POST");//设置为POST向服务器发送数据
                } catch (IOException e) {
                    System.out.println("urlConnection Set Information Exception\n");
                    e.printStackTrace();
                    return "服务器消息设置失败";
                }
                urlConnection.setUseCaches(false);//设置缓存
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                //设置格式为json格式文件
                urlConnection.setRequestProperty("Content-Type","application/json");
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
                    return "服务器连接失败";
                }
                //发送完毕，接收应答
                try {
                    if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //如果成功，则将返回的消息，存入BufferedReader中
                        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        while ((receiveData = bufferedReader.readLine()) != null){
                            result += receiveData + "\n";
                        }
                        inputStreamReader.close();
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    System.out.println("urlConnection Back Information Exception\n");
                    e.printStackTrace();
                    return "服务器消息接收失败";
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
                //显示注册成功，并跳转页面
                if(s.equals("reg_Succeed"+"\n")) {
                    Toast.makeText(RegLogin.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegLogin.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("account", account);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                if(s.equals("reg_Account_Exist_Fail"+"\n")) {
                    Toast.makeText(RegLogin.this, "账户已存在，注册失败, 请重新输入", Toast.LENGTH_SHORT).show();
                    ((EditText)findViewById(R.id.RegLogin_Account_EditText)).setText("");
                    ((EditText)findViewById(R.id.RegLogin_Password_EditText)).setText("");
                }
                else{
                    Toast.makeText(RegLogin.this, s, Toast.LENGTH_SHORT).show();
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
        }.execute(account,password);
    }

    @SuppressLint("StaticFieldLeak")
    protected void loginSync(final String account, final String password) throws IOException, JSONException {
        //建立新的线程，完成URL的工作
        asyncTask = new AsyncTask<String,Void,String>() {
            //后台线程的操作
            protected String doInBackground(String... strings) {
                //打包json格式文件准备发送服务器
                JSONObject jsonObject = new JSONObject();
                String receiveData = null;
                String result = "";
                try {
                    jsonObject.put("account",strings[0]);
                    jsonObject.put("password",strings[1]);
                } catch (JSONException e) {
                    System.out.println("JSON Text Exception\n");
                    e.printStackTrace();
                }
                //建立连接，并发送给服务器
//                String strUrl = "http://172.26.93.186:8080/loginJsonData";
                String strUrl = "http://"+ MainActivity.IP +"/loginJsonData";
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(strUrl);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setDoInput(true);//输入采用字节流
                    urlConnection.setDoOutput(true);//输出采用字节流
                    urlConnection.setRequestMethod("POST");//设置为Get向服务器发送数据
                } catch (IOException e) {
                    System.out.println("urlConnection Set Information Exception\n");
                    e.printStackTrace();
                    return "服务器消息设置失败";
                }
                urlConnection.setUseCaches(false);//设置缓存
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                //设置格式为json格式文件
                urlConnection.setRequestProperty("Content-Type","application/json");
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
                    return "服务器连接失败";
                }
                //发送完毕，接收应答
                try {
                    if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //如果成功，则将返回的消息，存入BufferedReader中
                        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        while ((receiveData = bufferedReader.readLine()) != null){
                            result += receiveData + "\n";
                        }
                        inputStreamReader.close();
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    System.out.println("urlConnection Back Information Exception\n");
                    e.printStackTrace();
                    return "服务器消息接收失败";
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
                //显示注册成功，并跳转页面
                if(s.equals("login_Succeed"+"\n")) {
                    Toast.makeText(RegLogin.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegLogin.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("account", account);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
                else if(s.equals("password_Error_Fail"+"\n")) {
                    Toast.makeText(RegLogin.this, "账户密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    ((EditText)findViewById(R.id.RegLogin_Account_EditText)).setText("");
                    ((EditText)findViewById(R.id.RegLogin_Password_EditText)).setText("");
                }
                else if(s.equals("null_Account_Fail"+"\n")) {
                    Toast.makeText(RegLogin.this, "不存在此用户，请注册", Toast.LENGTH_SHORT).show();
                    ((EditText)findViewById(R.id.RegLogin_Account_EditText)).setText("");
                    ((EditText)findViewById(R.id.RegLogin_Password_EditText)).setText("");
                }
                else{
                    Toast.makeText(RegLogin.this, s, Toast.LENGTH_SHORT).show();
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
        }.execute(account,password);
    }


}


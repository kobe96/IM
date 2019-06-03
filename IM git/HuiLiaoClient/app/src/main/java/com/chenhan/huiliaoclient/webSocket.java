package com.chenhan.huiliaoclient;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class webSocket extends WebSocketListener {

    WebSocket thisWebSocket;
    private String account;
    private boolean initDataFinish = false;
    private boolean initTheTruth = false;

    //webSocket 初始化一个对象
    webSocket(String account) {
        this.account = account;
    }

    //连接时发送自己的账号，基本消息，同步各种消息
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("account", account);
            jsonObject.put("messageType", "connect");
            webSocket.send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thisWebSocket = webSocket;
        //获得了webSocket之后，
        // 发送心跳包，同步朋友列表，获取离线数据功能
        sendTimeMessage();
        sendInitFriendMessage();
        sendInitMySettings();
        sendInitHeadView();
        sendGetOfflineMessage();

    }


    //服务器的Message发送给客户端调用以下的方法
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            String messageType = jsonObject.getString("messageType");

            //处理朋友列表
            if (messageType.equals("getFriendList")) {
                String friendList = jsonObject.getString("message");
                try {
                    JSONArray jsonArray = new JSONArray(friendList);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (!MainActivity.databaseManagerInMainActivity.isExistFriend(account, jsonArray.get(i).toString()))
                            MainActivity.databaseManagerInMainActivity.insertFriend(new FriendListTable(account, MainActivity.databaseManagerInMainActivity.queryFriendCount() + 1,
                                    jsonArray.get(i).toString(), jsonArray.get(i).toString()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //初始化朋友信息列表
            else if (messageType.equals("getInitSettings")) {
                String Settings = jsonObject.getString("message");
                JSONArray jsonArray = new JSONArray(Settings);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjects = (JSONObject) jsonArray.get(i);
                    if (MainActivity.databaseManagerInMainActivity.queryIsMySettingsAccountExist(jsonObjects.getString("account"))||DatabaseManager.busyFlag == 1 )
                        MainActivity.databaseManagerInMainActivity.updateMySettings(new MySettingsTable(
                                Integer.parseInt(jsonObjects.getString("headId")),
                                jsonObjects.getString("account"),
                                jsonObjects.getString("name"),
                                Integer.parseInt(jsonObjects.getString("age")),
                                jsonObjects.getString("address"),
                                jsonObjects.getString("phone"),
                                Integer.parseInt(jsonObjects.getString("sex"))
                        ));
                    else
                        MainActivity.databaseManagerInMainActivity.insertMySettings(new MySettingsTable(
                                Integer.parseInt(jsonObjects.getString("headId")),
                                jsonObjects.getString("account"),
                                jsonObjects.getString("name"),
                                Integer.parseInt(jsonObjects.getString("age")),
                                jsonObjects.getString("address"),
                                jsonObjects.getString("phone"),
                                Integer.parseInt(jsonObjects.getString("sex"))
                        ));
                }
            }

            //获取朋友头像
            else if (messageType.equals("getHeadView")) {
                String headView = jsonObject.getString("message");
                JSONArray jsonArray = new JSONArray(headView);
                System.out.println("已接收头像message" + jsonArray.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjects = (JSONObject) jsonArray.get(i);
                    if (MainActivity.databaseManagerInMainActivity.queryIsHeadViewAccountExist(jsonObjects.getString("account"))) {
                        byte[] img = Base64.decode(jsonObjects.getString("head_view"), Base64.DEFAULT);
                        for (int j = 0; j < 10; j++) {
                            System.out.println(img[j]);
                        }
                        MainActivity.databaseManagerInMainActivity.updateHeadView(new HeadViewTable(
                                img,
                                jsonObjects.getString("account")
                        ));
                    } else {

                        byte[] img = Base64.decode(jsonObjects.getString("head_view"), Base64.DEFAULT);
                        for (int j = 0; j < 10; j++) {
                            System.out.println(img[j]);
                        }
                        MainActivity.databaseManagerInMainActivity.insertHeadView(new HeadViewTable(
                                img,
                                jsonObjects.getString("account")
                        ));

                    }

                }

            } else if (messageType.equals("getTheTruth")) {
                JSONArray jsonArray = new JSONArray(jsonObject.get("message").toString());
                FragmentTheTruth.jsonArrays = jsonArray;

                initTheTruth = true;
            } else if (messageType.equals("nullGetTheTruth")) {
                initTheTruth = true;
            }

            //处理心跳包
            else if (messageType.equals("heartBeat")) {
                sendTimeMessage();
            }

            //处理离线消息储存
            else if (messageType.equals("offlineMessage")) {
                JSONArray jsonArray = (JSONArray) jsonObject.get("message");
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjects = (JSONObject) jsonArray.get(i);
                        MainActivity.databaseManagerInMainActivity.insertMessage(new MessageTable(MainActivity.databaseManagerInMainActivity.queryMessageCount() + 1, jsonObjects.getString("message"),
                                jsonObjects.getString("sendAccount"), jsonObjects.getString("myAccount"), 0, 0));
                        initDataFinish = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //处理没有离线消息
            else if (messageType.equals("nullOfflineMessage")) {
                initDataFinish = true;
            }

            //处理在线消息
            else if (messageType.equals("onlineSingleMessage")) {
                MainActivity.databaseManagerInMainActivity.insertMessage(new MessageTable(MainActivity.databaseManagerInMainActivity.queryMessageCount() + 1, jsonObject.getString("message"),
                        jsonObject.getString("sendAccount"), jsonObject.getString("toAccount"), 0, 1));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        sendCloseMessage(1000);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        sendCloseMessage(1001);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {

    }


    public void sendInitHeadView() {
        final String message = "{\"messageType\":\"getHeadView\",\"account\":\"" + account + "\"}";
        thisWebSocket.send(message);
    }

    public void sendInitMySettings() {
        final String message = "{\"messageType\":\"getInitSettings\",\"account\":\"" + account + "\"}";
        thisWebSocket.send(message);
    }

    public void sendGetOfflineMessage() {
        final String message = "{\"messageType\":\"getMessage\",\"account\":\"" + account + "\"}";
        thisWebSocket.send(message);
    }

    public void sendLikes(String message){
        JSONObject object = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject(message);
            object.put("account",account);
            object.put("messageType","addLike");
            object.put("message",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thisWebSocket.send(object.toString());
    }
    //关闭客户端
    public void sendCloseMessage(int code) {
        if (code == 1000)
            thisWebSocket.close(code, "close");
        else if (code == 1001)
            thisWebSocket.close(code, "be closed");

    }

    //发送消息
    public boolean sendMessage(String friendAccount, String message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("messageType", "sendMessage");
            jsonObject.put("message", message);
            jsonObject.put("toAccount", friendAccount);
            jsonObject.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return thisWebSocket.send(jsonObject.toString());
    }

    //收到服务器端发送来的信息后，每隔25秒发送一次心跳包
    private void sendTimeMessage() {
        final String message = "{\"messageType\":\"heartBeat\",\"account\":\"" + account + "\"}";
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                thisWebSocket.send(message);
            }
        }, 25000);
    }

    private void sendInitFriendMessage() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("messageType", "getFriendList");
            jsonObject.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thisWebSocket.send(jsonObject.toString());
    }

    public void sendHeadView(byte[] img) {
        JSONObject jsonObject = null;
        String base64 = Base64.encodeToString(img, Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            jsonObject.put("messageType", "updateHeadView");
            jsonObject.put("account", account);
            jsonObject.put("message", base64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thisWebSocket.send(jsonObject.toString());
    }

    public void sendUpdateMySettings(MySettingsTable mySettingsTable) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("messageType", "updateMySettings");
            jsonObject.put("account", account);
            jsonObject.put("age", mySettingsTable.getAge());
            jsonObject.put("sex", mySettingsTable.getSex());
            jsonObject.put("phoneNum", mySettingsTable.getPhoneNum());
            jsonObject.put("nikeName", mySettingsTable.getNikeName());
            jsonObject.put("headId", mySettingsTable.getHeadId());
            jsonObject.put("address", mySettingsTable.getAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thisWebSocket.send(jsonObject.toString());
    }

    public void sendGetTheTruth() {
        initTheTruth = false;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("messageType", "getTheTruth");
            jsonObject.put("account", account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thisWebSocket.send(jsonObject.toString());
    }

    public void sendTheTruth(String message, String sendType, String friendList) {
        /*
         * 格式{ messagetype;xxx
         *       message:{  send_type:xxxx
         *                  message_content:{  message:xxx
         *                                     friend:[]
         *                  }
         *       }
         * }或
         *{ messagetype;xxx
         *       message:{  send_type:xxxx
         *                  message_content:xxxx
         *       }
         * }
         *
         *
         * */

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("messageType", "sendTheTruth");
            jsonObject.put("account", account);
            JSONObject jsonObjectMessageContent = new JSONObject();
            jsonObjectMessageContent.put("message", message);
            if (sendType.equals("friend")) {
                jsonObjectMessageContent.put("send_type", sendType);
                jsonObjectMessageContent.put("message_content", message);
            } else {
                jsonObjectMessageContent.put("send_type", sendType);
                JSONObject jsonObjectMessageToSomeFriend = new JSONObject();
                jsonObjectMessageToSomeFriend.put("message", message);
                jsonObjectMessageToSomeFriend.put("friend", friendList);
                jsonObjectMessageContent.put("message_content", jsonObjectMessageToSomeFriend.toString());
            }
            jsonObject.put("message", jsonObjectMessageContent.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        thisWebSocket.send(jsonObject.toString());
    }


    public boolean isInitDataFinish() {
        return initDataFinish;
    }

    public boolean isInitTheTruth() {
        return initTheTruth;
    }


}

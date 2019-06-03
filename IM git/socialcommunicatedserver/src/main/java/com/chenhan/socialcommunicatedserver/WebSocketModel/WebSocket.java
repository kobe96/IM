package com.chenhan.socialcommunicatedserver.WebSocketModel;
import com.chenhan.socialcommunicatedserver.Util.ApplicationContextRegister;
import org.springframework.beans.BeansException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/recSendMsgWebSocket")
@Component
public class WebSocket {

    WebSocketService service;
    //在线人数
    private static int onlineCount = 0;
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
    private Session session;
    private String onlineAccount;
    private int test = 0;

    @OnOpen
        public void onOpen(Session session) throws IOException {
            this.session = session;
        try {
            service = ApplicationContextRegister.getApplicationContext().getBean(WebSocketService.class);
        } catch (BeansException e) {
            e.printStackTrace();
        }
            dealConnectMessage();
    }

    @OnClose
    public void onClose() throws IOException {
        for (WebSocket item : clients.values()) {
            if (item.onlineAccount.equals(this.onlineAccount) ) {
                try {
                    if (item.session != null) {
                        item.session.close();
                    }
                } catch (IllegalStateException e) {
                    System.out.println("WebSocket前端断开连接");
                } catch (Exception e) {
                    System.out.println("WebSocket关闭连接出错");
                }
            }
        }

        System.out.println(this.onlineAccount + "已断开");

        clients.remove(this);

    }

    @OnMessage
    public void onMessage(String message) throws IOException {
            try {
                JSONObject jsonObject = new JSONObject(message);
                   String messageType = jsonObject.getString("messageType");
                   this.onlineAccount = jsonObject.getString("account");
                   //获取离线消息
                if(messageType.equals("getMessage")){
                    dealGetMessage(this.onlineAccount);
                }
                //发送消息
                else if(messageType.equals("sendMessage")){
                    String realMessage = jsonObject.getString("message");
                    String toAccount = jsonObject.getString("toAccount");
                    dealSendMessage( realMessage, toAccount);
                }
                //获取朋友列表
                else if(messageType.equals("getFriendList")){
                    String [] friendList = service.getFriendList(this.onlineAccount);
                    dealGetFriendList(friendList);
                }
                //心跳包处理
                else if(messageType.equals("heartBeat")){
                    System.out.println("HeartBeat" + ++test);
                    System.out.println("online" + clients.size());
                    dealHeartBeat();
                }
                //处理connect链接
                else if(messageType.equals("connect")){
                    clients.put(jsonObject.getString("account"),this);
                    System.out.println(this.onlineAccount + "已连接");
                }
                //获取个人设置
                else if(messageType.equals("getInitSettings")){
                    dealGetSettings();
                }
                //更新个人设置
                else if(messageType.equals("updateMySettings")){
                    dealUpdateSettings(jsonObject);
                }
                //更新头像信息
                else if(messageType.equals("updateHeadView")){
                    dealUpdateHeadView(jsonObject.getString("message"));
                }
                //获取头像信息
                else if(messageType.equals("getHeadView")){
                    dealGetHeadView();
                }
                //发送一条社交消息
                else if(messageType.equals("sendTheTruth")){
                    dealSendTheTruth(jsonObject.getString("message"));
                }
                //获取社交广场消息
                else if(messageType.equals("getTheTruth")){
                    dealGetTheTruth();
                }
                else if(messageType.equals("addLike")){
                    dealAddLike(jsonObject.getString("message"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }


    @OnError
    public void onError(Session session, Throwable error) {
        //error.printStackTrace();
    }

    //心跳包处理
    public void dealHeartBeat(){
        synchronized (session) {
            this.session.getAsyncRemote().sendText("{\"messageType\":\"heartBeat\",\"message\":\"" + "heartBeatGet" + "\"}");
        }
    }

    //链接打开时处理
    public void dealConnectMessage(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message","connectedSucceed");
            jsonObject.put("messageType","connect");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        synchronized (session) {
            this.session.getAsyncRemote().sendText(jsonObject.toString());
        }
        dealGetMessage(this.onlineAccount);
    }

    //返回个人设置
    public void dealGetSettings(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageType","getInitSettings");
            jsonObject.put("message",service.getSettings(this.onlineAccount));
            synchronized (session) {
                this.session.getAsyncRemote().sendText(jsonObject.toString());
            }
            System.out.println("已发送个人设置"+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //返回朋友的头像
    public void dealGetHeadView(){
        synchronized (session) {
            this.session.getAsyncRemote().sendText(service.getHeadView(this.onlineAccount));
            System.out.println("已发送头像"+service.getHeadView(this.onlineAccount));
        }

    }

    //更新个人设置
    public void dealUpdateSettings(JSONObject jsonObject){
        try {
            String account = jsonObject.getString("account");
            String age = jsonObject.getString("age");
            String sex = jsonObject.getString("sex");
            String phoneNum = jsonObject.getString("phoneNum");
            String nikeName = jsonObject.getString("nikeName");
            String headId = jsonObject.getString("headId");
            String address = jsonObject.getString("address");
            service.updateMySettings(headId,account,nikeName,age,sex,address,phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //更新头像设置
    public void dealUpdateHeadView(String message){
        service.updateHeadView(message,this.onlineAccount);
    }

    //处理消息模块，finish
    public void dealSendMessage( String message, String toAccount){
            if(service.isFriend(this.onlineAccount,toAccount) == true) {
                if (!sendMessage(message, toAccount)) {
                    //不在线，保存进数据库，数据返回成功或不成功
                    saveMessage(message, toAccount);
                    System.out.println("已保存数据" + message);
                }
            }
    }

    //获取朋友列表
    public void dealGetFriendList(String [] friendList){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageType","getFriendList");
            jsonObject.put("message",new JSONArray(friendList).toString());
            synchronized (session) {
                this.session.getAsyncRemote().sendText(jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //发送消息模块,finish
    public boolean sendMessage(String message, String toAccount){
        for (WebSocket item : clients.values()) {
            //是否发送在线
            if (item.onlineAccount.equals(toAccount) ) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("message",message);
                    jsonObject.put("sendAccount",this.onlineAccount);
                    jsonObject.put("toAccount",toAccount);
                    jsonObject.put("messageType","onlineSingleMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                item.session.getAsyncRemote().sendText(jsonObject.toString());
                System.out.println("已发送在线消息" + jsonObject.toString());
                return true;
            }
        }
        return false;
    }

    //保存到数据库消息，finish
    public void saveMessage(String message, String toAccount){
        service.insertOfflineMessageService(this.onlineAccount,toAccount,message);
    }

    //获取离线消息
    public void dealGetMessage(String myAccount){
        List<Map> mapList = service.getOfflineMessageService(myAccount);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            if (mapList.size() == 0){
                jsonObject.put("messageType","nullOfflineMessage");
            }
            else{
                jsonObject.put("messageType","offlineMessage");
                System.out.println(mapList.size());
                for(int i = 0;i<mapList.size();i++){
                    JSONObject jsonMessage = new JSONObject();
                    jsonMessage.put("myAccount",mapList.get(i).get("rec_account"));
                    jsonMessage.put("sendAccount",mapList.get(i).get("account"));
                    jsonMessage.put("message",mapList.get(i).get("message"));
                    System.out.println("已发送离线消息" + jsonMessage.getString("message"));
                    jsonArray.put(jsonMessage);
                }
                jsonObject.put("message",jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.session.getAsyncRemote().sendText(jsonObject.toString());

    }

    //发送 the truth
    public void dealSendTheTruth(String message){
        String type = null;
        String message_content = null;
        try {
            JSONObject jsonObject = new JSONObject(message);
            type = jsonObject.getString("send_type");
            message_content = jsonObject.getString("message_content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        service.insTheTruth(type,message_content,this.onlineAccount);

    }

    //获取 The Truth
    public void dealGetTheTruth(){

        this.session.getAsyncRemote().sendText(service.getTheTruth(this.onlineAccount));
    }

    //处理点赞
    public void dealAddLike(String message){
        service.addLike(message);
    }

    public void dealSendLikes(String message){

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }


//    public static synchronized Map<String, WebSocket> getClients() {
//        return clients;
//    }
}
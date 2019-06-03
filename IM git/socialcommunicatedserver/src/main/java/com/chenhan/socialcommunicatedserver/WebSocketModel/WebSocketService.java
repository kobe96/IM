package com.chenhan.socialcommunicatedserver.WebSocketModel;

import com.chenhan.socialcommunicatedserver.Dao.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class WebSocketService {

    @Resource
    UserMapper userMapper;
    @Resource
    FriendListMapper friendListMapper;
    @Resource
    PasswordChkMapper passwordChkMapper;
    @Resource
    OfflineMessageMapper offlineMessageMapper;
    @Resource
    HeadViewMapper headViewMapper;
    @Resource
    ReadTheTruthMapper readTheTruthMapper;
    @Resource
    TheTruthMapper theTruthMapper;

    //插入离线聊天数据
    public void insertOfflineMessageService(String account,String toAccount,String message){
        offlineMessageMapper.ins_OfflineMessage(account,toAccount,message);
    }

    //获取离线消息，之后删除消息记录
    public List<Map> getOfflineMessageService(String account){
        List<Map> list = offlineMessageMapper.get_OfflineMessage(account);
        offlineMessageMapper.del_OfflineMessage(account);
        return list;
    }

    //获取好友列表
    public String[] getFriendList(String myAccount){
        List<Map> friendListMap= friendListMapper.get_FriendList(myAccount);
        String [] friendList = new String [friendListMap.size()];
        for(int i = 0;i<friendList.length;i++){
            friendList[i] = friendListMap.get(i).get("friend_account").toString();
        }
        return friendList;
    }

    //返回查询的账号信息
    public String getSettings(String account){
        List<Map> friendListMap= friendListMapper.get_FriendList(account);
        JSONArray jsonArray = new JSONArray();
        String [] friendList = new String [friendListMap.size()];
        for(int i = 0;i<friendList.length;i++){
            friendList[i] = friendListMap.get(i).get("friend_account").toString();
            JSONObject jsonObject = new JSONObject(userMapper.get_AccountSettings(friendList[i]));
            jsonArray.put(jsonObject);
        }
        JSONObject jsonObject = new JSONObject(userMapper.get_AccountSettings(account));
        jsonArray.put(jsonObject);
        return jsonArray.toString();
    }

    //更新个人设置
    public void updateMySettings(String headId ,String account, String nikeName,String age,String sex,String address,String phoneNum){

        userMapper.update_User(nikeName,Integer.parseInt(age),address,phoneNum,Integer.parseInt(sex),Integer.parseInt(headId),account);
    }

    //更新指定账号的头像
    public void updateHeadView(String message,String account){
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            byte[] img = decoder.decodeBuffer(message);
            if(headViewMapper.sel_IsExist(account) >= 1) {
                headViewMapper.update_HeadView(img, account);
            }
            else {
                headViewMapper.ins_HeadView(img, account);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取头像
    public String getHeadView(String account){
        BASE64Encoder encoder = new BASE64Encoder();
        JSONObject json = new JSONObject();
        List<Map> friendListMap= friendListMapper.get_FriendList(account);
        JSONArray jsonArray = new JSONArray();
        String [] friendList = new String [friendListMap.size()];
        //获取自己朋友的
        for(int i = 0;i<friendList.length;i++){
            friendList[i] = friendListMap.get(i).get("friend_account").toString();
            if(userMapper.get_headId(friendList[i]) == 1) {
                Map map = headViewMapper.get_HeadView(friendList[i]);
                byte[] bytes = (byte[]) map.get("head_view");
                String base64 = encoder.encodeBuffer(bytes);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("head_view",base64);
                    jsonObject.put("account",map.get("account"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
            else
                continue;
        }
        //获取自己的
        if(userMapper.get_headId(account) == 1) {
            Map map = headViewMapper.get_HeadView(account);
            byte[] bytes = (byte[]) map.get("head_view");
            String base64 = encoder.encodeBuffer(bytes);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("head_view",base64);
                jsonObject.put("account",map.get("account"));
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            try {
                json.put("messageType", "getHeadView");
                json.put("message", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return json.toString();
    }

    //处理点赞
    public void addLike(String message){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String messageId = null;
        String send_account = null;
        String rec_account = null;
        try {
            messageId = jsonObject.getString("messageId");
            send_account = jsonObject.getString("account");
            rec_account = jsonObject.getString("myAccount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(readTheTruthMapper.get_Is_Read(Integer.parseInt(messageId),send_account,rec_account) == 0){
                readTheTruthMapper.update_Read_The_Truth(Integer.parseInt(messageId),send_account,rec_account);
                int likes = theTruthMapper.get_Likes(Integer.parseInt(messageId)) + 1;
                theTruthMapper.update_Likes_Count(Integer.parseInt(messageId),likes);
        }
    }



    /**************************************************心里话*********************************************************/
    public void insTheTruth(String send_type,String message,String send_account){
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        if(send_type.equals("all")) {
            theTruthMapper.ins_The_Truth(message,send_account,timestamp);
            int messageId = theTruthMapper.sel_The_Truth_MessageId();
            List<String> accounts = userMapper.get_All_Account();
            for(int i = 0;i<accounts.size();i++) {
                readTheTruthMapper.ins_Read_The_Truth(send_account, accounts.get(i),messageId);
            }
            readTheTruthMapper.ins_Read_The_Truth(send_account,send_account,messageId);
        }
        else if(send_type.equals("friend")){
            theTruthMapper.ins_The_Truth(message,send_account,timestamp);
            int messageId = theTruthMapper.sel_The_Truth_MessageId();
            List<Map> accounts = friendListMapper.get_FriendList(send_account);
            for(int i = 0;i<accounts.size();i++){
                readTheTruthMapper.ins_Read_The_Truth(send_account,accounts.get(i).get("friend_account").toString(),messageId);
            }
            readTheTruthMapper.ins_Read_The_Truth(send_account,send_account,messageId);
        }
        else if(send_type.equals("some_friend")){
            JSONArray jsonArray = new JSONArray();
            try {
                System.out.println(message);
                JSONObject jsonObject  = new JSONObject(message);
                jsonArray = new JSONArray(jsonObject.get("friend").toString());
                theTruthMapper.ins_The_Truth(jsonObject.get("message").toString(),send_account,timestamp);
                int messageId = theTruthMapper.sel_The_Truth_MessageId();
                for(int i = 0;i<jsonArray.length();i++){
                    readTheTruthMapper.ins_Read_The_Truth(send_account,jsonArray.get(i).toString(),messageId);
                }
                readTheTruthMapper.ins_Read_The_Truth(send_account,send_account,messageId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTheTruth(String account){
        BASE64Encoder base64Encoder = new BASE64Encoder();
        List<Integer> integerList = readTheTruthMapper.get_message_id(account);
        JSONArray the_Truth_Map = new JSONArray();
        JSONObject jsonObjectItem = null;
        for(int i = 0;i<integerList.size();i++){
            Map item = theTruthMapper.get_message_Content(integerList.get(i));
            try {
                jsonObjectItem = new JSONObject();
                jsonObjectItem.put("message",item.get("message").toString());
                jsonObjectItem.put("message_id",item.get("message_id").toString());
                jsonObjectItem.put("date_time",item.get("date_time").toString());
                jsonObjectItem.put("likes",item.get("likes").toString());
                jsonObjectItem.put("send_account",item.get("send_account").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(userMapper.get_headId(item.get("send_account").toString()) == 0){
                try {
                    jsonObjectItem.put("head_view","null");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Map headView = headViewMapper.get_HeadView(item.get("send_account").toString());
                byte[] bytes = (byte[]) headView.get("head_view");
                String base64 = base64Encoder.encodeBuffer(bytes);
                try {
                    jsonObjectItem.put("head_view",base64);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            the_Truth_Map.put(jsonObjectItem);
        }
        JSONArray jsonArray = the_Truth_Map;
        JSONObject jsonObject = new JSONObject();
        try {
            if(jsonArray != null) {
                jsonObject.put("messageType", "getTheTruth");
                jsonObject.put("message", jsonArray.toString());
            }
            else {
                jsonObject.put("messageType", "nullGetTheTruth");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(jsonObject.toString());
        return jsonObject.toString();
    }

    boolean isFriend(String myAccount,String friendAccount){
        if(friendListMapper.chk_Friend(myAccount, friendAccount) >= 1&&friendListMapper.chk_Friend(friendAccount,myAccount)>=1) {
            return true;
        }
        else
            return false;
    }

}

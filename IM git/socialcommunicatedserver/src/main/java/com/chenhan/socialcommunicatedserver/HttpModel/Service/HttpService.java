package com.chenhan.socialcommunicatedserver.HttpModel.Service;

import com.chenhan.socialcommunicatedserver.Dao.FriendListMapper;
import com.chenhan.socialcommunicatedserver.Dao.OfflineMessageMapper;
import com.chenhan.socialcommunicatedserver.Dao.PasswordChkMapper;
import com.chenhan.socialcommunicatedserver.Dao.UserMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Resource;
import java.util.List;

@org.springframework.stereotype.Service
public class HttpService {

    @Resource
    UserMapper userMapper;
    @Resource
    FriendListMapper friendListMapper;
    @Resource
    PasswordChkMapper passwordChkMapper;
    @Resource
    OfflineMessageMapper offlineMessageMapper;

    public String helloController() {
        return "Welcome ,Hello Controller!\n";
    }

    //  注册账号，
//  userMapper.Chk_Usr校验是否存在账号，
//  userMapper.Reg_Usr注册账号
//  passwordMapper.reg_Password_Chk在登录端注册
    public String regAccountService(String account, String password) {
        if (userMapper.chk_Usr(account) == 0) {
            userMapper.reg_Usr(account, password);
            passwordChkMapper.reg_Password_Chk(account, password);
            return "reg_Succeed";
        } else {
            return "reg_Account_Exist_Fail";
        }
    }

    public String loginAccountService(String account, String password) {
        if (userMapper.chk_Usr(account) > 0) {
            if (passwordChkMapper.login_Password_Chk(account).equals(password)) {
                /*
                 * 写登录读取模块数据
                 * */
                return "login_Succeed";
            } else
                return "password_Error_Fail";
        } else
            return "null_Account_Fail";
    }

    //加入好友信息
    public String insertFriendService(String account, String friendAccount, String reName) {
        if (userMapper.chk_Usr(friendAccount) >= 1) {
            if (friendListMapper.chk_Friend(account,friendAccount) >= 1)
                return "friend_Exist";
            else {
                friendListMapper.ins_Friend(account, friendAccount, reName);
                return "insert_Friend_Succeed";
            }
        } else
            return "account_Not_Exist";

    }

    //查询好友ID
    public String searchFriendService(String SearchAccount) {
        List<String> getAccounts = userMapper.get_Account("%" + SearchAccount + "%");
        String result = new String();
        if (getAccounts == null || getAccounts.size() == 0) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", "isNull");
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonObject.put("message", "Succeed");
                for (int i = 0; i < getAccounts.size(); i++) {
                    JSONObject jsonAccount = new JSONObject();
                    jsonAccount.put("account", getAccounts.get(i));
                    jsonArray.put(jsonAccount);
                }
                jsonObject.put("accounts", jsonArray);
                result = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }




}

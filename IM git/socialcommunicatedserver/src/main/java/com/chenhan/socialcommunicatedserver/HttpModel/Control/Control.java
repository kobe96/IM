package com.chenhan.socialcommunicatedserver.HttpModel.Control;

import com.alibaba.fastjson.JSON;
import com.chenhan.socialcommunicatedserver.HttpModel.Service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Control {
    @Autowired
    HttpService service;


    @RequestMapping(value = "/regJsonData",method = RequestMethod.POST)
    @ResponseBody
//    数据的初步验证放在前端进行
//    后端数据库的验证，放在service中进行
    public String regAccountControl(@RequestBody String jsonParam) {
        Map requestMap = JSON.parseObject(jsonParam,Map.class);
        String account = (String) requestMap.get("account");
        String password = (String) requestMap.get("password");
        return service.regAccountService(account,password);
    }

    @RequestMapping(value = "/loginJsonData",method = RequestMethod.POST)
    @ResponseBody
//    登录控制
    public String loginAccountControl(@RequestBody String jsonParam) {
        Map requestMap = JSON.parseObject(jsonParam,Map.class);
        String account = (String) requestMap.get("account");
        String password = (String) requestMap.get("password");
        return service.loginAccountService(account,password);
    }

    @RequestMapping(value = "/insertFriendJsonData",method = RequestMethod.POST)
    @ResponseBody
//    加好友控制
    public String insertFriendControl(@RequestBody String jsonParam) {
        Map requestMap = JSON.parseObject(jsonParam,Map.class);
        String account = (String) requestMap.get("account");
        String friendAccount = (String) requestMap.get("friendAccount");
        String reName = (String) requestMap.get("reName");
        return service.insertFriendService(account,friendAccount,reName);
    }

    @RequestMapping(value = "/searchFriendJsonData",method = RequestMethod.POST)
    @ResponseBody
//    查询存在的账户
    public String searchFriendControl(@RequestBody String jsonParam) {
        Map requestMap = JSON.parseObject(jsonParam,Map.class);
        String account = (String) requestMap.get("account");
        return service.searchFriendService(account);
    }
}

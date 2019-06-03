package com.chenhan.socialcommunicatedserver.Dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    //注册账号，初始账号名即为姓名name
    @Insert("INSERT INTO user (account, name, password, age, address, phone, sex, headId) VALUES (#{account}, #{account}, #{password},'-1','未设置','0','0','0')")
    void reg_Usr(String account,String password);

    //判断账号是否存在
    @Select ("SELECT COUNT(*) from user where account = #{accountThis}")
    Integer chk_Usr(String accountThis);

    //返回查询的账号
    @Select("SELECT account from user where name like #{account}")
    List<String> get_Account(String account);

    //返回账号的所有信息
    @Select("SELECT account,name,age,address,phone,sex,headId FROM user WHERE account = #{account}")
    Map get_AccountSettings(String account);

    //更新账号消息
    @Update("UPDATE user SET name = #{name},age = #{age},address = #{address},phone = #{phone},sex = #{sex},headId = #{headId} WHERE account = #{account}")
    void update_User(String name,int age,String address,String phone,int sex,int headId,String account);

    //获得账号头像ID
    @Select("SELECT headId from user where account = #{accountThis}")
    Integer get_headId(String accountThis);

    //获得所有账号
    @Select("SELECT account FROM user")
    List<String> get_All_Account();

}

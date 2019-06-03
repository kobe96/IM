package com.chenhan.socialcommunicatedserver.Dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PasswordChkMapper {

    @Insert("INSERT INTO passwordCheck(account, password) VALUES (#{account}, #{password})")
    void reg_Password_Chk(String account,String password);

    @Select("SELECT password FROM passwordCheck where account = #{account}")
    String login_Password_Chk(String account);
}

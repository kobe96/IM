package com.chenhan.socialcommunicatedserver.Dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FriendListMapper {

    @Insert("INSERT INTO friend (account, friend_account, re_name) VALUES (#{account}, #{friendAccount}, #{reName})")
    void ins_Friend(String account,String friendAccount,String reName);

    @Select("SELECT COUNT(*) from friend where account = #{accountThis} and friend_account = #{friendAccount}")
    Integer chk_Friend(String accountThis,String friendAccount);

    @Select("SELECT friend_account from friend where account = #{accountThis} ")
    List<Map> get_FriendList(String accountThis);
}

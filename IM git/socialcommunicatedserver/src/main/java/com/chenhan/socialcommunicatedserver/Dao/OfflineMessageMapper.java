package com.chenhan.socialcommunicatedserver.Dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
public interface OfflineMessageMapper {

    @Insert("INSERT INTO offlineMessage (account, rec_account, message) VALUES (#{account}, #{recAccount}, #{message})")
    void ins_OfflineMessage(String account,String recAccount,String message);

    @Select("SELECT account, rec_account, message FROM offlineMessage WHERE rec_account = #{myAccount}")
    List<Map> get_OfflineMessage(String myAccount);

    @Delete("DELETE FROM offlineMessage WHERE rec_account = #{myAccount}")
    void del_OfflineMessage(String myAccount);
}

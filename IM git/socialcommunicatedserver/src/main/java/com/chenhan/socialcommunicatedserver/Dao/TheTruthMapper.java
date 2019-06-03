package com.chenhan.socialcommunicatedserver.Dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@Mapper
public interface TheTruthMapper {

    @Insert("INSERT INTO the_truth(message, likes,send_account,date_time) VALUES (#{message}, '0',#{send_account},#{dateTime})")
    void ins_The_Truth(String message, String send_account, Timestamp dateTime);

    @Select("select @@IDENTITY")
    Integer sel_The_Truth_MessageId();

    @Select("SELECT message ,send_account,likes,date_time,message_id FROM the_truth where message_id = #{message_Id} order by the_truth.message_id desc")
    Map get_message_Content(int message_Id);

    @Update("UPDATE the_truth SET likes = #{likes} WHERE message_id = #{message_Id}")
    void update_Likes_Count(int message_Id, int likes);

    @Select("SELECT likes FROM the_truth WHERE message_id = #{message_Id}")
    Integer get_Likes(int message_Id);

}

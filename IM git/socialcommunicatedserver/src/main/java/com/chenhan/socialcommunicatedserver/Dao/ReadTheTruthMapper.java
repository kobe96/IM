package com.chenhan.socialcommunicatedserver.Dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ReadTheTruthMapper {

    @Insert("INSERT INTO read_the_truth (send_account, rec_account,message_id,is_read) VALUES (#{send_account},#{rec_account},#{message_id},'0')")
    void ins_Read_The_Truth(String send_account,String rec_account,int message_id);

    @Select("SELECT message_id FROM read_the_truth where rec_account = #{account}")
    List<Integer> get_message_id(String account);

    @Update("UPDATE read_the_truth SET is_read = '1' WHERE message_id = #{message_id} and send_account = #{send_account} and rec_account = #{rec_account}")
    void update_Read_The_Truth(int message_id,String send_account,String rec_account);

    @Select("SELECT is_read FROM read_the_truth WHERE message_id = #{message_Id} and send_account = #{send_account} and rec_account = #{rec_account}")
    Integer get_Is_Read(int message_Id,String send_account,String rec_account);


}

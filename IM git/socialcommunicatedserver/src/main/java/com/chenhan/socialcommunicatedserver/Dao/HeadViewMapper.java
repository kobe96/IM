package com.chenhan.socialcommunicatedserver.Dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface HeadViewMapper {

    @Insert("INSERT INTO headImg (head_view, account) VALUES (#{headView}, #{account})")
    void ins_HeadView(byte [] headView,String account);

    @Select("SELECT head_view,account FROM headImg WHERE account = #{account}")
    Map get_HeadView(String account);

    @Update("UPDATE headImg SET head_view = #{headView} WHERE account = #{account}")
    void update_HeadView(byte [] headView,String account);

    @Select("SELECT COUNT(*) FROM headImg WHERE account = #{account}")
    Integer sel_IsExist(String account);

}

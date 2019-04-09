package com.kingold.educationblockchain.dao;

import com.kingold.educationblockchain.bean.TeacherInformation;

import java.util.List;

import org.apache.ibatis.annotations.*;

public interface TeacherInformationMapper {

    /**
     * 根据教師信息id查询教師信息
     */
    @Select("SELECT "
    		+ "a.kg_teacherinformationid,"
    		+ "a.kg_name,"
    		+ "a.kg_phonenumber,"
    		+ "b.kg_name kg_schoolname,"
    		+ "a.kg_state  "
    		+ "FROM kg_teacherinformation a "
    		+ "left join kg_school b on a.kg_schoolid = b.kg_schoolid  "
    		+ "where a.kg_teacherinformationid=#{id} and a.kg_state = 0 and b.kg_state = 0 ")
    @Results({
            @Result(property = "kg_teacherinformationid", column = "KG_TEACHERINFORMATIONID"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_phonenumber", column = "KG_PHONENUMBER"),
            @Result(property = "kg_schoolname", column = "KG_SCHOOLNAME"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    TeacherInformation FindTeacherInformationById(String id);

    /**
     * 根据教師手机号查询教師信息
     */
    @Select("SELECT "
    		+ "a.kg_teacherinformationid,"
    		+ "a.kg_name,"
    		+ "a.kg_phonenumber,"
    		+ "b.kg_name kg_schoolname,"
    		+ "a.kg_state  "
    		+ "FROM kg_teacherinformation a "
    		+ "left join kg_school b on a.kg_schoolid = b.kg_schoolid  "
    		+ "where a.kg_phonenumber=#{phone} and  a.kg_state = 0 and b.kg_state = 0 ")
    @Results({
            @Result(property = "kg_teacherinformationid", column = "KG_TEACHERINFORMATIONID"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_phonenumber", column = "KG_PHONENUMBER"),
            @Result(property = "kg_schoolname", column = "KG_SCHOOLNAME"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    TeacherInformation FindTeacherInformationByPhone(String phone);

    /**
     * 教師数据新增
     */
    @Insert("insert into kg_teacherinformation(kg_teacherinformationid,kg_name,kg_phonenumber,kg_schoolid,kg_state) values (#{kg_teacherinformationid},#{kg_name},#{kg_phonenumber},#{kg_schoolid},#{kg_state})")
    void AddTeacherInformation(TeacherInformation teacherInformation);

    /**
     * 教師数据修改
     */
    @Update("update kg_teacherinformation set kg_name=#{kg_name},kg_phonenumber=#{kg_phonenumber},kg_schoolid=#{kg_schoolid}, kg_state=#{kg_state} where kg_teacherinformationid=#{kg_teacherinformationid}")
    void UpdateTeacherInformation(TeacherInformation teacherInformation);

    /**
     * 教師数据删除
     */
    @Update("update kg_teacherinformation set kg_state=1 where kg_teacherinformationid=#{id}")
    void DeleteTeacherInformation(String id);
}

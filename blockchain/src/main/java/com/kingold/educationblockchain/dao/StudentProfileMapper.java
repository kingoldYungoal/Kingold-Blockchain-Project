package com.kingold.educationblockchain.dao;

import com.kingold.educationblockchain.bean.StudentProfile;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface StudentProfileMapper {

    /**
     * 根據id查詢學生信息
     */
    @Select("select kg_studentprofileid,kg_classname,kg_schoolname,kg_educationnumber,kg_studentnumber,kg_name,kg_fullname,kg_sex,kg_age,kg_countryname,to_char(kg_birthday,'yyyy-mm-dd') kg_birthday,kg_registeredresidence,kg_passportnumberoridnumber,kg_entrancestate,kg_state,to_char(kg_jointime,'yyyy-mm-dd') kg_jointime from KG_STUDENTPROFILE where kg_studentprofileid = #{id} and KG_STATE = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_classname", column = "KG_CLASSNAME"),
            @Result(property = "kg_schoolname", column = "KG_SCHOOLNAME"),
            @Result(property = "kg_educationnumber", column = "KG_EDUCATIONNUMBER"),
            @Result(property = "kg_studentnumber", column = "KG_STUDENTNUMBER"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_fullname", column = "KG_FULLNAME"),
            @Result(property = "kg_sex", column = "KG_SEX"),
            @Result(property = "kg_age", column = "KG_AGE"),
            @Result(property = "kg_countryname", column = "KG_COUNTRYNAME"),
            @Result(property = "kg_birthday", column = "KG_BIRTHDAY"),
            @Result(property = "kg_registeredresidence", column = "KG_REGISTEREDRESIDENCE"),
            @Result(property = "kg_passportnumberoridnumber", column = "KG_PASSPORTNUMBERORIDNUMBER"),
            @Result(property = "kg_entrancestate", column = "KG_ENTRANCESTATE"),
            @Result(property = "kg_state", column = "KG_STATE"),
            @Result(property = "kg_jointime", column = "KG_JOINTIME")
    })
    StudentProfile GetStudentProfileById(String id);

    /**
     * 根據学号或者学籍号查詢學生信息
     */
    @Select("select kg_studentprofileid,kg_classname,kg_schoolname,kg_educationnumber,kg_studentnumber,kg_name,kg_fullname,kg_sex,kg_age,kg_countryname,to_char(kg_birthday,'yyyy-mm-dd') kg_birthday,kg_registeredresidence,kg_passportnumberoridnumber,kg_entrancestate,kg_state,to_char(kg_jointime,'yyyy-mm-dd') kg_jointime from KG_STUDENTPROFILE where (kg_educationnumber = #{eduNumber} or kg_studentnumber = #{stuNumber}) and KG_STATE = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_classname", column = "KG_CLASSNAME"),
            @Result(property = "kg_schoolname", column = "KG_SCHOOLNAME"),
            @Result(property = "kg_educationnumber", column = "KG_EDUCATIONNUMBER"),
            @Result(property = "kg_studentnumber", column = "KG_STUDENTNUMBER"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_fullname", column = "KG_FULLNAME"),
            @Result(property = "kg_sex", column = "KG_SEX"),
            @Result(property = "kg_age", column = "KG_AGE"),
            @Result(property = "kg_countryname", column = "KG_COUNTRYNAME"),
            @Result(property = "kg_birthday", column = "KG_BIRTHDAY"),
            @Result(property = "kg_registeredresidence", column = "KG_REGISTEREDRESIDENCE"),
            @Result(property = "kg_passportnumberoridnumber", column = "KG_PASSPORTNUMBERORIDNUMBER"),
            @Result(property = "kg_entrancestate", column = "KG_ENTRANCESTATE"),
            @Result(property = "kg_state", column = "KG_STATE"),
            @Result(property = "kg_jointime", column = "KG_JOINTIME")
    })
    List<StudentProfile> GetStudentProfileByNumber(String eduNumber, String stuNumber);

    /**
     * 根据教師信息id查询
     */
    @Select("select * from KG_STUDENTPROFILE where kg_studentprofileid in (SELECT kg_studentprofileid FROM kg_student_teacher where kg_teacherinformationid=#{teacherId} and kg_state=0) and kg_state=0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_classname", column = "KG_CLASSNAME"),
            @Result(property = "kg_schoolname", column = "KG_SCHOOLNAME"),
            @Result(property = "kg_educationnumber", column = "KG_EDUCATIONNUMBER"),
            @Result(property = "kg_studentnumber", column = "KG_STUDENTNUMBER"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_fullname", column = "KG_FULLNAME"),
            @Result(property = "kg_sex", column = "KG_SEX"),
            @Result(property = "kg_age", column = "KG_AGE"),
            @Result(property = "kg_countryname", column = "KG_COUNTRYNAME"),
            @Result(property = "kg_birthday", column = "KG_BIRTHDAY"),
            @Result(property = "kg_registeredresidence", column = "KG_REGISTEREDRESIDENCE"),
            @Result(property = "kg_passportnumberoridnumber", column = "KG_PASSPORTNUMBERORIDNUMBER"),
            @Result(property = "kg_entrancestate", column = "KG_ENTRANCESTATE"),
            @Result(property = "kg_state", column = "KG_STATE"),
            @Result(property = "kg_jointime", column = "KG_JOINTIME")
    })
    List<StudentProfile> GetStudentsByTeacherId(String teacherId);

    /**
     * 根据教師信息id，学生班级查询
     */
    @Select("select * from KG_STUDENTPROFILE where kg_studentprofileid in (SELECT kg_studentprofileid FROM kg_student_teacher where kg_teacherinformationid=#{teacherId} and kg_state=0) and kg_classname=#{classname} and kg_state=0")
    List<StudentProfile> GetStudentsByClassAndTeacher(String teacherId, String classname);

    /**
     * 新增學生信息
     */
    @Insert("insert into KG_STUDENTPROFILE(kg_studentprofileid, kg_classname,kg_schoolname,kg_educationnumber,kg_studentnumber,kg_name,kg_fullname,kg_sex,kg_age,kg_countryname,kg_birthday,kg_registeredresidence,kg_passportnumberoridnumber,kg_entrancestate,kg_state,kg_jointime) values (#{kg_studentprofileid},#{kg_classname},#{kg_schoolname},#{kg_educationnumber},#{kg_studentnumber},#{kg_name},#{kg_fullname},#{kg_sex},#{kg_age},#{kg_countryname},to_date(#{kg_birthday},'yyyy-mm-dd'),#{kg_registeredresidence},#{kg_passportnumberoridnumber},#{kg_entrancestate},#{kg_state},to_date(#{kg_jointime},'yyyy-mm-dd'))")
    void AddStudentProfile(StudentProfile studentProfile);

    /**
     * 更新學生信息
     */
    @Update("update KG_STUDENTPROFILE set kg_classname=#{kg_classname},kg_schoolname=#{kg_schoolname},kg_educationnumber=#{kg_educationnumber},kg_studentnumber=#{kg_studentnumber},kg_name=#{kg_name},kg_fullname=#{kg_fullname},kg_sex=#{kg_sex},kg_age=#{kg_age},kg_countryname=#{kg_countryname},kg_birthday=to_date(#{kg_birthday},'yyyy-mm-dd'),kg_registeredresidence=#{kg_registeredresidence},kg_passportnumberoridnumber=#{kg_passportnumberoridnumber},#{kg_entrancestate},kg_jointime=to_date(#{kg_jointime},'yyyy-mm-dd') where kg_studentprofileid=#{kg_studentprofileid} and kg_state=0)")
    void UpdateStudentProfile(StudentProfile studentProfile);

    /**
     * 刪除學生信息
     */
    @Update("update KG_STUDENTPROFILE set kg_state=1 where kg_studentprofileid=#{id}")
    void DeleteStudentProfile(String id);

    /**
     * 刪除學生信息
     */
    @Delete("delete from KG_STUDENTPROFILE where kg_studentprofileid=#{id}")
    void RelDeleteStudentProfile(String id);
}

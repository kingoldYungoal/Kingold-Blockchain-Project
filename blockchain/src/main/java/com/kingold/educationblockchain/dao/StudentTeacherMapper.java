package com.kingold.educationblockchain.dao;

import com.kingold.educationblockchain.bean.StudentTeacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface StudentTeacherMapper {

    /**
     * 根据教師信息id查询
     */
    @Select("SELECT * FROM kg_student_teacher where kg_teacherinformationid=#{teacherId} and kg_state = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_teacherinformationid", column = "KG_TEACHERINFORMATIONID"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    List<StudentTeacher> FindStudentTeacherByTeacherId(String teacherId);

    /**
     * 根据学生id查询
     */
    @Select("SELECT * FROM kg_student_teacher where kg_studentprofileid=#{studentId} and kg_state = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_teacherinformationid", column = "KG_TEACHERINFORMATIONID"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    List<StudentTeacher> FindStudentTeacherByStudentId(String studentId);

    /**
     * 根据学生id,教師信息id查询
     */
    @Select("SELECT * FROM kg_student_teacher where kg_teacherinformationid=#{teacherId} and kg_studentprofileid=#{studentId} and kg_state = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_teacherinformationid", column = "KG_TEACHERINFORMATIONID"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    StudentTeacher FindStudentTeacher(String teacherId, String studentId);

    /**
     * 學生教師關係新增
     */
    @Insert("insert into kg_student_teacher(kg_studentprofileid,kg_teacherinformationid,kg_state) values (#{kg_studentprofileid},#{kg_teacherinformationid},#{kg_state})")
    void AddStudentTeacher(StudentTeacher studentTeacher);

    /**
     * 學生教師關係刪除
     */
    @Update("update kg_student_teacher set kg_state=1 where kg_teacherinformationid=#{teacherId} and kg_studentprofileid=#{studentId}")
    void DeleteStudentTeacher(String teacherId, String studentId);
}

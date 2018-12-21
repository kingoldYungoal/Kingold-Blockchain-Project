package com.kingold.educationblockchain.dao;

import com.kingold.educationblockchain.bean.StudentParent;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface StudentParentMapper {

    /**
     * 根据家長信息id查询
     */
    @Select("SELECT * FROM kg_student_parent where kg_parentinformationid=#{parentId} and kg_state = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_parentinformationid", column = "KG_PARENTINFORMATIONID"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    List<StudentParent> FindStudentParentByParentId(String parentId);

    /**
     * 根据学生id查询
     */
    @Select("SELECT * FROM kg_student_parent where kg_studentprofileid=#{studentId} and kg_state = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_parentinformationid", column = "KG_PARENTINFORMATIONID"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    List<StudentParent> FindStudentParentByStudentId(String studentId);

    /**
     * 根据学生id,家長信息id查询
     */
    @Select("SELECT * FROM kg_student_parent where kg_parentinformationid=#{parentId} and kg_studentprofileid=#{studentId} and kg_state = 0")
    @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_parentinformationid", column = "KG_PARENTINFORMATIONID"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    StudentParent FindStudentParent(String parentId, String studentId);

    /**
     * 學生家長關係新增
     */
    @Insert("insert into kg_student_parent(kg_studentprofileid,kg_parentinformationid,kg_state) values (#{kg_studentprofileid},#{kg_parentinformationid},#{kg_state})")
    void AddStudentParent(StudentParent studentParent);

    /**
     * 學生家長關係刪除
     */
    @Update("update kg_student_parent set kg_state=1 where kg_parentinformationid=#{parentId} and kg_studentprofileid=#{studentId}")
    void DeleteStudentParent(String parentId, String studentId);
}

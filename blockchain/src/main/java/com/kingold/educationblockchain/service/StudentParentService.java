package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.StudentParent;

import java.util.List;

public interface StudentParentService {

    /**
     * 根据家長信息id查询
     */
    List<StudentParent> FindStudentParentByParentId(String parentId);

    /**
     * 根据学生id查询
     */
    List<StudentParent> FindStudentParentByStudentId(String studentId);

    /**
     * 根据学生id,家長信息id查询
     */
    StudentParent FindStudentParent(String parentId, String studentId);

    /**
     * 學生家長關係新增
     */
    boolean AddStudentParent(StudentParent studentParent);
    
    boolean UpdateStudentParent(StudentParent studentParent);

    /**
     * 學生家長關係刪除
     */
    boolean DeleteStudentParent(String parentId, String studentId);
    
    boolean deleteByStudentId(String studentId);
    
    boolean deleteByParentId(String parentId);
}

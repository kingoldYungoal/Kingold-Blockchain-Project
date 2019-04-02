package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.StudentTeacher;

import java.util.List;

public interface StudentTeacherService {

    /**
     * 根据教師信息id查询
     */
    List<StudentTeacher> FindStudentTeacherByTeacherId(String teacherId);

    /**
     * 根据学生id查询
     */
    List<StudentTeacher> FindStudentTeacherByStudentId(String studentId);

    /**
     * 根据学生id,教師信息id查询
     */
    List<StudentTeacher> FindStudentTeacherByPage(String teacherId, int currentPage,int pageSize);

    /**
     * 根据学生id,教師信息id查询
     */
    StudentTeacher FindStudentTeacher(String teacherId, String studentId);

    /**
     * 學生教師關係新增
     */
    boolean AddStudentTeacher(StudentTeacher studentTeacher);

    /**
     * 學生教師關係更新
     */
    boolean UpdateStudentTeacher(StudentTeacher studentTeacher);
    
    /**
     * 狀態更新
     */
    boolean DeleteStudentTeacher(String teacherId, String studentId);
    
    boolean deleteByStudentId(String studentId);
    
    boolean deleteByTeacherId(String teacherId);
}

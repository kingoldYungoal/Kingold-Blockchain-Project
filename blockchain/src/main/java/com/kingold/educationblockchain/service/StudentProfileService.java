package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.StudentProfile;

import java.util.List;

public interface StudentProfileService {

    /**
     * 根據id查詢學生信息
     */
    StudentProfile GetStudentProfileById(int id);

    /**
     * 根據学籍号或者学号查詢學生信息
     */
    List<StudentProfile> GetStudentProfileByNumber(String eduNumber, String stuNumber);

    /**
     * 根据教師信息id，学生班级查询
     */
    List<StudentProfile> GetStudentsByClassAndTeacher(int teacherId, String classname,int currentPage,int pageSize);

    /**
     * 學生信息新增
     */
    boolean AddStudentProfile(StudentProfile studentProfile);

    /**
     * 學生信息更新
     */
    boolean UpdateStudentProfile(StudentProfile studentProfile);

    /**
     * 學生信息刪除
     */
    boolean DeleteStudentProfile(int id);
}

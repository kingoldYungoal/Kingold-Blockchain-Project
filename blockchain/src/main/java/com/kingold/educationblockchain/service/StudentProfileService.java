package com.kingold.educationblockchain.service;

import java.util.List;

import com.kingold.educationblockchain.bean.PageBean;
import com.kingold.educationblockchain.bean.StudentInfo;
import com.kingold.educationblockchain.bean.StudentProfile;

public interface StudentProfileService {

    /**
     * 根據id查詢學生信息
     */
    StudentProfile GetStudentProfileById(String id);

    /**
     * 根據学籍号或者学号查詢學生信息
     */
    List<StudentProfile> GetStudentProfileByNumber(String eduNumber, String stuNumber);

    /**
     * 根据教師信息id，从证书表取出学生id，获取学生信息
     */
    PageBean<StudentInfo> GetStudentsByParam(String teacherId, String classname, int year, int currentPage, int pageSize);

    /**
     * 根据教師信息id，班级，年份，从证书表取出学生id，获取学生信息，不分页
     * @return 学生信息列表
     */
    List<StudentInfo> GetStudentsByParamNoPage(String teacherId, String classname, int year);

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
    boolean DeleteStudentProfile(String id);

    /**
     * 學生信息从表中刪除
     */
    boolean RelDeleteStudentProfile(String id);
    
    PageBean<StudentInfo> queryStudentsByClassId(String classId, int currentPage,int pageSize);
    
    List<StudentInfo> queryStudentsByClassIdNoPage(String classId);
}

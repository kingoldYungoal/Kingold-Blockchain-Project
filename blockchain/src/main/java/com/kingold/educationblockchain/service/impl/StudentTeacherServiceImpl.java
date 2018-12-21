package com.kingold.educationblockchain.service.impl;

import com.github.pagehelper.PageHelper;
import com.kingold.educationblockchain.bean.PageBean;
import com.kingold.educationblockchain.bean.StudentTeacher;
import com.kingold.educationblockchain.dao.StudentTeacherMapper;
import com.kingold.educationblockchain.service.StudentTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentTeacherServiceImpl implements StudentTeacherService {
    @Autowired
    @Resource
    private StudentTeacherMapper mStudentTeacherMapper;

    /**
     * 根据教師信息id查询
     */
    @Override
    public List<StudentTeacher> FindStudentTeacherByTeacherId(String teacherId){
        return mStudentTeacherMapper.FindStudentTeacherByTeacherId(teacherId);
    }

    /**
     * 根据学生id查询
     */
    @Override
    public List<StudentTeacher> FindStudentTeacherByStudentId(String studentId){
        return mStudentTeacherMapper.FindStudentTeacherByStudentId(studentId);
    }

    /**
     * 根据学生id,教師信息id查询
     */
    @Override
    public List<StudentTeacher> FindStudentTeacherByPage(String teacherId, int currentPage,int pageSize){
        //设置分页信息，分别是当前页数和每页显示的总记录数
        PageHelper.startPage(currentPage, pageSize);

        List<StudentTeacher> allItems = mStudentTeacherMapper.FindStudentTeacherByTeacherId(teacherId);
        int countNums = allItems.size();            //总记录数
        PageBean<StudentTeacher> pageData = new PageBean<>(currentPage, pageSize, countNums);
        pageData.setItems(allItems);
        return pageData.getItems();
    }

    /**
     * 根据学生id,教師信息id查询
     */
    @Override
    public StudentTeacher FindStudentTeacher(String teacherId, String studentId){
        return mStudentTeacherMapper.FindStudentTeacher(teacherId, studentId);
    }

    /**
     * 學生教師關係新增
     */
    @Override
    public boolean AddStudentTeacher(StudentTeacher studentTeacher){
        boolean flag = false;
        try{
            mStudentTeacherMapper.AddStudentTeacher(studentTeacher);
            flag = true;
        }catch(Exception e){
            System.out.println("學生教師關係新增失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 狀態更新
     */
    @Override
    public boolean DeleteStudentTeacher(String teacherId, String studentId){
        boolean flag = false;
        try{
            mStudentTeacherMapper.DeleteStudentTeacher(teacherId, studentId);
            flag = true;
        }catch(Exception e){
            System.out.println("學生教師關係信息失败!");
            e.printStackTrace();
        }
        return flag;
    }
}

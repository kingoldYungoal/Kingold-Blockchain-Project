package com.kingold.educationblockchain.service.impl;

import com.kingold.educationblockchain.bean.TeacherInformation;
import com.kingold.educationblockchain.dao.TeacherInformationMapper;
import com.kingold.educationblockchain.service.TeacherInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TeacherInformationServiceImpl implements TeacherInformationService {

    @Autowired
    @Resource
    private TeacherInformationMapper mTeacherInformationMapper;

    /**
     * 根据教師信息id查询教師信息
     */
    @Override
    public TeacherInformation FindTeacherInformationById(String id){
        return mTeacherInformationMapper.FindTeacherInformationById(id);
    }

    /**
     * 根据教師手机号查询教師信息
     */
    @Override
    public TeacherInformation FindTeacherInformationByPhone(String phone){
        return mTeacherInformationMapper.FindTeacherInformationByPhone(phone);
    }

    /**
     * 教師数据新增
     */
    @Override
    public boolean AddTeacherInformation(TeacherInformation teacherInformation){
        boolean flag = false;
        try{
            mTeacherInformationMapper.AddTeacherInformation(teacherInformation);
            flag = true;
        }catch(Exception e){
            System.out.println("教師信息新增失败!");
            e.printStackTrace();
        }
        return flag;

    }

    /**
     * 教師数据修改
     */
    @Override
    public boolean UpdateTeacherInformation(TeacherInformation teacherInformation){
        boolean flag = false;
        try{
            mTeacherInformationMapper.UpdateTeacherInformation(teacherInformation);
            flag = true;
        }catch(Exception e){
            System.out.println("教師信息更新失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 教師数据删除
     */
    @Override
    public boolean DeleteTeacherInformation(String id){
        boolean flag = false;
        try{
            mTeacherInformationMapper.DeleteTeacherInformation(id);
            flag = true;
        }catch(Exception e){
            System.out.println("教師信息刪除失败!");
            e.printStackTrace();
        }
        return flag;
    }
}

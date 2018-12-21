package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.TeacherInformation;

public interface TeacherInformationService {

    /**
     * 根据教師信息id查询教師信息
     */
    TeacherInformation FindTeacherInformationById(String id);

    /**
     * 根据教師手机号查询教師信息
     */
    TeacherInformation FindTeacherInformationByPhone(String phone);

    /**
     * 教師数据新增
     */
    boolean AddTeacherInformation(TeacherInformation teacherInformation);

    /**
     * 教師数据修改
     */
    boolean UpdateTeacherInformation(TeacherInformation teacherInformation);

    /**
     * 教師数据删除
     */
    boolean DeleteTeacherInformation(String id);
}

package com.kingold.educationblockchain.service.impl;

import com.github.pagehelper.PageHelper;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.bean.paramBean.CertificateParam;
import com.kingold.educationblockchain.dao.StudentProfileMapper;
import com.kingold.educationblockchain.service.ElectronicscertificateService;
import com.kingold.educationblockchain.service.ParentInformationService;
import com.kingold.educationblockchain.service.StudentParentService;
import com.kingold.educationblockchain.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    @Autowired
    @Resource
    private StudentProfileMapper mStudentProfileMapper;

    @Autowired
    private ParentInformationService mParentInfomationService;

    @Autowired
    private StudentParentService mStudentParentService;

    @Autowired
    private ElectronicscertificateService mElectronicscertificateService;

    /**
     * 根據id查詢學生信息
     */
    @Override
    public StudentProfile GetStudentProfileById(String id)
    {
        return mStudentProfileMapper.GetStudentProfileById(id);
    }

    /**
     * 根據学籍号或者学号查詢學生信息
     */
    @Override
    public List<StudentProfile> GetStudentProfileByNumber(String eduNumber, String stuNumber){
        return mStudentProfileMapper.GetStudentProfileByNumber(eduNumber,stuNumber);
    }

    /**
     * 根据教師信息id查询
     */
    @Override
    public PageBean<StudentInfo> GetStudentsByTeacherId(String teacherId, int currentPage, int pageSize){
        //设置分页信息，分别是当前页数和每页显示的总记录数
        List<StudentProfile> allItems = mStudentProfileMapper.GetStudentsByTeacherId(teacherId);
        PageHelper.startPage(currentPage, pageSize);
        List<StudentProfile> pageItems = mStudentProfileMapper.GetStudentsByTeacherId(teacherId);
        //studentinfo封装
        List<StudentInfo> infoList = GetStudentInfoList(pageItems);
        int countNums = allItems.size();            //总记录数
        PageBean<StudentInfo> pageData = new PageBean<>(currentPage, pageSize, countNums);
        pageData.setItems(infoList);
        return pageData;
    }

    /**
     * 根据教師信息id，学生班级查询
     */
    @Override
    public PageBean<StudentInfo> GetStudentsByClassAndTeacher(String teacherId, String classname,int currentPage,int pageSize){
        // 设置分页信息，分别是当前页数和每页显示的总记录数
        PageHelper.startPage(currentPage, pageSize);
        List<StudentProfile> pageItems = mStudentProfileMapper.GetStudentsByClassAndTeacher(teacherId,classname);
        // studentinfo封装
        List<StudentInfo> infoList = GetStudentInfoList(pageItems);
        int countNums = pageItems.size();            //总记录数
        PageBean<StudentInfo> pageData = new PageBean<>(currentPage, pageSize, countNums);
        pageData.setItems(infoList);
        return pageData;
    }

    /**
     * 根据教師信息id，从证书表取出学生id，获取学生信息
     */
    @Override
    public PageBean<StudentInfo> GetStudentsByParam(String teacherId, String classname,int year, int currentPage, int pageSize){
        List<StudentProfile> stus = mStudentProfileMapper.GetStudentsByParam(teacherId,classname,year);

        // 设置分页信息，分别是当前页数和每页显示的总记录数
        PageHelper.startPage(currentPage, pageSize);
        List<StudentProfile> pageItems = mStudentProfileMapper.GetStudentsByParam(teacherId,classname,year);

        // studentinfo封装
        List<StudentInfo> infoList = GetStudentInfoList(pageItems);
        int countNums = stus.size();            //总记录数
        PageBean<StudentInfo> pageData = new PageBean<>(currentPage, pageSize, countNums);
        pageData.setItems(infoList);
        return pageData;
    }

    /**
     * 學生信息新增
     */
    @Override
    public boolean AddStudentProfile(StudentProfile studentProfile)
    {
        boolean flag = false;
        try{
            mStudentProfileMapper.AddStudentProfile(studentProfile);
            flag = true;
        }catch(Exception e){
            System.out.println("學生信息新增失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 學生信息更新
     */
    @Override
    public boolean UpdateStudentProfile(StudentProfile studentProfile)
    {
        boolean flag = false;
        try{
            mStudentProfileMapper.UpdateStudentProfile(studentProfile);
            flag = true;
        }catch(Exception e){
            System.out.println("學生信息更新失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 學生信息刪除
     */
    @Override
    public boolean DeleteStudentProfile(String id)
    {
        boolean flag = false;
        try{
            mStudentProfileMapper.DeleteStudentProfile(id);
            flag = true;
        }catch(Exception e){
            System.out.println("學生信息刪除失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 學生信息从表中刪除
     */
    @Override
    public boolean RelDeleteStudentProfile(String id)
    {
        boolean flag = false;
        try{
            mStudentProfileMapper.RelDeleteStudentProfile(id);
            flag = true;
        }catch(Exception e){
            System.out.println("學生信息刪除失败!");
            e.printStackTrace();
        }
        return flag;
    }

    public List<StudentInfo> GetStudentInfoList(List<StudentProfile> list) {
        List<StudentInfo> infoList = new ArrayList<>();
        if(list.size() > 0){
            for(StudentProfile profile: list){
                StudentInfo info = new StudentInfo();
                info.setKg_studentprofileid(profile.getKg_studentprofileid());
                info.setKg_classname(profile.getKg_classname());
                info.setKg_fullname(profile.getKg_fullname());
                info.setKg_educationnumber(profile.getKg_educationnumber());
                info.setKg_sex(profile.getKg_sex());
                info.setKg_jointime(profile.getKg_jointime().split(" ")[0]);

                List<StudentParent> parents = mStudentParentService.FindStudentParentByStudentId(profile.getKg_studentprofileid());
                if(parents != null && parents.size() > 0){
                    ParentInformation parentInformation = mParentInfomationService.FindParentInformationById(parents.get(0).getKg_parentinformationid());
                    if(parentInformation != null){
                        info.setKg_parentName(parentInformation.getKg_name());
                        info.setKg_parentPhoneNumber(parentInformation.getKg_phonenumber());
                    }else{
                        info.setKg_parentName("");
                        info.setKg_parentPhoneNumber("");
                    }
                }
                else{
                    info.setKg_parentName("");
                    info.setKg_parentPhoneNumber("");
                }
                infoList.add(info);
            }
        }
        return infoList;
    }

    // 对学生id重复的去重
    public ArrayList<Electronicscertificate> RemoveDuplicateStudent(List<Electronicscertificate> certs){
        Set<Electronicscertificate> set = new TreeSet<Electronicscertificate>(new Comparator<Electronicscertificate>() {
            @Override
            public int compare(Electronicscertificate e1, Electronicscertificate e2) {
                //字符串,则按照asicc码升序排列
                return e1.getKg_studentprofileid().compareTo(e2.getKg_studentprofileid());
            }
        });
        set.addAll(certs);
        return new ArrayList<Electronicscertificate>(set);
    }
}

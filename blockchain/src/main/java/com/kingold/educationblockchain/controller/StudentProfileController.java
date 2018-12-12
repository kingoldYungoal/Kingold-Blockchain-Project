package com.kingold.educationblockchain.controller;

import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class StudentProfileController {
    @Autowired
    private StudentProfileService mStudentProfileService;
    @Autowired
    private ParentInformationService mParentInfomationService;
    @Autowired
    private TeacherInformationService mTeacherInfomationService;
    @Autowired
    private StudentParentService mStudentParentService;
    @Autowired
    private StudentTeacherService mStudentTeacherService;
    private Gson gson;

    @RequestMapping(value = "/studentinfo", method = RequestMethod.GET)
    @ResponseBody
    public String GetStudentProfile(@RequestParam(value = "phonenumber", required = true)String phonenumber, @RequestParam(value = "role", required = true)int role) {
        gson = new Gson();
        StudentProfile studentProfile;
        //role 為1，代表家長，為2，代表教師
        if (role == 1) {
            ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phonenumber);
            if (parentInformation == null) {
                //跳转到学生详情页面
                return gson.toJson(new StudentProfile());
            } else {
                List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(parentInformation.getKg_parentinformationid());
                if (studentParents == null || studentParents.size() <= 0) {
                    //跳转到学生详情页面
                    return gson.toJson(new StudentProfile());
                } else {
                    if (studentParents.size() == 1) {
                        studentProfile = mStudentProfileService.GetStudentProfileById(studentParents.get(0).getKg_studentprofileid());
                        //跳转到学生详情页面
                        return gson.toJson(studentProfile);
                    } else {
                        List<StudentProfile> StudentProfileList = new ArrayList<>();
                        for (StudentParent sp : studentParents) {
                            StudentProfileList.add(mStudentProfileService.GetStudentProfileById(sp.getKg_studentprofileid()));
                        }
                        //跳转到学生列表页面
                        return gson.toJson(StudentProfileList);
                    }
                }
            }
        } else {
            //教師身份
            TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phonenumber);
            if (teacherInformation == null) {
                //跳转到学生列表页面
                return gson.toJson(new StudentProfile());
            } else {
                // 分页
                // List<StudentTeacher> studentTeachers = mStudentTeacherService.FindStudentTeacherByPage(teacherInformation.getKg_teacherinformationid(),1,10);
                // 不分页
                List<StudentTeacher> studentTeachers = mStudentTeacherService.FindStudentTeacherByTeacherId(teacherInformation.getKg_teacherinformationid());
                //跳转到学生列表页面
                return GetStudentList(studentTeachers);
            }
        }
    }

    public String GetStudentList(List<StudentTeacher> list){
        gson = new Gson();
        if (list == null || list.size() <= 0) {
            return gson.toJson(new StudentProfile());
        } else {
            List<StudentInfo> StudentInfoList = new ArrayList<>();
            for (StudentTeacher st : list) {
                StudentProfile profile = mStudentProfileService.GetStudentProfileById(st.getKg_studentprofileid());
                StudentInfo info = new StudentInfo();
                if(profile != null){
                    info.setKg_studentprofileid(profile.getKg_studentprofileid());
                    info.setKg_classname(profile.getKg_classname());
                    info.setKg_fullname(profile.getKg_fullname());
                    info.setKg_educationnumber(profile.getKg_educationnumber());
                    info.setKg_sex(profile.getKg_sex());
                }
                List<StudentParent> parents = mStudentParentService.FindStudentParentByStudentId(st.getKg_studentprofileid());
                if(parents != null && parents.size() > 0){
                    ParentInformation parentInformation = mParentInfomationService.FindParentInformationById(parents.get(0).getKg_parentinformationid());
                    if(parentInformation != null){
                        info.setKg_parentName(parentInformation.getKg_name());
                        info.setKg_parentPhoneNumber(parentInformation.getKg_phonenumber());
                    }
                }
                StudentInfoList.add(info);
            }
            return gson.toJson(StudentInfoList);
        }
    }
}

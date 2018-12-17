package com.kingold.educationblockchain.controller;

import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/home")
public class LoginController {
    @Autowired
    private ParentInformationService mParentInfomationService;
    @Autowired
    private TeacherInformationService mTeacherInfomationService;
    @Autowired
    private StudentParentService mStudentParentService;
    @Autowired
    private StudentTeacherService mStudentTeacherService;
    private Gson gson;

    @RequestMapping("/login")
    public String UserLogin(){
        return "/login";
    }

    @RequestMapping(value = "/loginauth",method = RequestMethod.GET)
    @ResponseBody
    public String UserLoginAuth(@RequestParam(value = "phonenumber", required = true)String phonenumber, @RequestParam(value = "role", required = true)int role){
        //role 為1，代表家長，為2，代表教師
        if (role == 1) {
            ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phonenumber);
            if (parentInformation != null) {
                List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(parentInformation.getKg_parentinformationid());
                if (studentParents != null && studentParents.size() > 0) {
                    if(studentParents.size() > 1){
                        return "student/childrenList?phonenumber=" + phonenumber;
                    }else{
                        return "student/studentinfo?id=" + studentParents.get(0).getKg_studentprofileid();
                    }
                }
            }
        }else{
            TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phonenumber);
            if (teacherInformation != null) {
                List<StudentTeacher> studentTeachers = mStudentTeacherService.FindStudentTeacherByTeacherId(teacherInformation.getKg_teacherinformationid());
                if (studentTeachers != null && studentTeachers.size() > 0) {
                    if(studentTeachers.size() > 1){
                        return "student/studentList?phonenumber=" + phonenumber;
                    }else{
                        return "student/studentinfo?id=" + studentTeachers.get(0).getKg_studentprofileid();
                    }
                }
            }
        }
        return "home/login";
    }
}

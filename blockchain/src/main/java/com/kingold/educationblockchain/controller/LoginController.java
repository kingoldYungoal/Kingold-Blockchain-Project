package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import com.kingold.educationblockchain.util.Base64;
import com.kingold.educationblockchain.util.EncrypDES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
@RequestMapping("/login")
public class LoginController {
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

    private EncrypDES des;

    @RequestMapping("/login")
    public String UserLogin(){
        return "/login";
    }

    @RequestMapping(value = "/loginVerify",method = RequestMethod.POST)
    public ModelAndView UserLoginVerify(String phonenumber, int role){
        //role 為1，代表家長，為2，代表教師
        ModelAndView model = new ModelAndView();
        if (role == 1) {
            ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phonenumber);
            if (parentInformation != null) {
                List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(parentInformation.getKg_parentinformationid());
                if (studentParents != null && studentParents.size() > 0) {
                    if(studentParents.size() > 1){
                        List<StudentProfile> StudentProfileList = new ArrayList<>();
                        for(StudentParent sp : studentParents){
                            StudentProfileList.add(mStudentProfileService.GetStudentProfileById(sp.getKg_studentprofileid()));
                        }
                        model.addObject("childrenList",StudentProfileList);
                        model.addObject("parentInformation",parentInformation);
                        model.setViewName("childrenlist");
                        return model;
                    }else{
                        StudentProfile studentprofile = mStudentProfileService.GetStudentProfileById(studentParents.get(0).getKg_studentprofileid());
                        model.addObject("studentprofile",studentprofile);
                        model.addObject("backPage","login");
                        model.setViewName("studentinfoandcerts");
                        return model;
                    }
                }
            }
        }else{
            TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phonenumber);
            if (teacherInformation != null) {
                List<StudentTeacher> studentTeachers = mStudentTeacherService.FindStudentTeacherByTeacherId(teacherInformation.getKg_teacherinformationid());
                if (studentTeachers != null && studentTeachers.size() > 0) {
                    if(studentTeachers.size() > 1){
                        List<StudentInfo> StudentInfoList = StudentInfoList = GetStudentList(studentTeachers);
                        // 获取所有的classname
                        List<StudentInfo> classList = new ArrayList<>();
                        List<String> classes = new ArrayList<>();
                        for(StudentInfo studentInfo: StudentInfoList){
                            if(!classes.contains(studentInfo.getKg_classname())){
                                classes.add(studentInfo.getKg_classname());
                                classList.add(studentInfo);
                            }
                        }
                        model.addObject("classList",classList);
                        model.addObject("studentList",StudentInfoList);
                        model.addObject("teacherInformation", teacherInformation);
                        model.setViewName("studentlist");
                        return model;
                    }else{
                        StudentProfile studentprofile = mStudentProfileService.GetStudentProfileById(studentTeachers.get(0).getKg_studentprofileid());
                        model.addObject("studentprofile",studentprofile);
                        model.addObject("backPage","login");
                        model.setViewName("studentinfoandcerts");
                        return model;
                    }
                }
            }
        }
        model.addObject("loginVerify",false);
        model.setViewName("login");
        return model;
    }

    @RequestMapping(value = "/IsExistPhone", method = RequestMethod.POST)
    @ResponseBody
    public String IsExistPhone(@RequestBody JSONObject params){
        gson = new Gson();
        String phone = params.getString("phone");
        if(phone != null && phone.trim().length() > 0){
            //先从家长信息表中查询此号码
            ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phone);
            if(parentInformation != null){
                return gson.toJson(true);
            }
            //从教师信息表中查询此号码
            TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phone);
            if(teacherInformation != null){
                return gson.toJson(true);
            }
            return gson.toJson(false);
        }else{
            return gson.toJson(false);
        }
    }

    @RequestMapping(value = "/IsExistPhoneByRole", method = RequestMethod.POST)
    @ResponseBody
    public String IsExistPhoneByRole(@RequestBody JSONObject params){
        gson = new Gson();
        String phone = params.getString("phone");
        int role = params.getInteger("role");
        if(phone != null && phone.trim().length() > 0){
            if(role == 1){
                ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phone);
                if(parentInformation != null){
                    return gson.toJson(true);
                }
                return gson.toJson(false);
            }else{
                TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phone);
                if(teacherInformation != null){
                    return gson.toJson(true);
                }
                return gson.toJson(false);
            }
        }else{
            return gson.toJson(false);
        }
    }

    public List<StudentInfo> GetStudentList(List<StudentTeacher> list){
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
                info.setKg_jointime(profile.getKg_jointime());
            }
            List<StudentParent> parents = mStudentParentService.FindStudentParentByStudentId(st.getKg_studentprofileid());
            if(parents != null && parents.size() > 0){
                ParentInformation parentInformation = mParentInfomationService.FindParentInformationById(parents.get(0).getKg_parentinformationid());
                if(parentInformation != null){
                    info.setKg_parentName(parentInformation.getKg_name());
                    info.setKg_parentPhoneNumber(parentInformation.getKg_phonenumber());
                }else{
                    info.setKg_parentName("");
                    info.setKg_parentPhoneNumber("");
                }
            }else{
                info.setKg_parentName("");
                info.setKg_parentPhoneNumber("");
            }
            StudentInfoList.add(info);
        }
        return StudentInfoList;
    }
}

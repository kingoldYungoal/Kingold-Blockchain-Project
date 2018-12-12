package com.kingold.educationblockchain.controller;

import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private static final String  authCode = "Basic aGVsaXgubGl1QG9yYWNsZS5jb206QWJjZDEyMzQ=";

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
    public String GetStudentProfile(@RequestParam(value = "id", required = true)int id, ModelMap map) {
        try {
            map.addAttribute("studentInfo", mStudentProfileService.GetStudentProfileById(id));

            String urlStr="https://yungoal-kingoldcloud.blockchain.ocp.oraclecloud.com:443/restproxy1/bcsgw/rest/v1/transaction/invocation";
            RestTemplate restTemplate=new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            String requestStr="{\"channel\":\"channel1\",\"chaincode\":\"kingold\",\"method\":\"queryEventByEducationNo\",\"args\":[\"edu1\"],\"chaincodeVer\":\"v7\"}";
            headers.add("Content-Type","application/json");
            headers.add("Connection", "keep-alive");
            headers.add("Authorization","Basic aGVsaXgubGl1QG9yYWNsZS5jb206QWJjZDEyMzQ=");
            HttpEntity<String> request1=new HttpEntity<String>(requestStr,headers);
            ResponseEntity<String> respose = restTemplate.postForEntity(urlStr, request1,String.class);

            map.addAttribute("json", respose.getBody());
        }
        catch (HttpClientErrorException ex)
        {
            String s = ex.getResponseBodyAsString();
            return "error";
        }

        return "studentinfoandcerts";
    }

    @RequestMapping(value = "/childrenList", method = RequestMethod.GET)
    @ResponseBody
    public String GetChildrenList(@RequestParam(value = "phonenumber", required = true)String phonenumber) {
        gson = new Gson();
        ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phonenumber);
        List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(parentInformation.getKg_parentinformationid());
        List<StudentProfile> StudentProfileList = new ArrayList<>();
        for (StudentParent sp : studentParents) {
            StudentProfileList.add(mStudentProfileService.GetStudentProfileById(sp.getKg_studentprofileid()));
        }
        return gson.toJson(StudentProfileList);
    }

    @RequestMapping(value = "/studentlist", method = RequestMethod.GET)
    @ResponseBody
    public String GetStudentList(@RequestParam(value = "phonenumber", required = true)String phonenumber) {
        gson = new Gson();
        TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phonenumber);
        // 分页
        // List<StudentTeacher> studentTeachers = mStudentTeacherService.FindStudentTeacherByPage(teacherInformation.getKg_teacherinformationid(),1,10);
        // 不分页
        List<StudentTeacher> studentTeachers = mStudentTeacherService.FindStudentTeacherByTeacherId(teacherInformation.getKg_teacherinformationid());
        //跳转到学生列表页面
        return GetStudentList(studentTeachers);
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

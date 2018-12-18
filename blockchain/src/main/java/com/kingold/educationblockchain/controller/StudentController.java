package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
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
    private Gson gson;


    @RequestMapping(value = "/studentinfo", method = RequestMethod.GET)
    @ResponseBody
    public String GetStudentProfile(@RequestParam(value = "id", required = true)int id, ModelMap map) {
        try {
            map.addAttribute("studentInfo", mStudentProfileService.GetStudentProfileById(id));

            String urlStr="https://yungoal-kingoldcloud.blockchain.ocp.oraclecloud.com:443/restproxy1/bcsgw/rest/v1/transaction/invocation";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            String requestStr="{\"channel\":\"channel1\",\"chaincode\":\"kingold\",\"method\":\"queryEventByEducationNo\",\"args\":[\"edu1\"],\"chaincodeVer\":\"v7\"}";
            headers.add("Content-Type","application/json");
            headers.add("Connection", "keep-alive");
            headers.add("Authorization",authCode);
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
    public String GetStudentList(@RequestParam(value = "teacherphone", required = true)String teacherphone,@RequestParam(value = "classname", required = true)String classname,@RequestParam(value = "pageNum", required = true)int pageNum,@RequestParam(value = "pageSize", required = true)int pageSize) {
        gson = new Gson();
        TeacherInformation teacherInformation;
        PageBean<StudentInfo> studentInfoPage = new PageBean<StudentInfo>();
        if(teacherphone.trim().length() > 0){
            teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(teacherphone);
            if(teacherInformation != null){
                if(classname.trim().length() > 0){
                    studentInfoPage = mStudentProfileService.GetStudentsByClassAndTeacher(teacherInformation.getKg_teacherinformationid(),classname,pageNum,pageSize);
                }else{
                    studentInfoPage = mStudentProfileService.GetStudentsByTeacherId(teacherInformation.getKg_teacherinformationid(),pageNum,pageSize);
                }
            }
        }
        return gson.toJson(studentInfoPage);
    }
}

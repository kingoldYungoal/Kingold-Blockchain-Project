package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("chainCode.channel")
    private  String channel;
    @RequestMapping(value = "/studentinfo", method = RequestMethod.GET)
    @ResponseBody
    public String GetStudentProfile(@RequestParam(value = "id", required = true)int id, ModelMap map) {
        try {
            map.addAttribute("studentInfo", mStudentProfileService.GetStudentProfileById(id));

            CommonController commonController =new CommonController();
            List<CertInfo> json=  commonController.QueryCertByCRMId("crm1",channel);
            //List<CertInfo> json=  commonController.QueryCertByCRMId(request.getParameter("crm_id"),request.getParameter("channel"));
            map.addAttribute("json", json);
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

    @RequestMapping(value = "/studentlist", method = RequestMethod.POST)
    @ResponseBody
    public String GetStudentList(@RequestBody JSONObject params) {
        gson = new Gson();
        String teacherphone = params.getString("teacherphone");
        String classname = params.getString("classname");
        int pageNum = params.getInteger("pageNum");
        int pageSize = params.getInteger("pageSize");
        System.out.println("teacherphone="+teacherphone);
        TeacherInformation teacherInformation;
        List<StudentProfile> studentProfiles = new ArrayList<>();
        if(teacherphone.trim().length() > 0){
            teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(teacherphone);
            System.out.println("teacherInformation="+teacherInformation.getKg_schoolname());
            if(teacherInformation != null){
                System.out.println("pageNum="+pageNum);
                System.out.println("pageSize="+pageSize);
                studentProfiles  = mStudentProfileService.GetStudentsByClassAndTeacher(teacherInformation.getKg_teacherinformationid(),classname,pageNum,pageSize);
            }
        }
        return gson.toJson(studentProfiles);
    }
}

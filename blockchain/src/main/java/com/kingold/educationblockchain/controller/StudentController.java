package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import com.kingold.educationblockchain.util.NullStringToEmptyAdapterFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentProfileService mStudentProfileService;
    @Autowired
    private ParentInformationService mParentInfomationService;
    @Autowired
    private TeacherInformationService mTeacherInfomationService;
    @Autowired
    private StudentParentService mStudentParentService;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private Gson gson;

    @Value("${chainCode.channel}")
    private String channel;

    @RequestMapping(value = "/studentinfo", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView GetStudentProfile(@RequestParam(value = "id", required = true)String id,@RequestParam(value = "roleid", required = true)String roleid,@RequestParam(value = "classid", required = true)String classid,@RequestParam(value = "schoolid", required = true)String schoolid,@RequestParam(value = "role", required = true)int role,@RequestParam(value = "device", required = true)String device, ModelMap map) {
        ModelAndView model = new ModelAndView();
        try {
            StudentProfile studentProfile = mStudentProfileService.GetStudentProfileById(id);
            map.addAttribute("studentInfo", studentProfile);
            model.addObject("studentprofile",studentProfile);

            CommonController commonController = new CommonController();
            List<DisplayInfo> displayInfos = new ArrayList<>();
            List<CertInfo> certJson = commonController.QueryCertByCRMId(id,channel);
            if(certJson != null){
                for (CertInfo cert:certJson){
                    if(cert.getCertType().equals("毕业证书") || cert.getCertType().equals("录取通知书") || cert.getCertType().equals("课程证书")){
                        DisplayInfo x=new DisplayInfo();
                        x.setDisplayCertInfo(cert);
                        try {
                            x.setInfoDate(new SimpleDateFormat("yyyy-MM-dd").parse(cert.getCertIssueDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        displayInfos.add(x);
                    }
                }
                Collections.sort(displayInfos);
            }
            map.addAttribute("certCount", displayInfos.size());
            map.addAttribute("json", displayInfos);
        }
        catch (HttpClientErrorException ex)
        {
            String s = ex.getResponseBodyAsString();
        }

        if(id.trim().length() > 0){
            model.addObject("stuid",id);
        }
        if(roleid.trim().length() > 0){
            model.addObject("roleid", roleid);
        }
        model.addObject("classid", classid);
        model.addObject("schoolid", schoolid);
        model.addObject("role", role);
        if(device.trim().equals("mobile")){
            model.setViewName("mobileStudentInfo");
        }else{
            model.setViewName("studentinfoandcerts");
        }

        return model;
    }

    @RequestMapping(value = "/childrenList", method = RequestMethod.GET)
    @ResponseBody
    public String GetChildrenList(@RequestParam(value = "phonenumber", required = true)String phonenumber) {
    	gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
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
    public String GetStudentList(@RequestParam(value = "classId", required = true)String classId,@RequestParam(value = "pageNum", required = true)int pageNum,@RequestParam(value = "pageSize", required = true)int pageSize) {
    	gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        PageBean<StudentInfo> studentInfoPage = mStudentProfileService.queryStudentsByClassId(classId, pageNum, pageSize);
        return gson.toJson(studentInfoPage);
    }
    
    @RequestMapping(value = "/classlist", method = RequestMethod.GET)
    @ResponseBody
    public String GetClassList(@RequestParam(value = "schoolId", required = true)String schoolId, @RequestParam(value = "teacherId", required = true)String teacherId) {
        gson = new Gson();
        List<ClassInfo> classInfoList = classInfoService.getClassesBySchoolId(teacherId, schoolId);
        return gson.toJson(classInfoList);
    }

    @RequestMapping(value = "/mstudentinfo", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView GetStudentInfo(@RequestParam(value = "stuId", required = true)String stuId, ModelMap map) {
        ModelAndView model = new ModelAndView();
        try {
            StudentProfile studentProfile = mStudentProfileService.GetStudentProfileById(stuId);
            model.addObject("studentInfo",studentProfile);
        }
        catch (HttpClientErrorException ex)
        {
            String s = ex.getResponseBodyAsString();
        }
        model.setViewName("mobileStudentDetailInfo");
        return model;
    }

    @RequestMapping(value = "/mstudentlist", method = RequestMethod.POST)
    @ResponseBody
    public String GetStudentListNoPage(@RequestBody JSONObject params) {
    	gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        String classId = params.getString("classId");
        List<StudentInfo> list = mStudentProfileService.queryStudentsByClassIdNoPage(classId);
        return gson.toJson(list);
    }
}

package com.kingold.educationblockchain.controller;

import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private Gson gson;

    @Value("${chainCode.channel}")
    private String channel;

    @RequestMapping(value = "/studentinfo", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView GetStudentProfile(@RequestParam(value = "id", required = true)String id,@RequestParam(value = "roleid", required = true)String roleid,@RequestParam(value = "role", required = true)int role, ModelMap map) {
        ModelAndView model = new ModelAndView();
        try {
            StudentProfile studentProfile = mStudentProfileService.GetStudentProfileById(id);
            map.addAttribute("studentInfo", studentProfile);
            model.addObject("studentprofile",studentProfile);

            CommonController commonController =new CommonController();
            List<DisplayInfo> displayInfos = new ArrayList<>();
            List<CertInfo> certJson =  commonController.QueryCertByCRMId(id,channel);
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
            map.addAttribute("json", displayInfos);
        }
        catch (HttpClientErrorException ex)
        {
            String s = ex.getResponseBodyAsString();
            model.setViewName("error");
            model.addObject("errorMessage",ex.getMessage());
            return model;
        }

        if(id.trim().length() > 0){
            model.addObject("stuid",id);
        }
        if(roleid.trim().length() > 0){
            model.addObject("roleid", roleid);
        }
        model.addObject("role",role);
        model.setViewName("studentinfoandcerts");
        return model;
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
    public String GetStudentList(@RequestParam(value = "teacherphone", required = true)String teacherphone,@RequestParam(value = "classname", required = true)String classname,@RequestParam(value = "year", required = true)int year,@RequestParam(value = "pageNum", required = true)int pageNum,@RequestParam(value = "pageSize", required = true)int pageSize) {
        gson = new Gson();
        TeacherInformation teacherInformation;
        PageBean<StudentInfo> studentInfoPage = new PageBean<StudentInfo>();
        if(teacherphone.trim().length() > 0){
            teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(teacherphone);
            if(teacherInformation != null){
                studentInfoPage = mStudentProfileService.GetStudentsByParam(teacherInformation.getKg_teacherinformationid(),classname,year,pageNum,pageSize);
            }
        }
        return gson.toJson(studentInfoPage);
    }
}

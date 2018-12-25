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

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    @Value("${chainCode.channel}")
    private String channel;

    @RequestMapping(value = "/studentinfo", method = RequestMethod.GET)
    public ModelAndView GetStudentProfile(@RequestParam(value = "id", required = true)String id, ModelMap map) {
        ModelAndView model = new ModelAndView();
        try {
            StudentProfile studentProfile = mStudentProfileService.GetStudentProfileById(id);
            map.addAttribute("studentInfo", studentProfile);
            model.addObject("studentprofile",studentProfile);

            CommonController commonController =new CommonController();
            List<DisplayInfo> displayInfos=new ArrayList<DisplayInfo>();
            List<CertInfo> certJson=  commonController.QueryCertByCRMId(String.valueOf(id),channel);
            List<EventInfo> eventJson=  commonController.QueryEventByCRMId(String.valueOf(id),channel);
            for (CertInfo cert:certJson
                 ) {
                DisplayInfo x=new DisplayInfo();
                x.setDisplayCertInfo(cert);
                try {
                    x.setInfoDate(new SimpleDateFormat("yyyy-mm-dd").parse(cert.getCertIssueDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                displayInfos.add(x);
            }
            for (EventInfo event:eventJson
            ) {
                DisplayInfo x=new DisplayInfo();
                x.setDisplayEventInfo(event);
                try {
                    x.setInfoDate(new SimpleDateFormat("yyyy-mm-dd").parse(event.getEventDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                displayInfos.add(x);
            }
            Collections.sort(displayInfos);
            map.addAttribute("json", displayInfos);
            //System.out.print(URLEncoder.encode(json.get(8).getCertNo().getBytes("UTF-8").toString(), "UTF-8"));
            model.setViewName("studentinfoandcerts");
        }
        catch (HttpClientErrorException ex)
        {
            String s = ex.getResponseBodyAsString();
        }

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

package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.bean.paramBean.CertificateParam;
import com.kingold.educationblockchain.dao.ElectronicscertificateMapper;
import com.kingold.educationblockchain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Autowired
    private ElectronicscertificateService mElectronicscertificateService;

    private Gson gson;

    @Value("${chainCode.channel}")
    private String channel;

    @RequestMapping("/login")
    public String UserLogin(){
        return "login";
    }

    @RequestMapping(value = "/loginVerify",method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView UserLoginVerify(String phonenumber, int role, ModelMap map){
        //role 為1，代表家長，為2，代表教師
        ModelAndView model = new ModelAndView();
        //HttpSession session = request.getSession(true);
        if (role == 1) {
            ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phonenumber);
            if (parentInformation != null) {
                List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(parentInformation.getKg_parentinformationid());
                if (studentParents != null && studentParents.size() > 0) {
                    if(studentParents.size() > 1){
                        List<StudentProfile> StudentProfileList = new ArrayList<>();
                        try {
                            for (StudentParent sp : studentParents) {
                                StudentProfileList.add(mStudentProfileService.GetStudentProfileById(sp.getKg_studentprofileid()));
                            }
                            model.addObject("childrenList", StudentProfileList);
                            model.addObject("parentInformation", parentInformation);
                            model.setViewName("childrenlist");
                            return model;
                        }
                        catch (HttpClientErrorException ex)
                        {
                            model.addObject("errorMessage",ex.getMessage());
                            model.setViewName("error");
                            return model;
                        }
                    }else{
                        StudentProfile studentprofile = mStudentProfileService.GetStudentProfileById(studentParents.get(0).getKg_studentprofileid());
                        model.addObject("studentprofile",studentprofile);
                        CommonController commonController =new CommonController();
                        List<DisplayInfo> displayInfos = new ArrayList<>();
                        List<CertInfo> certJson =  commonController.QueryCertByCRMId(studentprofile.getKg_studentprofileid(),channel);
                        for (CertInfo cert:certJson){
                            if(cert.getCertType().equals("毕业证书") || cert.getCertType().equals("录取通知书") || cert.getCertType().equals("课程证书")){
                                DisplayInfo x=new DisplayInfo();
                                x.setDisplayCertInfo(cert);
                                try {
                                    x.setInfoDate(new SimpleDateFormat("yyyy-mm-dd").parse(cert.getCertIssueDate()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                displayInfos.add(x);
                            }
                        }
                        Collections.sort(displayInfos);
                        map.addAttribute("json", displayInfos);

                        model.addObject("stuid",studentprofile.getKg_studentprofileid());
                        model.addObject("roleid", "");
                        model.addObject("role", 0);

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
                        // 获取所有的year
                        model.addObject("yearList",GetAllYears());

                        // 根据教师id获取证书表中的所有的classname
                        List<Electronicscertificate> electronicscertificateList = GetAllCertificates(teacherInformation.getKg_teacherinformationid());
                        List<String> classes = new ArrayList<>();
                        if(electronicscertificateList.size() > 0){
                            for(Electronicscertificate cert: electronicscertificateList) {
                                if(!classes.contains(cert.getKg_classname())){
                                    classes.add(cert.getKg_classname());
                                }
                            }
                        }

                        model.addObject("classList",classes);
                        model.addObject("teacherInformation", teacherInformation);
                        model.setViewName("studentlist");
                        return model;
                    }else{
                        StudentProfile studentprofile = mStudentProfileService.GetStudentProfileById(studentTeachers.get(0).getKg_studentprofileid());
                        model.addObject("studentprofile",studentprofile);
                        CommonController commonController =new CommonController();
                        List<DisplayInfo> displayInfos = new ArrayList<>();
                        List<CertInfo> certJson =  commonController.QueryCertByCRMId(studentprofile.getKg_studentprofileid(),channel);
                        for (CertInfo cert:certJson){
                            if(cert.getCertType().equals("毕业证书") || cert.getCertType().equals("录取通知书") || cert.getCertType().equals("课程证书")){
                                DisplayInfo x=new DisplayInfo();
                                x.setDisplayCertInfo(cert);
                                try {
                                    x.setInfoDate(new SimpleDateFormat("yyyy-mm-dd").parse(cert.getCertIssueDate()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                displayInfos.add(x);
                            }
                        }
                        Collections.sort(displayInfos);
                        map.addAttribute("json", displayInfos);

                        model.addObject("stuid",studentprofile.getKg_studentprofileid());
                        model.addObject("roleid", "");
                        model.addObject("role", 0);

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

    @RequestMapping(value = "/BackListPage",method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView BackListPage(String roleid, int role) {
        ModelAndView model = new ModelAndView();
        if (role == 1) {
            ParentInformation parentInformation = mParentInfomationService.FindParentInformationById(roleid);
            List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(roleid);
            List<StudentProfile> StudentProfileList = new ArrayList<>();
            for(StudentParent sp : studentParents){
                StudentProfileList.add(mStudentProfileService.GetStudentProfileById(sp.getKg_studentprofileid()));
            }
            model.addObject("childrenList",StudentProfileList);
            model.addObject("parentInformation",parentInformation);
            model.setViewName("childrenlist");
            return model;
        }else{
            TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationById(roleid);
            List<StudentTeacher> studentTeachers = mStudentTeacherService.FindStudentTeacherByTeacherId(roleid);
            // 获取所有的year
            model.addObject("yearList",GetAllYears());

            // 根据教师id获取证书表中的所有的classname
            List<Electronicscertificate> electronicscertificateList = GetAllCertificates(teacherInformation.getKg_teacherinformationid());
            List<String> classes = new ArrayList<>();
            if(electronicscertificateList.size() > 0){
                for(Electronicscertificate cert: electronicscertificateList) {
                    if(!classes.contains(cert.getKg_classname())){
                        classes.add(cert.getKg_classname());
                    }
                }
            }

            model.addObject("classList",classes);
            model.addObject("teacherInformation", teacherInformation);
            model.setViewName("studentlist");
            return model;
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

    public List<StudentInfo> GetStudentList(List<String> studentIds){
        //获取教师id下的所有证书信息
        List<StudentInfo> StudentInfoList = new ArrayList<>();
        if(studentIds.size() > 0){
            for (String studentId : studentIds) {
                StudentProfile profile = mStudentProfileService.GetStudentProfileById(studentId);
                StudentInfo info = new StudentInfo();
                if(profile != null){
                    info.setKg_studentprofileid(profile.getKg_studentprofileid());
                    info.setKg_classname(profile.getKg_classname());
                    info.setKg_fullname(profile.getKg_fullname());
                    info.setKg_educationnumber(profile.getKg_educationnumber());
                    info.setKg_sex(profile.getKg_sex());
                    info.setKg_jointime(profile.getKg_jointime());
                }
                List<StudentParent> parents = mStudentParentService.FindStudentParentByStudentId(studentId);
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
        }

        return StudentInfoList;
    }

    // 获取所有的班级
    public List<Electronicscertificate> GetAllCertificates(String teacherId){
        CertificateParam param = new CertificateParam();
        param.setTeacherId(teacherId);
        return mElectronicscertificateService.GetCertificatesByParam(param);
    }

    // 获取所有的年份
    public List<Integer> GetAllYears(){
        List<Integer> years = new ArrayList<>();
        for(int i = Integer.parseInt(getDateYear());i > Integer.parseInt(getDateYear())-10;i--){
            years.add(i);
        }
        return years;
    }

    // 获取当前年份
    public String getDateYear() {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return year;
    }
}

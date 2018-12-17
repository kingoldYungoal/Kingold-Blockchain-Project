package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import com.kingold.educationblockchain.util.CertInfo;
import com.kingold.educationblockchain.util.EventInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommonController {
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
//    private Logger logger = Logger.getLogger(CommonController.class);

    @RequestMapping(value = "/Insert", method = RequestMethod.POST)
    public String Insert(@RequestBody String jsonParam, @RequestParam(value = "tablename", required = true)String tablename,@RequestParam(value = "synctype", required = true)String synctype) {
        gson = new Gson();
        if(tablename.trim().length() == 0 || synctype.trim().length() == 0){
            return gson.toJson(false);
        }
        boolean flag = false;
        switch(synctype.trim()){
            case "insert":
                flag = InsertData(tablename, jsonParam);
                break;
            case "update":
                flag = UpdateData(tablename, jsonParam);
                break;
        }
        return gson.toJson(flag);
    }

    @RequestMapping(value = "/Delete", method = RequestMethod.GET)
    public String Delete(@RequestParam(value = "id", required = true)int id, @RequestParam(value = "tablename", required = true)String tablename) {
        gson = new Gson();
        if(tablename.trim().length() == 0 || id <= 0){
            return gson.toJson(false);
        }
        boolean flag = DeleteData(id, tablename);
        return gson.toJson(flag);
    }

    public boolean InsertData(String tableName,String jsonParam){
        boolean flag = false;
        switch(tableName.trim()){
            case "kg_studentprofile":
                StudentProfile studentProfile = JSONObject.parseObject(jsonParam,StudentProfile.class);
                // 判断学号，学籍号 是否已存在
                List<StudentProfile> studentProfileList = mStudentProfileService.GetStudentProfileByNumber(studentProfile.getKg_educationnumber(),studentProfile.getKg_studentnumber());
                if(studentProfileList == null || studentProfileList.size() <= 0){
                    flag = mStudentProfileService.AddStudentProfile(studentProfile);
                }
                break;
            case "kg_teacherinformation":
                TeacherInformation teacherInformation= JSONObject.parseObject(jsonParam,TeacherInformation.class);
                // 判断教师信息是否存在
                if(mTeacherInfomationService.FindTeacherInformationByPhone(teacherInformation.getKg_phonenumber()) == null) {
                    flag = mTeacherInfomationService.AddTeacherInformation(teacherInformation);
                }
                break;
            case "kg_parentinformation":
                ParentInformation parentInformation= JSONObject.parseObject(jsonParam,ParentInformation.class);
                // 判断家长信息是否存在
                if(mParentInfomationService.FindParentInformationByPhone(parentInformation.getKg_phonenumber()) == null){
                    flag = mParentInfomationService.AddParentInformation(parentInformation);
                }
                break;
            case "kg_student_parent":
                StudentParent studentParent= JSONObject.parseObject(jsonParam,StudentParent.class);
                //判断 是否存在 家长信息，学生信息
                StudentProfile profile = mStudentProfileService.GetStudentProfileById(studentParent.getKg_studentprofileid());
                ParentInformation pInformation = mParentInfomationService.FindParentInformationById(studentParent.getKg_parentinformationid());
                if(profile != null && pInformation != null){
                    //判断 之前并不存在 家长和学生关系信息
                    StudentParent sp = mStudentParentService.FindStudentParent(studentParent.getKg_parentinformationid(), studentParent.getKg_studentprofileid());
                    if(sp == null){
                        flag = mStudentParentService.AddStudentParent(studentParent);
                    }
                }
                break;
            case "kg_student_teacher":
                StudentTeacher studentTeacher= JSONObject.parseObject(jsonParam,StudentTeacher.class);
                //判断 是否存在 教师信息，学生信息
                StudentProfile profile2 = mStudentProfileService.GetStudentProfileById(studentTeacher.getKg_studentprofileid());
                TeacherInformation tInformation = mTeacherInfomationService.FindTeacherInformationById(studentTeacher.getKg_teacherinformationid());
                if(profile2 != null && tInformation != null){
                    //判断 之前并不存在 教师和学生关系信息
                    StudentTeacher st = mStudentTeacherService.FindStudentTeacher(studentTeacher.getKg_teacherinformationid(), studentTeacher.getKg_studentprofileid());
                    if(st == null){
                        flag = mStudentTeacherService.AddStudentTeacher(studentTeacher);
                    }
                }
                break;
        }
        return flag;
    }

    public boolean UpdateData(String tableName,String jsonParam){
        boolean flag = false;
        // 只会对 学生信息表，教师信息表，家长信息表进行更新操作，对于 状态更新，为删除操作
        switch(tableName.trim()){
            case "kg_studentprofile":
                StudentProfile studentProfile = JSONObject.parseObject(jsonParam,StudentProfile.class);
                //判断学生信息是否存在
                if(studentProfile.getKg_studentprofileid() > 0){
                    if(mStudentProfileService.GetStudentProfileById(studentProfile.getKg_studentprofileid()) != null){
                        flag = mStudentProfileService.UpdateStudentProfile(studentProfile);
                    }
                }
                break;
            case "kg_teacherinformation":
                TeacherInformation teacherInformation= JSONObject.parseObject(jsonParam,TeacherInformation.class);
                //判断教师信息是否存在
                if(teacherInformation.getKg_teacherinformationid() > 0){
                    if(mTeacherInfomationService.FindTeacherInformationById(teacherInformation.getKg_teacherinformationid()) != null){
                        flag = mTeacherInfomationService.UpdateTeacherInformation(teacherInformation);
                    }
                }
                break;
            case "kg_parentinformation":
                ParentInformation parentInformation= JSONObject.parseObject(jsonParam,ParentInformation.class);
                //判断教师信息是否存在
                if(parentInformation.getKg_parentinformationid() > 0){
                    if(mParentInfomationService.FindParentInformationById(parentInformation.getKg_parentinformationid()) != null){
                        flag = mParentInfomationService.UpdateParentInformation(parentInformation);
                    }
                }
                break;
        }
        return flag;
    }

    public boolean DeleteData(int id,String tableName){
        boolean flag = false;
        switch(tableName.trim()){
            case "kg_studentprofile":
                StudentProfile studentProfile = mStudentProfileService.GetStudentProfileById(id);
                if(studentProfile != null){
                    // 删除学生信息，同时删除学生教师关系信息，学生家长关系信息
                    if(mStudentProfileService.DeleteStudentProfile(id)){
                        List<StudentTeacher> stList = mStudentTeacherService.FindStudentTeacherByStudentId(id);
                        if(stList != null && stList.size() > 0){
                            for(StudentTeacher st : stList){
                                mStudentTeacherService.DeleteStudentTeacher(st.getKg_teacherinformationid(),st.getKg_studentprofileid());
                            }
                        }
                        List<StudentParent> spList = mStudentParentService.FindStudentParentByStudentId(id);
                        if(spList != null && spList.size() > 0){
                            for(StudentParent sp : spList){
                                mStudentParentService.DeleteStudentParent(sp.getKg_parentinformationid(),sp.getKg_studentprofileid());
                            }
                        }
                        flag = true;
                    }
                }
                break;
            case "kg_teacherinformation":
                TeacherInformation teacherInformation= mTeacherInfomationService.FindTeacherInformationById(id);
                if(teacherInformation != null){
                    // 删除教师信息，同时删除学生教师关系信息
                    if(mTeacherInfomationService.DeleteTeacherInformation(id)){
                        List<StudentTeacher> stList = mStudentTeacherService.FindStudentTeacherByTeacherId(id);
                        if(stList != null && stList.size() > 0){
                            for(StudentTeacher st : stList){
                                mStudentTeacherService.DeleteStudentTeacher(st.getKg_teacherinformationid(),st.getKg_studentprofileid());
                            }
                        }
                        flag = true;
                    }
                }
                break;
            case "kg_parentinformation":
                ParentInformation parentInformation= mParentInfomationService.FindParentInformationById(id);
                if(parentInformation != null){
                    // 删除家长信息，同时删除学生家长关系信息
                    if(mParentInfomationService.DeleteParentInformation(id)){
                        List<StudentParent> spList = mStudentParentService.FindStudentParentByParentId(id);
                        if(spList != null && spList.size() > 0){
                            for(StudentParent sp : spList){
                                mStudentParentService.DeleteStudentParent(sp.getKg_parentinformationid(),sp.getKg_studentprofileid());
                            }
                        }
                        flag = true;
                    }
                }
                break;
        }
        return flag;
    }

    //blockchain api
    public List<CertInfo> QueryCertByCRMId(String CrmId,String channelName) {
        try {
            JsonArray jsonArray=getPayload("queryCertByCRMId",'"'+CrmId+'"',channelName).getAsJsonArray();
            Iterator<JsonElement> it =jsonArray.iterator();
            Gson  gson=new Gson();
            List<CertInfo> certInfoList=new ArrayList<CertInfo>();
            while(it.hasNext())
            {
                String str =  it.next().getAsJsonObject().get("valueJson").getAsString();
                gson.fromJson(str, CertInfo.class);
                CertInfo obj= gson.fromJson(str, CertInfo.class);
                certInfoList.add(obj);
            }
            return certInfoList;
        } catch (HttpClientErrorException ex) {
            throw ex;
        }
    }
    public List<EventInfo> QueryEventByCRMId(String CrmId, String channelName) {
        try {

            JsonArray jsonArray= getPayload("queryEventByCRMId",'"'+CrmId+'"',channelName).getAsJsonArray();
            Iterator<JsonElement> it =jsonArray.iterator();
            Gson  gson=new Gson();
            List<EventInfo> eventInfoList=new ArrayList<EventInfo>();
            while(it.hasNext())
            {
                String str =  it.next().getAsJsonObject().get("valueJson").getAsString();
                gson.fromJson(str, CertInfo.class);
                EventInfo obj= gson.fromJson(str, EventInfo.class);
                eventInfoList.add(obj);
            }
            return eventInfoList;
        } catch (HttpClientErrorException ex) {
            throw ex;
        }
    }
    private JsonElement getPayload(String functionName,String argJson,String  channelName)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String requestStr = String.format("{\"channel\":\"%s\",\"chaincode\":\"%s\",\"method\":\"%s\",\"args\":[%s],\"chaincodeVer\":\"%s\"}",
                channelName,
                ChainCodeConfig.getProperty("chainCode.chainCodeName"),
                functionName,
                argJson,
                ChainCodeConfig.getProperty("chainCode.chainCodeVer"));
        headers.add("Content-Type", "application/json");
        headers.add("Connection", "keep-alive");
        headers.add("Authorization", ChainCodeConfig.getProperty("chainCode.authorizationKey"));

        HttpEntity<String> request1 = new HttpEntity<String>(requestStr, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(ChainCodeConfig.getProperty("chainCode.hostUrl"), request1, String.class);
        JsonParser parse= new JsonParser();
        JsonObject jsonObject= (JsonObject) parse.parse(response.getBody());
        String payload=jsonObject.getAsJsonObject("result").get("payload").getAsString();
        return parse.parse(payload);
    }
}

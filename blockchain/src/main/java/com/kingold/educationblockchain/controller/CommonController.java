package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import com.kingold.educationblockchain.bean.CertInfo;
import com.kingold.educationblockchain.bean.EventInfo;
import com.kingold.educationblockchain.util.Base64;
import com.kingold.educationblockchain.util.BlockChainPayload;
import com.kingold.educationblockchain.util.DateHandler;
import com.kingold.educationblockchain.util.EncrypDES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;


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
    @Value("${chainCode.channel}")
    private String channel;
    private DateHandler dateHandler;
    private BlockChainPayload payload = new BlockChainPayload();
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
    public String Delete(@RequestParam(value = "id", required = true)String id, @RequestParam(value = "tablename", required = true)String tablename) {
        gson = new Gson();
        if(tablename.trim().length() == 0 || id.trim().length() == 0){
            return gson.toJson(false);
        }
        return gson.toJson(DeleteData(id, tablename));
    }

    public boolean InsertData(String tableName,String jsonParam){
        boolean flag = false;
        switch(tableName.trim()){
            case "kg_studentprofile":
                StudentProfile studentProfile = JSONObject.parseObject(jsonParam,StudentProfile.class);
                studentProfile.setKg_studentprofileid(UUID.randomUUID().toString());
                // 判断学号，学籍号 是否已存在
                List<StudentProfile> studentProfileList = mStudentProfileService.GetStudentProfileByNumber(studentProfile.getKg_educationnumber(),studentProfile.getKg_studentnumber());
                if(studentProfileList == null || studentProfileList.size() <= 0){
                    //将学生信息添加到数据库
                    try{

                        boolean tableflag = mStudentProfileService.AddStudentProfile(studentProfile);
                        if(tableflag){
                            //将学生信息上链
                            StudentJson studentJson = new StudentJson();
                            //敏感信息加密
//                            des = new EncrypDES();
//                            byte[] educationNo = des.Encrytor(studentProfile.getKg_educationnumber());
//                            byte[] cardNo = des.Encrytor(studentProfile.getKg_passportnumberoridnumber());
                            studentJson.setStudentId(studentProfile.getKg_studentprofileid());
                            studentJson.setCrmId(studentProfile.getKg_studentprofileid());
//                            studentJson.setStudentEducationNo(new String(educationNo));
//                            studentJson.setStudentIdCardNo(new String(cardNo));
                            studentJson.setStudentEducationNo(studentProfile.getKg_educationnumber());
                            studentJson.setStudentIdCardNo(studentProfile.getKg_passportnumberoridnumber());
                            studentJson.setStudentNameString(studentProfile.getKg_fullname());
                            dateHandler = new DateHandler();
                            studentJson.setStudentOperationTime(dateHandler.GetCurrentTime());
                            //studentJson.setRemark();
                            System.out.print("channel=" + channel);
                            InitStudent(studentJson, channel);

                            flag = true;
                        }
                    }catch (HttpClientErrorException e1) {
                        //删除表中新增的数据
                        mStudentProfileService.DeleteStudentProfile(studentProfile.getKg_studentprofileid());
                    }catch (Exception ex){
                        //throw ex;
                    }
                }
                break;
            case "kg_teacherinformation":
                try{
                    TeacherInformation teacherInformation = JSONObject.parseObject(jsonParam,TeacherInformation.class);
                    teacherInformation.setKg_teacherinformationid(UUID.randomUUID().toString());
                    // 判断教师信息是否存在
                    if(mTeacherInfomationService.FindTeacherInformationByPhone(teacherInformation.getKg_phonenumber()) == null) {
                        flag = mTeacherInfomationService.AddTeacherInformation(teacherInformation);
                    }
                }catch(Exception ex){
                    throw ex;
                }
                break;
            case "kg_parentinformation":
                try{
                    ParentInformation parentInformation = JSONObject.parseObject(jsonParam,ParentInformation.class);
                    parentInformation.setKg_parentinformationid(UUID.randomUUID().toString());
                    // 判断家长信息是否存在
                    if(mParentInfomationService.FindParentInformationByPhone(parentInformation.getKg_phonenumber()) == null){
                        flag = mParentInfomationService.AddParentInformation(parentInformation);
                    }
                }catch(Exception ex){
                    throw ex;
                }
                break;
            case "kg_student_parent":
                try{
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
                }catch(Exception ex){
                    throw ex;
                }
                break;
            case "kg_student_teacher":
                try{
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
                }catch(Exception ex){
                    throw ex;
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
                if(studentProfile.getKg_studentprofileid().trim().length() > 0){
                    if(mStudentProfileService.GetStudentProfileById(studentProfile.getKg_studentprofileid()) != null){
                        flag = mStudentProfileService.UpdateStudentProfile(studentProfile);
                    }
                }
                break;
            case "kg_teacherinformation":
                TeacherInformation teacherInformation= JSONObject.parseObject(jsonParam,TeacherInformation.class);
                //判断教师信息是否存在
                if(teacherInformation.getKg_teacherinformationid().trim().length() > 0){
                    if(mTeacherInfomationService.FindTeacherInformationById(teacherInformation.getKg_teacherinformationid()) != null){
                        flag = mTeacherInfomationService.UpdateTeacherInformation(teacherInformation);
                    }
                }
                break;
            case "kg_parentinformation":
                ParentInformation parentInformation= JSONObject.parseObject(jsonParam,ParentInformation.class);
                //判断教师信息是否存在
                if(parentInformation.getKg_parentinformationid().trim().length() > 0){
                    if(mParentInfomationService.FindParentInformationById(parentInformation.getKg_parentinformationid()) != null){
                        flag = mParentInfomationService.UpdateParentInformation(parentInformation);
                    }
                }
                break;
        }
        return flag;
    }

    public boolean DeleteData(String id,String tableName){
        boolean flag = false;
        if(id.trim().length() > 0){
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
        }
        return flag;
    }

    //blockchain api

    /*
    学生信息上链
     */
    public String InitStudent(StudentJson studentJson,String channelName) {
        try {
            return payload.GetPayload("initStudent", getInsertStudentJson(studentJson),channelName).toString();
        }catch (HttpClientErrorException e1) {
            return e1.getMessage();
        }
    }

    /*
    事件信息上链
     */
    public String InsertEventinfo(EventInfo eventInfo,String channelName) {
        try {
            return payload.GetPayload("insertEventInfo", getInsertEventJson(eventInfo), channelName).toString();
        } catch (HttpClientErrorException ex) {
            throw ex;
        }
    }

    /*
    通过crmid查询学生所有证书
     */
    public List<CertInfo> QueryCertByCRMId(String CrmId,String channelName) {
        try {
            JsonArray jsonArray = payload.GetPayload("queryCertByCRMId",'"'+CrmId+'"',channelName).getAsJsonArray();
            Iterator<JsonElement> it =jsonArray.iterator();
            gson = new Gson();
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
        } catch(Exception e){
            return null;
        }
    }

    /*
    通过crmid查询学生所有事件
     */
    public List<EventInfo> QueryEventByCRMId(String CrmId, String channelName) {
        try {

            JsonArray jsonArray = payload.GetPayload("queryEventByCRMId",'"'+CrmId+'"',channelName).getAsJsonArray();
            Iterator<JsonElement> it =jsonArray.iterator();
            gson = new Gson();
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

    /*
    通过证书id查询证书详细信息
     */
    public CertInfo QueryCertById(String certId, String channelName) {
        try {

            String jsonString = payload.GetPayload("readCert",'"'+certId+'"',channelName).toString();
            gson = new Gson();
            CertInfo certInfo = gson.fromJson(jsonString,CertInfo.class);
            return certInfo;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /*
    通过事件id查询证书详细信息
     */
    public EventInfo QueryEventById(String evtId, String channelName) {
        try {

            String jsonString = payload.GetPayload("readEvent",'"'+evtId+'"',channelName).toString();
            gson = new Gson();
            EventInfo evtInfo = gson.fromJson(jsonString,EventInfo.class);
            return evtInfo;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private String getInsertEventJson(EventInfo event)
    {
        return  String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                event.getEventId() ,
                event.getStudentId(),
                event.getEventContent(),
                event.getEventDate(),
                event.getEventOrg(),
                event.getEventOperationTime(),
                event.getRemark());
    }
    private String getInsertStudentJson(StudentJson student)
    {
        return  String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                student.getStudentId(),
                student.getCrmId(),
                student.getStudentEducationNo(),
                student.getStudentIdCardNo(),
                student.getStudentNameString(),
                student.getStudentOperationTime(),
                student.getRemark());
    }
}

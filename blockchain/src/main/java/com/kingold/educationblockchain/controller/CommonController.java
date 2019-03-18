package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;
import com.kingold.educationblockchain.bean.CertInfo;
import com.kingold.educationblockchain.bean.EventInfo;
import com.kingold.educationblockchain.util.*;
import com.kingold.educationblockchain.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static com.kingold.educationblockchain.util.ResultResponse.makeErrRsp;
import static com.kingold.educationblockchain.util.ResultResponse.makeOKRsp;


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
    @Autowired
    private ErrorLogService mErrorLogService;

    @Value("${chainCode.channel}")
    private String channel;

    @Value("${schoolName.schools}")
    private String schools;

    private DateHandler dateHandler;
    private BlockChainPayload payload = new BlockChainPayload();
    private StringWriter stringWriter = new StringWriter();
    private PrintWriter printWriter = new PrintWriter(stringWriter);
    private LoggerException loggerException = new LoggerException();
    private RecordErrorLog recordErrorLog = new RecordErrorLog();
    private Gson gson;

    @RequestMapping(value = "/Insert", method = RequestMethod.POST)
    public RetResult Insert(@RequestBody String jsonParam) {
        try{
            JSONObject jsonx = JSONObject.parseObject(jsonParam);
            String tablename = jsonx.getString("table_name");
            String synctype = jsonx.getString("sync_type");
            String fields = jsonx.getString("fields");
            if(tablename.trim().length() == 0 || synctype.trim().length() == 0){
                return makeErrRsp("表名或者同步类型不能为空");
            }
            Map map = new HashMap();
            switch(synctype.trim()){
                case "insert":
                    map = InsertData(tablename, fields);
                    break;
                case "update":
                    map = UpdateData(tablename, fields);
                    break;
            }
            boolean flag = (boolean)map.get("flag");
            String message = map.get("message").toString();
            if(flag){
                return makeOKRsp(message);
            }else {
                return makeErrRsp(message);
            }
        }catch(Exception ex){
            return makeErrRsp(ex.getMessage());
        }
    }

    @RequestMapping(value = "/Delete", method = RequestMethod.GET)
    public RetResult Delete(@RequestParam(value = "id", required = true)String id, @RequestParam(value = "tablename", required = true)String tablename) {
        if(tablename.trim().length() == 0 || id.trim().length() == 0){
            return makeErrRsp("表名或者同步类型不能为空");
        }
        Map map = new HashMap();
        map = DeleteData(id, tablename);
        boolean flag = (boolean)map.get("flag");
        String message = map.get("message").toString();
        if(flag){
            return makeOKRsp(message);
        }else {
            return makeErrRsp(message);
        }
    }

    public Map InsertData(String tableName,String jsonParam){
        Map map = new HashMap();
        boolean flag = false;
        String message = "";
        switch(tableName.trim()){
            case "kg_studentprofile":
                StudentProfile studentProfile = JSONObject.parseObject(jsonParam,StudentProfile.class);
                message = ValidateStudent(studentProfile);
                if(message.equals("")){
                    // 判断学生 是否已存在
                    StudentProfile student = mStudentProfileService.GetStudentProfileById(studentProfile.getKg_studentprofileid());
                    if(student == null){
                        //将学生信息添加到数据库
                        try{
                            boolean tableflag = mStudentProfileService.AddStudentProfile(studentProfile);
                            if(tableflag){
                                //将学生信息上链
                                StudentJson studentJson = new StudentJson();
                                //敏感信息加密
                                studentJson.setStudentId(studentProfile.getKg_studentprofileid());
                                studentJson.setCrmId(studentProfile.getKg_studentprofileid());
                                studentJson.setStudentEducationNo(Base64.encryptBASE64(studentProfile.getKg_educationnumber().getBytes()).replace("\r\n",""));
                                studentJson.setStudentIdCardNo(Base64.encryptBASE64(studentProfile.getKg_passportnumberoridnumber().getBytes()).replace("\r\n",""));
                                studentJson.setStudentNameString(studentProfile.getKg_fullname());
                                dateHandler = new DateHandler();
                                studentJson.setStudentOperationTime(dateHandler.GetCurrentTime());
                                //studentJson.setRemark();
                                try{
                                    InitStudent(studentJson, channel);
                                }catch (Exception ex){
                                    ex.printStackTrace(printWriter);
                                    ex.getMessage();
                                    loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
                                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
                                }

                                flag = true;
                                message = "学生信息添加成功";
                            }
                        }catch (HttpClientErrorException e1) {
                            //删除表中新增的数据
                            mStudentProfileService.DeleteStudentProfile(studentProfile.getKg_studentprofileid());
                            message = e1.getMessage();
                            e1.printStackTrace(printWriter);
                            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, e1, stringWriter.toString());
                            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e1, jsonParam, "", "", stringWriter.toString()));
                        }catch (Exception ex){
                            mStudentProfileService.DeleteStudentProfile(studentProfile.getKg_studentprofileid());
                            message = ex.getMessage();
                            ex.printStackTrace(printWriter);
                            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
                            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
                        }
                    }else{
                        //更新学生信息
                        flag = mStudentProfileService.UpdateStudentProfile(studentProfile);
                        if(flag){
                            message = "学生信息更新成功";
                        }else{
                            message = "学生信息更新失败";
                        }
                    }
                }
                break;
            case "kg_teacherinformation":
                try{
                    TeacherInformation teacherInformation = JSONObject.parseObject(jsonParam,TeacherInformation.class);
                    TeacherInformation tInfo = mTeacherInfomationService.FindTeacherInformationById(teacherInformation.getKg_teacherinformationid());
                    TeacherInformation tInfo2 = mTeacherInfomationService.FindTeacherInformationByPhone(teacherInformation.getKg_phonenumber());
                    if(tInfo == null){
                        if(tInfo2 == null) {
                            flag = mTeacherInfomationService.AddTeacherInformation(teacherInformation);
                            if(flag){
                                message = "教师信息添加成功";
                            }else{
                                message = "教师信息添加失败";
                            }
                        }else{
                            message = "该教师手机号已存在，无法再次添加";
                        }
                    }else{
                        //更新
                        if(tInfo2 != null && !tInfo2.getKg_teacherinformationid().equals(teacherInformation.getKg_teacherinformationid())) {
                            message = "该教师手机号已存在，无法更新为此号码";
                        }else{
                            flag = mTeacherInfomationService.UpdateTeacherInformation(teacherInformation);
                            if(flag){
                                message = "教师信息更新成功";
                            }else{
                                message = "教师信息更新失败";
                            }
                        }
                    }
                }catch(Exception ex){
                    message = ex.getMessage();
                    ex.printStackTrace(printWriter);
                    loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
                    throw ex;
                }
                break;
            case "kg_parentinformation":
                try{
                    ParentInformation parentInformation = JSONObject.parseObject(jsonParam,ParentInformation.class);
                    ParentInformation pInfo = mParentInfomationService.FindParentInformationById(parentInformation.getKg_parentinformationid());
                    ParentInformation pInfo2 = mParentInfomationService.FindParentInformationByPhone(parentInformation.getKg_phonenumber());
                    if(pInfo == null){
                        if(pInfo2 == null) {
                            flag = mParentInfomationService.AddParentInformation(parentInformation);
                            if(flag){
                                message = "家长信息添加成功";
                            }else{
                                message = "家长信息添加失败";
                            }
                        }else{
                            message = "该家长手机号已存在，无法再次添加";
                        }
                    }else{
                        //更新
                        if(pInfo2 != null && !pInfo2.getKg_parentinformationid().equals(parentInformation.getKg_parentinformationid())) {
                            message = "该家长手机号已存在，无法更新为此号码";
                        }else{
                            flag = mParentInfomationService.UpdateParentInformation(parentInformation);
                            if(flag){
                                message = "家长信息更新成功";
                            }else{
                                message = "家长信息更新失败";
                            }
                        }
                    }
                }catch(Exception ex){
                    message = ex.getMessage();
                    ex.printStackTrace(printWriter);
                    loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
                    throw ex;
                }
                break;
            case "kg_student_parent":
                try{
                    StudentParent studentParent = JSONObject.parseObject(jsonParam,StudentParent.class);
                    //判断 是否存在 家长信息，学生信息
                    StudentProfile profile = mStudentProfileService.GetStudentProfileById(studentParent.getKg_studentprofileid());
                    ParentInformation pInformation = mParentInfomationService.FindParentInformationById(studentParent.getKg_parentinformationid());
                    if(profile != null && pInformation != null){
                        //判断 之前并不存在 家长和学生关系信息
                        StudentParent sp = mStudentParentService.FindStudentParent(studentParent.getKg_parentinformationid(), studentParent.getKg_studentprofileid());
                        if(sp == null){
                            flag = mStudentParentService.AddStudentParent(studentParent);
                            if(flag){
                                message = "家长学生信息添加成功";
                            }else{
                                message = "家长学生信息添加失败";
                            }
                        }else{
                            flag = true;
                            message = "已存在该家长和该学生关系信息";
                        }
                    }else{                        message = "暂无该家长或该学生信息，无法添加";
                    }
                }catch(Exception ex){
                    message = ex.getMessage();
                    ex.printStackTrace(printWriter);
                    loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
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
                            if(flag){
                                message = "教师学生信息添加成功";
                            }else{
                                message = "教师学生信息添加失败";
                            }
                        }else{
                            flag = true;
                            message = "已存在该教师和该学生关系信息";
                        }
                    }else{
                        message = "暂无该教师或该学生信息，无法添加";
                    }
                }catch(Exception ex){
                    message = ex.getMessage();
                    ex.printStackTrace(printWriter);
                    loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
                    throw ex;
                }
                break;
        }
        map.put("flag", flag);
        map.put("message", message);
        return map;
    }

    public Map UpdateData(String tableName,String jsonParam){
        Map map = new HashMap();
        boolean flag = false;
        String message = "";
        // 只会对 学生信息表，教师信息表，家长信息表进行更新操作，对于 状态更新，为删除操作
        switch(tableName.trim()){
            case "kg_studentprofile":
                StudentProfile studentProfile = JSONObject.parseObject(jsonParam,StudentProfile.class);
                message = ValidateStudent(studentProfile);
                if(message.equals("")){
                    if(mStudentProfileService.GetStudentProfileById(studentProfile.getKg_studentprofileid()) != null){
                        flag = mStudentProfileService.UpdateStudentProfile(studentProfile);
                        if(flag){
                            message = "学生信息更新成功";
                        }else{
                            message = "学生信息更新失败";
                        }
                    }else{
                        message = "该学生不存在,无法进行更新操作";
                    }
                }
                break;
            case "kg_teacherinformation":
                TeacherInformation teacherInformation = JSONObject.parseObject(jsonParam,TeacherInformation.class);
                //判断教师信息是否存在
                if(teacherInformation.getKg_teacherinformationid().trim().length() > 0){
                    if(mTeacherInfomationService.FindTeacherInformationById(teacherInformation.getKg_teacherinformationid()) != null){
                        //判断要更新的教师手机号是否存在
                        if(mTeacherInfomationService.FindTeacherInformationByPhone(teacherInformation.getKg_phonenumber()) != null && !mTeacherInfomationService.FindTeacherInformationByPhone(teacherInformation.getKg_phonenumber()).getKg_teacherinformationid().equals(teacherInformation.getKg_teacherinformationid())){
                            message = "教师手机号已存在，无法重复添加";
                        }else{
                            flag = mTeacherInfomationService.UpdateTeacherInformation(teacherInformation);
                            if(flag){
                                message = "教师信息更新成功";
                            }else{
                                message = "教师信息更新失败";
                            }
                        }
                    }else{
                        message = "该教师不存在,无法进行更新操作";
                    }
                }else {
                    message = "教师crmid不能为空,无法进行更新操作";
                }
                break;
            case "kg_parentinformation":
                ParentInformation parentInformation = JSONObject.parseObject(jsonParam,ParentInformation.class);
                //判断教师信息是否存在
                if(parentInformation.getKg_parentinformationid().trim().length() > 0){
                    if(mParentInfomationService.FindParentInformationById(parentInformation.getKg_parentinformationid()) != null){
                        if(mParentInfomationService.FindParentInformationByPhone(parentInformation.getKg_phonenumber()) != null && !mParentInfomationService.FindParentInformationByPhone(parentInformation.getKg_phonenumber()).getKg_parentinformationid().equals(parentInformation.getKg_parentinformationid())){
                            message = "家长手机号已存在，无法重复添加";
                        }else{
                            flag = mParentInfomationService.UpdateParentInformation(parentInformation);
                            if(flag){
                                message = "家长信息更新成功";
                            }else{
                                message = "家长信息更新失败";
                            }
                        }
                    }else{
                        message = "该家长不存在,无法进行更新操作";
                    }
                }else{
                    message = "家长crmid不能为空,无法进行更新操作";
                }
                break;
        }
        map.put("flag", flag);
        map.put("message", message);
        return map;
    }

    public Map DeleteData(String id,String tableName){
        Map map = new HashMap();
        boolean flag = false;
        String message = "";
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
                            message = "学生信息删除成功";
                        }else{
                            message = "学生信息删除失败";
                        }
                    }else{
                        message = "学生信息不存在，无法删除";
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
                            message = "教师信息删除成功";
                        }else{
                            message = "教师信息删除失败";
                        }
                    }else{
                        message = "教师信息不存在，无法删除";
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
                            message = "家长信息删除成功";
                        }else{
                            message = "家长信息删除失败";
                        }
                    }else {
                        message = "家长信息不存在，无法删除";
                    }
                    break;
            }
        }else{
            message = "id不能为空";
        }
        map.put("flag", flag);
        map.put("message",message);
        return map;
    }

    public String ValidateStudent(StudentProfile studentProfile){
        String message = "";
        if(schools.trim().length() > 0){
            String[] schoolNames = schools.split(",");
            if(!Arrays.asList(schoolNames).contains(studentProfile.getKg_schoolname())){
                message += "学校名称有误;";
            }
        }
        // 对学生数据进行验证
        if(studentProfile.getKg_studentprofileid() == null || studentProfile.getKg_studentprofileid().trim().length() <= 0){
            message += "学生crmid有误;";
        }
        if(studentProfile.getKg_classname() == null|| studentProfile.getKg_classname().trim().length() <= 0){
            message += "学生所在班级有误;";
        }
        if(studentProfile.getKg_schoolname() == null || studentProfile.getKg_schoolname().trim().length() <= 0){
            message += "学生所在学校有误;";
        }
        if(studentProfile.getKg_name() == null || studentProfile.getKg_name().trim().length() <= 0) {
            message += "学生档案编号有误;";
        }
        if(studentProfile.getKg_fullname() == null || studentProfile.getKg_fullname().trim().length() <= 0){
            message += "学生姓名有误;";
        }
        if(studentProfile.getKg_sex() == null || studentProfile.getKg_sex().trim().length() <= 0){
            message += "学生性别有误;";
        }
        if(studentProfile.getKg_jointime() == null || studentProfile.getKg_jointime().trim().length() <= 0) {
            message += "学生入学时间有误;";
        }
        return message;
    }

    //--------------------blockchain api----------------------------------

    /*
    学生信息上链
     */
    public String InitStudent(StudentJson studentJson,String channelName) {
        try {
            return payload.GetPayload("initStudent", getInsertStudentJson(studentJson),channelName).toString();
        }catch (HttpClientErrorException e1) {
            e1.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), getInsertStudentJson(studentJson), e1, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e1, getInsertStudentJson(studentJson), "", "", stringWriter.toString()));
            return e1.getMessage();
        } catch(Exception e){
            e.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), getInsertStudentJson(studentJson), e, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, getInsertStudentJson(studentJson), "", "", stringWriter.toString()));
            return null;
        }
    }

    /*
    事件信息上链
     */
    public String InsertEventinfo(EventInfo eventInfo,String channelName) {
        try {
            return payload.GetPayload("insertEventInfo", getInsertEventJson(eventInfo), channelName).toString();
        } catch (HttpClientErrorException ex) {
            ex.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), getInsertEventJson(eventInfo), ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, getInsertEventJson(eventInfo), "", "", stringWriter.toString()));
            throw ex;
        } catch(Exception e){
            e.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), getInsertEventJson(eventInfo), e, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, getInsertEventJson(eventInfo), "", "", stringWriter.toString()));
            return null;
        }
    }

    /*
    通过crmid查询学生所有证书
     */
    @RequestMapping(value = "/QueryCertByCRMId", method = RequestMethod.GET)
    public List<CertInfo> QueryCertByCRMId(@RequestParam(value = "CrmId", required = true)String CrmId,@RequestParam(value = "channelName", required = true)String channelName) {
        try {
            JsonArray jsonArray = payload.GetPayload("queryCertByCRMId",'"'+CrmId+'"',channelName).getAsJsonArray();
            Iterator<JsonElement> it =jsonArray.iterator();
            gson = new Gson();
            List<CertInfo> certInfoList=new ArrayList<>();
            while(it.hasNext())
            {
                String str = it.next().getAsJsonObject().get("valueJson").getAsString();
                gson.fromJson(str, CertInfo.class);
                CertInfo obj= gson.fromJson(str, CertInfo.class);
                certInfoList.add(obj);
            }
            return certInfoList;
        } catch (HttpClientErrorException ex) {
            ex.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), CrmId, ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, CrmId, "", "", stringWriter.toString()));
            throw ex;
        } catch(Exception e){
            e.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), CrmId, e, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, CrmId, "", "", stringWriter.toString()));
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
            ex.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), CrmId, ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, CrmId, "", "", stringWriter.toString()));
            throw ex;
        }catch(Exception e){
            e.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), CrmId, e, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, CrmId, "", "", stringWriter.toString()));
            return null;
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
        } catch (HttpClientErrorException e1) {
            e1.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), certId, e1, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e1, certId, "", "", stringWriter.toString()));
            throw e1;
        } catch (Exception ex) {
            ex.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), certId, ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, certId, "", "", stringWriter.toString()));
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
        } catch (HttpClientErrorException e1) {
            e1.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), evtId, e1, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e1, evtId, "", "", stringWriter.toString()));
            throw e1;
        } catch (Exception ex) {
            ex.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), evtId, ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, evtId, "", "", stringWriter.toString()));
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

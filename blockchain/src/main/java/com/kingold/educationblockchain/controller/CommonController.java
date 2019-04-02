package com.kingold.educationblockchain.controller;

import static com.kingold.educationblockchain.util.ResultResponse.makeErrRsp;
import static com.kingold.educationblockchain.util.ResultResponse.makeOKRsp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.kingold.educationblockchain.bean.CertInfo;
import com.kingold.educationblockchain.bean.EventInfo;
import com.kingold.educationblockchain.bean.ParentInformation;
import com.kingold.educationblockchain.bean.StudentInfo;
import com.kingold.educationblockchain.bean.StudentJson;
import com.kingold.educationblockchain.bean.StudentParent;
import com.kingold.educationblockchain.bean.StudentProfile;
import com.kingold.educationblockchain.bean.StudentTeacher;
import com.kingold.educationblockchain.bean.TeacherInformation;
import com.kingold.educationblockchain.service.ClassInfoService;
import com.kingold.educationblockchain.service.ErrorLogService;
import com.kingold.educationblockchain.service.ParentInformationService;
import com.kingold.educationblockchain.service.SchoolInfoService;
import com.kingold.educationblockchain.service.Student2ClassService;
import com.kingold.educationblockchain.service.StudentParentService;
import com.kingold.educationblockchain.service.StudentProfileService;
import com.kingold.educationblockchain.service.StudentTeacherService;
import com.kingold.educationblockchain.service.Teacher2ClassService;
import com.kingold.educationblockchain.service.TeacherInformationService;
import com.kingold.educationblockchain.util.BlockChainPayload;
import com.kingold.educationblockchain.util.DateHandler;
import com.kingold.educationblockchain.util.LoggerException;
import com.kingold.educationblockchain.util.MD5Encryp;
import com.kingold.educationblockchain.util.NullStringToEmptyAdapterFactory;
import com.kingold.educationblockchain.util.RecordErrorLog;
import com.kingold.educationblockchain.util.RetResult;
import com.kingold.educationblockchain.util.SynData;

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
	@Autowired
	private ClassInfoService classInfoService;
	@Autowired
	private SchoolInfoService schoolInfoService;
	@Autowired
	private Student2ClassService student2ClassService;
	@Autowired
	private Teacher2ClassService teacher2ClassService;

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
	private MD5Encryp md5Encryp = new MD5Encryp();
	private Gson gson;
	private SynData synData = new SynData();

	@RequestMapping(value = "/Insert", method = RequestMethod.POST)
	public RetResult Insert(@RequestBody String jsonParam) {
		try {
			JSONObject jsonx = JSONObject.parseObject(jsonParam);
			String tablename = jsonx.getString("table_name");
			String synctype = jsonx.getString("sync_type");
			String fields = jsonx.getString("fields");
			if (tablename.trim().length() == 0 || synctype.trim().length() == 0) {
				return makeErrRsp("表名或者同步类型不能为空");
			}
			Map map = new HashMap();
			switch (synctype.trim()) {
			case "insert":
				return InsertData(tablename, fields);
			case "update":
				return InsertData(tablename, fields);
			}

			return makeErrRsp("数据插入或者更新失败");
		} catch (Exception ex) {
			return makeErrRsp(ex.getMessage());
		}
	}

	@RequestMapping(value = "/Delete", method = RequestMethod.GET)
	public RetResult Delete(@RequestParam(value = "id", required = true) String id, @RequestParam(value = "tablename", required = true) String tablename) {
		if (tablename.trim().length() == 0 || id.trim().length() == 0) {
			return makeErrRsp("表名或者同步类型不能为空");
		}
		return DeleteData(id, tablename);
	}

	public RetResult InsertData(String tableName, String jsonParam) {
		switch (tableName.trim()) {
		case "kg_studentprofile":
			return synData.insertOrUpdateStudent(jsonParam, channel, mStudentProfileService, mErrorLogService);
		case "kg_teacherinformation":
			return synData.insertOrUpdateTeacher(jsonParam, mTeacherInfomationService, mErrorLogService);
		case "kg_parentinformation":
			return synData.insertOrUpdateParent(jsonParam, mParentInfomationService, mErrorLogService);
		case "kg_student_parent":
			return synData.insertOrUpdateStudent2Parent(jsonParam, mStudentProfileService, mParentInfomationService, mStudentParentService, mErrorLogService);
		case "kg_student_teacher":
			return synData.insertOrUpdateStudent2Teacher(jsonParam, mStudentProfileService, mTeacherInfomationService, mStudentTeacherService, mErrorLogService);
		case "kg_class":
			return synData.insertOrUpdateClass(jsonParam, classInfoService, mErrorLogService);
		case "kg_school":
			return synData.insertOrUpdateSchool(jsonParam, schoolInfoService, mErrorLogService);
		case "kg_studentprofile_class":
			return synData.insertOrUpdateStudent2Class(jsonParam, mStudentProfileService, classInfoService, student2ClassService, mErrorLogService);
		case "kg_class_teacherinformation":
			return synData.insertOrUpdateTeacher2Class(jsonParam, mTeacherInfomationService, classInfoService, teacher2ClassService, mErrorLogService);
		}

		return makeErrRsp("表名不正确");
	}

	public RetResult<String> DeleteData(String id, String tableName) {
		if (Strings.isNullOrEmpty(id)) {
			return makeErrRsp("传入Id为空");
		}
		switch (tableName.trim()) {
		case "kg_studentprofile":
			if (mStudentProfileService.DeleteStudentProfile(id) && 
					mStudentParentService.deleteByStudentId(id) && 
					mStudentTeacherService.deleteByStudentId(id) && 
					student2ClassService.deleteByStudentId(id)) {
				return makeOKRsp("学生信息删除成功");
			}
			return makeErrRsp("学生信息删除失败");
		case "kg_teacherinformation":
			if(mTeacherInfomationService.DeleteTeacherInformation(id)&&
					mStudentTeacherService.deleteByTeacherId(id)&&
					teacher2ClassService.deleteByTeacherId(id)) {
				return makeOKRsp("老师信息删除成功");
			}
			
			return makeErrRsp("老师信息删除失败");
		case "kg_parentinformation":
			if(mParentInfomationService.DeleteParentInformation(id)&&
					mStudentParentService.deleteByParentId(id)) {
				return makeOKRsp("家长信息删除成功");
			}
			
			return makeErrRsp("家长信息删除失败");
		}
		
		return makeErrRsp("表名不正确");
	}

	/*
	 * 事件信息上链
	 */
	public String InsertEventinfo(EventInfo eventInfo, String channelName) {
		try {
			return payload.GetPayload("insertEventInfo", getInsertEventJson(eventInfo), channelName).toString();
		} catch (HttpClientErrorException ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), getInsertEventJson(eventInfo), ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, getInsertEventJson(eventInfo), "", "", stringWriter.toString()));
			throw ex;
		} catch (Exception e) {
			e.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), getInsertEventJson(eventInfo), e, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, getInsertEventJson(eventInfo), "", "", stringWriter.toString()));
			return null;
		}
	}

	/*
	 * 通过crmid查询学生所有证书
	 */
	@RequestMapping(value = "/QueryCertByCRMId", method = RequestMethod.GET)
	public List<CertInfo> QueryCertByCRMId(@RequestParam(value = "CrmId", required = true) String CrmId, @RequestParam(value = "channelName", required = true) String channelName) {
		try {
			gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
			List<CertInfo> certInfoList = new ArrayList<>();
			JsonElement jsonElement = payload.GetPayload("queryCertByCRMId", '"' + CrmId + '"', channelName);
			if (jsonElement==null || jsonElement.isJsonObject() && jsonElement.getAsJsonObject().get("returnCode").getAsString().equalsIgnoreCase("Failure")) {
				return certInfoList;
			}
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			Iterator<JsonElement> it = jsonArray.iterator();
			while (it.hasNext()) {
				String str = it.next().getAsJsonObject().get("valueJson").getAsString();;
				CertInfo obj = gson.fromJson(str, CertInfo.class);
				certInfoList.add(obj);
			}
			return certInfoList;
		} catch (HttpClientErrorException ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), CrmId, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, CrmId, "", "", stringWriter.toString()));
			throw ex;
		} catch (Exception e) {
			e.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), CrmId, e, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, CrmId, "", "", stringWriter.toString()));
			return null;
		}
	}

	/*
	 * 通过crmid查询学生所有事件
	 */
	public List<EventInfo> QueryEventByCRMId(String CrmId, String channelName) {
		try {
			JsonArray jsonArray = payload.GetPayload("queryEventByCRMId", '"' + CrmId + '"', channelName).getAsJsonArray();
			Iterator<JsonElement> it = jsonArray.iterator();
			gson = new Gson();
			List<EventInfo> eventInfoList = new ArrayList<EventInfo>();
			while (it.hasNext()) {
				String str = it.next().getAsJsonObject().get("valueJson").getAsString();
				gson.fromJson(str, CertInfo.class);
				EventInfo obj = gson.fromJson(str, EventInfo.class);
				eventInfoList.add(obj);
			}
			return eventInfoList;
		} catch (HttpClientErrorException ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), CrmId, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, CrmId, "", "", stringWriter.toString()));
			throw ex;
		} catch (Exception e) {
			e.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), CrmId, e, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, CrmId, "", "", stringWriter.toString()));
			return null;
		}
	}

	/*
	 * 通过证书id查询证书详细信息
	 */
	public CertInfo QueryCertById(String certId, String channelName) {
		try {

			String jsonString = payload.GetPayload("readCert", '"' + certId + '"', channelName).toString();
			gson = new Gson();
			CertInfo certInfo = gson.fromJson(jsonString, CertInfo.class);
			return certInfo;
		} catch (HttpClientErrorException e1) {
			e1.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), certId, e1, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e1, certId, "", "", stringWriter.toString()));
			throw e1;
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), certId, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, certId, "", "", stringWriter.toString()));
			throw ex;
		}
	}

	/*
	 * 通过事件id查询证书详细信息
	 */
	public EventInfo QueryEventById(String evtId, String channelName) {
		try {

			String jsonString = payload.GetPayload("readEvent", '"' + evtId + '"', channelName).toString();
			gson = new Gson();
			EventInfo evtInfo = gson.fromJson(jsonString, EventInfo.class);
			return evtInfo;
		} catch (HttpClientErrorException e1) {
			e1.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), evtId, e1, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e1, evtId, "", "", stringWriter.toString()));
			throw e1;
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), evtId, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, evtId, "", "", stringWriter.toString()));
			throw ex;
		}
	}

	private String getInsertEventJson(EventInfo event) {
		return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", event.getEventId(), event.getStudentId(), event.getEventContent(), event.getEventDate(), event.getEventOrg(),
				event.getEventOperationTime(), event.getRemark());
	}

	private String getInsertStudentJson(StudentJson student) {
		return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", student.getStudentId(), student.getCrmId(), student.getStudentEducationNo(), student.getStudentIdCardNo(), student.getStudentNameString(),
				student.getStudentOperationTime(), student.getRemark());
	}
}

package com.kingold.educationblockchain.util;

import static com.kingold.educationblockchain.util.ResultResponse.makeErrRsp;
import static com.kingold.educationblockchain.util.ResultResponse.makeOKRsp;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.kingold.educationblockchain.bean.ClassInfo;
import com.kingold.educationblockchain.bean.ParentInformation;
import com.kingold.educationblockchain.bean.SchoolInfo;
import com.kingold.educationblockchain.bean.Student2Class;
import com.kingold.educationblockchain.bean.StudentJson;
import com.kingold.educationblockchain.bean.StudentParent;
import com.kingold.educationblockchain.bean.StudentProfile;
import com.kingold.educationblockchain.bean.StudentTeacher;
import com.kingold.educationblockchain.bean.Teacher2Class;
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

public class SynData {
	private DateHandler dateHandler;
	private BlockChainPayload payload = new BlockChainPayload();
	private StringWriter stringWriter = new StringWriter();
	private PrintWriter printWriter = new PrintWriter(stringWriter);
	private LoggerException loggerException = new LoggerException();
	private RecordErrorLog recordErrorLog = new RecordErrorLog();
	private MD5Encryp md5Encryp = new MD5Encryp();

	public RetResult<String> insertOrUpdateStudent(String jsonParam, String channel, StudentProfileService mStudentProfileService, ErrorLogService mErrorLogService) {
		StudentProfile studentProfile = JSONObject.parseObject(jsonParam, StudentProfile.class);
		String validateResult = ValidateStudent(studentProfile);
		if (!Strings.isNullOrEmpty(validateResult)) {
			return makeErrRsp(validateResult);
		}
		// 判断学生 是否已存在
		try {
			// 将学生信息上链
			StudentJson studentJson = new StudentJson();
			studentJson.setStudentId(studentProfile.getKg_studentprofileid());
			studentJson.setCrmId(studentProfile.getKg_studentprofileid());
			studentJson.setStudentEducationNo(md5Encryp.GetMD5(studentProfile.getKg_educationnumber()));
			studentJson.setStudentIdCardNo(md5Encryp.GetMD5(studentProfile.getKg_passportnumberoridnumber()));
			studentJson.setStudentNameString(studentProfile.getKg_fullname());
			dateHandler = new DateHandler();
			studentJson.setStudentOperationTime(dateHandler.GetCurrentTime());
			studentJson.setRemark("");
			JsonElement chainResult = payload.GetPayload("initStudent", getInsertStudentJson(studentJson), channel);
			if (chainResult == null || chainResult.getAsString().equalsIgnoreCase("Failure")) {
				return makeErrRsp("学生信息上链失败");
			}

			StudentProfile student = mStudentProfileService.GetStudentProfileById(studentProfile.getKg_studentprofileid());
			return (student == null ? mStudentProfileService.AddStudentProfile(studentProfile) : mStudentProfileService.UpdateStudentProfile(studentProfile)) ? makeOKRsp("学生信息添加成功") : makeErrRsp("学生信息添加失败");
		} catch (Exception ex) {
			mStudentProfileService.DeleteStudentProfile(studentProfile.getKg_studentprofileid());
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}

	public RetResult<String> insertOrUpdateTeacher(String jsonParam, TeacherInformationService mTeacherInfomationService, ErrorLogService mErrorLogService) {
		try {
			TeacherInformation teacherInformation = JSONObject.parseObject(jsonParam, TeacherInformation.class);
			TeacherInformation tInfo = mTeacherInfomationService.FindTeacherInformationById(teacherInformation.getKg_teacherinformationid()); //FindTeacherInformationByPhone(teacherInformation.getKg_phonenumber());

			return (tInfo == null ? mTeacherInfomationService.AddTeacherInformation(teacherInformation) : mTeacherInfomationService.UpdateTeacherInformation(teacherInformation)) ? makeOKRsp("教师信息添加成功")
					: makeErrRsp("教师信息添加失败");
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}

	public RetResult<String> insertOrUpdateParent(String jsonParam, ParentInformationService mParentInfomationService, ErrorLogService mErrorLogService) {
		try {
			ParentInformation parentInformation = JSONObject.parseObject(jsonParam, ParentInformation.class);
			ParentInformation pInfo = mParentInfomationService.FindParentInformationById(parentInformation.getKg_parentinformationid());//FindParentInformationByPhone(parentInformation.getKg_phonenumber());

			return (pInfo == null ? mParentInfomationService.AddParentInformation(parentInformation) : mParentInfomationService.UpdateParentInformation(parentInformation)) ? makeOKRsp("家长信息添加成功")
					: makeErrRsp("家长信息添加失败");
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}

	public RetResult<String> insertOrUpdateStudent2Parent(String jsonParam, StudentProfileService mStudentProfileService, ParentInformationService mParentInfomationService, StudentParentService mStudentParentService,
			ErrorLogService mErrorLogService) {
		try {
			StudentParent studentParent = JSONObject.parseObject(jsonParam, StudentParent.class);
			// 判断 是否存在 家长信息，学生信息
			StudentProfile profile = mStudentProfileService.GetStudentProfileById(studentParent.getKg_studentprofileid());
			ParentInformation pInformation = mParentInfomationService.FindParentInformationById(studentParent.getKg_parentinformationid());
			if (profile == null || pInformation == null) {
				return makeErrRsp("暂无该家长或该学生信息，无法添加");
			}

			StudentParent sp = mStudentParentService.FindStudentParent(studentParent.getKg_parentinformationid(), studentParent.getKg_studentprofileid());
			return (sp == null ? mStudentParentService.AddStudentParent(studentParent) : mStudentParentService.UpdateStudentParent(studentParent)) ? makeOKRsp("家长学生信息添加成功") : makeErrRsp("家长学生信息添加失败");
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}

	public RetResult<String> insertOrUpdateStudent2Teacher(String jsonParam, StudentProfileService mStudentProfileService, TeacherInformationService mTeacherInfomationService,
			StudentTeacherService mStudentTeacherService, ErrorLogService mErrorLogService) {
		try {
			StudentTeacher studentTeacher = JSONObject.parseObject(jsonParam, StudentTeacher.class);
			// 判断 是否存在 教师信息，学生信息
			StudentProfile profile2 = mStudentProfileService.GetStudentProfileById(studentTeacher.getKg_studentprofileid());
			TeacherInformation tInformation = mTeacherInfomationService.FindTeacherInformationById(studentTeacher.getKg_teacherinformationid());
			if (profile2 == null || tInformation == null) {
				return makeErrRsp("暂无该教师或该学生信息，无法添加");
			}

			StudentTeacher st = mStudentTeacherService.FindStudentTeacher(studentTeacher.getKg_teacherinformationid(), studentTeacher.getKg_studentprofileid());

			return (st == null ? mStudentTeacherService.AddStudentTeacher(studentTeacher) : mStudentTeacherService.UpdateStudentTeacher(studentTeacher)) ? makeOKRsp("教师学生信息添加成功") : makeErrRsp("教师学生信息添加失败");
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}

	public RetResult<String> insertOrUpdateClass(String jsonParam, ClassInfoService classInfoService, ErrorLogService mErrorLogService) {
		try {
			ClassInfo classInfo = JSONObject.parseObject(jsonParam, ClassInfo.class);
			// 判断班级信息是否存在
			ClassInfo cInfo = classInfoService.getById(classInfo.getKg_classid());
			return (cInfo == null ? classInfoService.add(classInfo) : classInfoService.update(classInfo)) ? makeOKRsp("班級信息新增或者更新成功") : makeErrRsp("班級信息新增或者更新失敗");

		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}
	
	public RetResult<String> insertOrUpdateSchool(String jsonParam, SchoolInfoService schoolInfoService, ErrorLogService mErrorLogService){
		try {
			SchoolInfo schoolInfo = JSONObject.parseObject(jsonParam, SchoolInfo.class);
			// 判断学校信息是否存在
			SchoolInfo sInfo = schoolInfoService.getById(schoolInfo.getKg_schoolid());
			return (sInfo == null ? schoolInfoService.add(schoolInfo) : schoolInfoService.update(schoolInfo)) ? makeOKRsp("学校信息新增或者更新成功") : makeErrRsp("学校信息新增或者更新失敗");

		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}
	
	public RetResult<String> insertOrUpdateStudent2Class(String jsonParam, StudentProfileService mStudentProfileService, ClassInfoService classInfoService, Student2ClassService student2ClassService, ErrorLogService mErrorLogService){
		try {
			Student2Class student2Class = JSONObject.parseObject(jsonParam, Student2Class.class);
			// 判断 是否存在 学生信息 班级信息
			StudentProfile profile = mStudentProfileService.GetStudentProfileById(student2Class.getKg_studentprofileid());
			ClassInfo cinfo = classInfoService.getById(student2Class.getKg_classid());
			if (profile == null || cinfo == null) {
				return makeErrRsp("暂无该班级或该学生信息，无法添加");
			}

			Student2Class sc = student2ClassService.getById(student2Class.getKg_studentprofileid(), student2Class.getKg_classid());

			return (sc == null ? student2ClassService.add(student2Class):student2ClassService.update(student2Class)) ? makeOKRsp("学生班级信息添加成功") : makeErrRsp("班级学生信息添加失败");
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}
	
	public RetResult<String> insertOrUpdateTeacher2Class(String jsonParam, TeacherInformationService mTeacherInfomationService, ClassInfoService classInfoService, Teacher2ClassService teacher2ClassService, ErrorLogService mErrorLogService){
		try {
			Teacher2Class teacher2Class = JSONObject.parseObject(jsonParam, Teacher2Class.class);
			// 判断 是否存在 班级信息 教师信息
			TeacherInformation tInfo = mTeacherInfomationService.FindTeacherInformationById(teacher2Class.getKg_teacherinformationid());
			ClassInfo cinfo = classInfoService.getById(teacher2Class.getKg_classid());
			if (tInfo == null || cinfo == null) {
				return makeErrRsp("暂无该班级或该老师信息，无法添加");
			}

			Teacher2Class tc = teacher2ClassService.getById(teacher2Class.getKg_teacherinformationid(), teacher2Class.getKg_classid());

			return (tc == null ? teacher2ClassService.add(teacher2Class):teacher2ClassService.update(teacher2Class)) ? makeOKRsp("老师班级信息添加成功") : makeErrRsp("老师学生信息添加失败");
		} catch (Exception ex) {
			ex.printStackTrace(printWriter);
			loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getName(), jsonParam, ex, stringWriter.toString());
			mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
			return makeErrRsp(ex.getMessage());
		}
	}

	private String getInsertStudentJson(StudentJson student) {
		return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", student.getStudentId(), student.getCrmId(), student.getStudentEducationNo(), student.getStudentIdCardNo(), student.getStudentNameString(),
				student.getStudentOperationTime(), student.getRemark());
	}

	private String ValidateStudent(StudentProfile studentProfile) {
		String message = "";
		/*
		 * if(schools.trim().length() > 0){ String[] schoolNames = schools.split(",");
		 * if(!Arrays.asList(schoolNames).contains(studentProfile.getKg_schoolname())){
		 * message += "学校名称有误;"; } }
		 */
		// 对学生数据进行验证
		if (Strings.isNullOrEmpty(studentProfile.getKg_studentprofileid())) {
			message += "学生crmid有误;";
		}
		if (Strings.isNullOrEmpty(studentProfile.getKg_classid())) {
			message += "学生所在班级有误;";
		}
		if (Strings.isNullOrEmpty(studentProfile.getKg_schoolid())) {
			message += "学生所在学校有误;";
		}
		if (Strings.isNullOrEmpty(studentProfile.getKg_name())) {
			message += "学生档案编号有误;";
		}
		if (Strings.isNullOrEmpty(studentProfile.getKg_fullname())) {
			message += "学生姓名有误;";
		}
		if (Strings.isNullOrEmpty(studentProfile.getKg_sex())) {
			message += "学生性别有误;";
		}
		if (Strings.isNullOrEmpty(studentProfile.getKg_jointime())) {
			message += "学生入学时间有误;";
		}
		return message;
	}
}

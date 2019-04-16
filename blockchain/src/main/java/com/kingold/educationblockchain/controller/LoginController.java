package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.kingold.educationblockchain.bean.*;
import com.kingold.educationblockchain.service.*;

import com.kingold.educationblockchain.util.CheckRequestDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
	private Teacher2ClassService teacher2ClassService;
	@Autowired
	private ClassInfoService classInfoService;

	private Gson gson;

	@Value("${chainCode.channel}")
	private String channel;

	private boolean isFromMobile;

	@RequestMapping("/login")
	public ModelAndView UserLogin(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		isFromMobile = CheckLogin(request);
		if (isFromMobile) {
			model.addObject("device", "mobile");
			model.setViewName("mobileLogin");
		} else {
			model.addObject("device", "pc");
			model.setViewName("login");
		}
		return model;
	}

	@RequestMapping(value = "/loginVerify", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView UserLoginVerify(String phonenumber, int option, ModelMap map, HttpServletRequest request) {
		// role 為1，代表家長，為2，代表教師
		ModelAndView model = new ModelAndView();
		isFromMobile = CheckLogin(request);
		if (option == 1) {
			ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phonenumber);
			if (parentInformation != null) {
				List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(parentInformation.getKg_parentinformationid());
				if (studentParents != null && studentParents.size() > 0) {
					if (studentParents.size() > 1) {
						List<StudentProfile> StudentProfileList = new ArrayList<>();
						for (StudentParent sp : studentParents) {
							StudentProfile studentProfile = mStudentProfileService.GetStudentProfileById(sp.getKg_studentprofileid());
							if(studentProfile.getKg_jointime() == null || studentProfile.getKg_jointime() == ""){
								studentProfile.setKg_jointime("未知");
							}
							if(studentProfile.getKg_educationnumber() == null || studentProfile.getKg_educationnumber() == ""){
								studentProfile.setKg_educationnumber("未知");
							}
							StudentProfileList.add(studentProfile);
						}
						model.addObject("childrenList", StudentProfileList);
						model.addObject("parentInformation", parentInformation);

						if (isFromMobile) {
							model.setViewName("mobileChildrenlist");
						} else {
							model.setViewName("childrenlist");
						}
						return model;
					} else {
						StudentProfile studentprofile = mStudentProfileService.GetStudentProfileById(studentParents.get(0).getKg_studentprofileid());
						model.addObject("studentprofile", studentprofile);
						CommonController commonController = new CommonController();
						List<DisplayInfo> displayInfos = new ArrayList<>();
						List<CertInfo> certJson = commonController.QueryCertByCRMId(studentprofile.getKg_studentprofileid(), channel);
						for (CertInfo cert : certJson) {
							if (cert.getCertType().equals("毕业证书") || cert.getCertType().equals("录取通知书") || cert.getCertType().equals("课程证书")) {
								DisplayInfo x = new DisplayInfo();
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
						map.addAttribute("certCount", displayInfos.size());
						map.addAttribute("json", displayInfos);

						model.addObject("stuid", studentprofile.getKg_studentprofileid());
						model.addObject("roleid", "");
						model.addObject("role", 0);
						model.addObject("classid", "");
						model.addObject("schoolid", "");

						if (isFromMobile) {
							model.setViewName("mobileStudentInfo");
						} else {
							model.setViewName("studentinfoandcerts");
						}
						return model;
					}
				}
			}
		} else {
			TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phonenumber);

			List<SchoolInfo> schoolInfoList = teacher2ClassService.getSchoolsByTeacherId(teacherInformation.getKg_teacherinformationid());
			if (schoolInfoList.size() > 0) {
				List<ClassInfo> classInfoList = classInfoService.getClassesBySchoolId(teacherInformation.getKg_teacherinformationid(), schoolInfoList.get(0).getKg_schoolid());
				if (classInfoList.size() > 0) {
					model.addObject("classList", classInfoList);
					model.addObject("teacherInformation", teacherInformation);
					model.addObject("schoolInfoList", schoolInfoList);
					model.addObject("classid", "");
					model.addObject("schoolid", "");
					// 判断设备，如果是移动端，则需要直接获取所有的学生信息
					if (isFromMobile) {
						model.setViewName("mobileStudentlist");
					} else {
						model.setViewName("studentlist");
					}
					
					return model;
				}
			}

		}
		model.addObject("loginVerify", false);
		if (isFromMobile) {
			model.addObject("device", "mobile");
			model.setViewName("mobileLogin");
		} else {
			model.addObject("device", "pc");
			model.setViewName("login");
		}
		return model;
	}

	@RequestMapping(value = "/IsExistPhone", method = RequestMethod.POST)
	@ResponseBody
	public String IsExistPhone(@RequestBody JSONObject params) {
		gson = new Gson();
		String phone = params.getString("phone");
		if (phone != null && phone.trim().length() > 0) {
			// 先从家长信息表中查询此号码
			ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phone);
			if (parentInformation != null) {
				return gson.toJson(true);
			}
			// 从教师信息表中查询此号码
			TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phone);
			if (teacherInformation != null) {
				return gson.toJson(true);
			}
			return gson.toJson(false);
		} else {
			return gson.toJson(false);
		}
	}

	@RequestMapping(value = "/BackListPage", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView BackListPage(String roleid, int role, String classid, String schoolid, HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		isFromMobile = CheckLogin(request);
		if (role == 1) {
			ParentInformation parentInformation = mParentInfomationService.FindParentInformationById(roleid);
			List<StudentParent> studentParents = mStudentParentService.FindStudentParentByParentId(roleid);
			List<StudentProfile> StudentProfileList = new ArrayList<>();
			for (StudentParent sp : studentParents) {
				StudentProfileList.add(mStudentProfileService.GetStudentProfileById(sp.getKg_studentprofileid()));
			}
			model.addObject("childrenList", StudentProfileList);
			model.addObject("parentInformation", parentInformation);
			model.setViewName("childrenlist");
			return model;
		} else {
			TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationById(roleid);

			List<SchoolInfo> schoolInfoList = teacher2ClassService.getSchoolsByTeacherId(teacherInformation.getKg_teacherinformationid());
			if (schoolInfoList.size() > 0) {
				List<ClassInfo> classInfoList = classInfoService.getClassesBySchoolId(teacherInformation.getKg_teacherinformationid(),schoolInfoList.get(0).getKg_schoolid());
				if (classInfoList.size() > 0) {
					model.addObject("classList", classInfoList);
					model.addObject("teacherInformation", teacherInformation);
					model.addObject("schoolInfoList", schoolInfoList);
					model.addObject("classid", classid);
					model.addObject("schoolid", schoolid);
					// 判断设备，如果是移动端，则需要直接获取所有的学生信息
					if (isFromMobile) {
						model.setViewName("mobileStudentlist");
					} else {
						model.setViewName("studentlist");
					}
					
					return model;
				}
			}
			return model;
		}
	}

	@RequestMapping(value = "/IsExistPhoneByRole", method = RequestMethod.POST)
	@ResponseBody
	public String IsExistPhoneByRole(@RequestBody JSONObject params) {
		gson = new Gson();
		String phone = params.getString("phone");
		int role = params.getInteger("role");
		if (phone != null && phone.trim().length() > 0) {
			if (role == 1) {
				ParentInformation parentInformation = mParentInfomationService.FindParentInformationByPhone(phone);
				if (parentInformation != null) {
					return gson.toJson(true);
				}
				return gson.toJson(false);
			} else {
				TeacherInformation teacherInformation = mTeacherInfomationService.FindTeacherInformationByPhone(phone);
				if (teacherInformation != null) {
					return gson.toJson(true);
				}
				return gson.toJson(false);
			}
		} else {
			return gson.toJson(false);
		}
	}

	public List<StudentInfo> GetStudentList(List<String> studentIds) {
		// 获取教师id下的所有证书信息
		List<StudentInfo> StudentInfoList = new ArrayList<>();
		if (studentIds.size() > 0) {
			for (String studentId : studentIds) {
				StudentProfile profile = mStudentProfileService.GetStudentProfileById(studentId);
				StudentInfo info = new StudentInfo();
				if (profile != null) {
					info.setKg_studentprofileid(profile.getKg_studentprofileid());
					info.setKg_classname(profile.getKg_classname());
					info.setKg_fullname(profile.getKg_fullname());
					info.setKg_educationnumber(profile.getKg_educationnumber());
					info.setKg_sex(profile.getKg_sex());
					info.setKg_jointime(profile.getKg_jointime());
				}
				List<StudentParent> parents = mStudentParentService.FindStudentParentByStudentId(studentId);
				if (parents != null && parents.size() > 0) {
					ParentInformation parentInformation = mParentInfomationService.FindParentInformationById(parents.get(0).getKg_parentinformationid());
					if (parentInformation != null) {
						info.setKg_parentname(parentInformation.getKg_name());
						info.setKg_parentphonenumber(parentInformation.getKg_phonenumber());
					} else {
						info.setKg_parentname("");
						info.setKg_parentphonenumber("");
					}
				} else {
					info.setKg_parentname("");
					info.setKg_parentphonenumber("");
				}
				StudentInfoList.add(info);
			}
		}

		return StudentInfoList;
	}

	// 访问设备验证
	public boolean CheckLogin(HttpServletRequest request) {
		boolean isFromMobile = false;
		try{
			// 获取ua，用来判断是否为移动端访问
			String userAgent = request.getHeader("USER-AGENT").toLowerCase();
			if (userAgent == null) {
				userAgent = "";
			}
			isFromMobile = CheckRequestDevice.check(userAgent);
		}catch(Exception ex){

		}
		return isFromMobile;
	}
}

package com.kingold.educationblockchain.bean;

/*
教师查看学生列表信息展示bean
 */
public class StudentInfo {

	public String getKg_studentprofileid() {
		return kg_studentprofileid;
	}

	public void setKg_studentprofileid(String kg_studentprofileid) {
		this.kg_studentprofileid = kg_studentprofileid;
	}

	public String getKg_classname() {
		return kg_classname;
	}

	public void setKg_classname(String kg_classname) {
		this.kg_classname = kg_classname;
	}

	public String getKg_educationnumber() {
		return kg_educationnumber;
	}

	public void setKg_educationnumber(String kg_educationnumber) {
		this.kg_educationnumber = kg_educationnumber;
	}

	public String getKg_fullname() {
		return kg_fullname;
	}

	public void setKg_fullname(String kg_fullname) {
		this.kg_fullname = kg_fullname;
	}

	public String getKg_sex() {
		return kg_sex;
	}

	public void setKg_sex(String kg_sex) {
		this.kg_sex = kg_sex;
	}

	private String kg_studentprofileid;

	private String kg_classname;

	private String kg_educationnumber;

	private String kg_fullname;

	private String kg_sex;

	private String kg_parentname;

	private String kg_parentphonenumber;

	public String getKg_jointime() {
		return kg_jointime;
	}

	public void setKg_jointime(String kg_jointime) {
		this.kg_jointime = kg_jointime;
	}

	private String kg_jointime;

	public StudentInfo() {
	}

	public String getKg_parentname() {
		return kg_parentname;
	}

	public void setKg_parentname(String kg_parentname) {
		this.kg_parentname = kg_parentname;
	}

	public String getKg_parentphonenumber() {
		return kg_parentphonenumber;
	}

	public void setKg_parentphonenumber(String kg_parentphonenumber) {
		this.kg_parentphonenumber = kg_parentphonenumber;
	}

}

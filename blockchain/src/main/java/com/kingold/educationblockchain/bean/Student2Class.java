package com.kingold.educationblockchain.bean;

/**
 * 
 * 学生-班级关联表
 * 
 * @author Bob Tang
 *
 */
public class Student2Class {
	private String kg_studentprofileid;
	private String kg_classid;
	private int kg_state;

	public String getKg_studentprofileid() {
		return kg_studentprofileid;
	}

	public void setKg_studentprofileid(String kg_studentprofileid) {
		this.kg_studentprofileid = kg_studentprofileid;
	}

	public String getKg_classid() {
		return kg_classid;
	}

	public void setKg_classid(String kg_classid) {
		this.kg_classid = kg_classid;
	}

	public int getKg_state() {
		return kg_state;
	}

	public void setKg_state(int kg_state) {
		this.kg_state = kg_state;
	}

}

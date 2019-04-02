package com.kingold.educationblockchain.bean;

/**
 * 学生信息表
 * 
 * @author Bob Tang
 *
 */
public class SchoolInfo {
	private String kg_schoolid;
	private String kg_name;
	private int kg_state;

	public String getKg_schoolid() {
		return kg_schoolid;
	}

	public void setKg_schoolid(String kg_schoolid) {
		this.kg_schoolid = kg_schoolid;
	}

	public String getKg_name() {
		return kg_name;
	}

	public void setKg_name(String kg_name) {
		this.kg_name = kg_name;
	}

	public int getKg_state() {
		return kg_state;
	}

	public void setKg_state(int kg_state) {
		this.kg_state = kg_state;
	}

}

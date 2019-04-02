package com.kingold.educationblockchain.bean;

/*
   * 班级信息表
 * Bob Tang
 */
public class ClassInfo {
	private String kg_classid;
	private String kg_name;
	private String kg_schoolid;
	private int kg_state;

	public String getKg_classid() {
		return kg_classid;
	}

	public void setKg_classid(String kg_classid) {
		this.kg_classid = kg_classid;
	}

	public String getKg_name() {
		return kg_name;
	}

	public void setKg_name(String kg_name) {
		this.kg_name = kg_name;
	}

	public String getKg_schoolid() {
		return kg_schoolid;
	}

	public void setKg_schoolid(String kg_schoolid) {
		this.kg_schoolid = kg_schoolid;
	}

	public int getKg_state() {
		return kg_state;
	}

	public void setKg_state(int kg_state) {
		this.kg_state = kg_state;
	}

}

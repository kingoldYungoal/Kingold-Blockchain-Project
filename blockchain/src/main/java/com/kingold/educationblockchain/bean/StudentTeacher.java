package com.kingold.educationblockchain.bean;

public class StudentTeacher {

    public String getKg_studentprofileid() {
        return kg_studentprofileid;
    }

    public void setKg_studentprofileid(String kg_studentprofileid) {
        this.kg_studentprofileid = kg_studentprofileid;
    }

    public String getKg_teacherinformationid() {
        return kg_teacherinformationid;
    }

    public void setKg_teacherinformationid(String kg_teacherinformationid) {
        this.kg_teacherinformationid = kg_teacherinformationid;
    }

    public int getKg_state() {
        return kg_state;
    }

    public void setKg_state(int kg_state) {
        this.kg_state = kg_state;
    }

    private String kg_studentprofileid;

    private String kg_teacherinformationid;

    private int kg_state;

    public StudentTeacher(){
    }
}

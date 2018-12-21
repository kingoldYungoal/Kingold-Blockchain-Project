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

    public String getKg_parentName() {
        return kg_parentName;
    }

    public void setKg_parentName(String kg_parentName) {
        this.kg_parentName = kg_parentName;
    }

    public String getKg_parentPhoneNumber() {
        return kg_parentPhoneNumber;
    }

    public void setKg_parentPhoneNumber(String kg_parentPhoneNumber) {
        this.kg_parentPhoneNumber = kg_parentPhoneNumber;
    }

    private String kg_studentprofileid;

    private String kg_classname;

    private String kg_educationnumber;

    private String kg_fullname;

    private String kg_sex;

    private String kg_parentName;

    private String kg_parentPhoneNumber;

    public String getKg_jointime() {
        return kg_jointime;
    }

    public void setKg_jointime(String kg_jointime) {
        this.kg_jointime = kg_jointime;
    }

    private String kg_jointime;

    public StudentInfo(){
    }
}

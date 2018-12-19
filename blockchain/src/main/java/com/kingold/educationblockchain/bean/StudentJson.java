package com.kingold.educationblockchain.bean;

import com.google.gson.annotations.SerializedName;

public class StudentJson {
    @SerializedName("stu_id")
    private String StudentId;
    @SerializedName("crm_id")
    private String CrmId;
    @SerializedName("stu_education_no")
    private String StudentEducationNo;
    @SerializedName("stu_idCard_no")
    private String StudentIdCardNo;
    @SerializedName("stu_name_string")
    private String StudentNameString;
    @SerializedName("stu_operation_time")
    private String StudentOperationTime;
    @SerializedName("remark")
    private String Remark;


    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentNameString() {
        return StudentNameString;
    }

    public void setStudentNameString(String studentNameString) {
        StudentNameString = studentNameString;
    }

    public String getCrmId() {
        return CrmId;
    }

    public void setCrmId(String crmId) {
        CrmId = crmId;
    }

    public String getStudentEducationNo() {
        return StudentEducationNo;
    }

    public void setStudentEducationNo(String studentEducationNo) {
        StudentEducationNo = studentEducationNo;
    }

    public String getStudentIdCardNo() {
        return StudentIdCardNo;
    }

    public void setStudentIdCardNo(String studentIdCardNo) {
        StudentIdCardNo = studentIdCardNo;
    }

    public String getStudentOperationTime() {
        return StudentOperationTime;
    }

    public void setStudentOperationTime(String studentOperationTime) {
        StudentOperationTime = studentOperationTime;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}

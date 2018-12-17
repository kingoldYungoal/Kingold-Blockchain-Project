package com.kingold.educationblockchain.bean;

import com.google.gson.annotations.SerializedName;

public class EventInfo {
    @SerializedName("cert_id")
    public String  CertId;
    @SerializedName("stu_id")
    public String StudentId;
    @SerializedName("cert_no")
    public String CertNo;
    @SerializedName("cert_type")
    public String CertType;
    @SerializedName("cert_holder")
    public String CertHolder;
    @SerializedName("cert_name")
    public String CertName;
    @SerializedName("cert_content")
    public String  CertContent;
    @SerializedName("cert_pdf_path")
    public String CertPdfPath;
    @SerializedName("cert_hash")
    public String  CertHash;
    @SerializedName("cert_issuer")
    public String  CertIssuer;
    @SerializedName("cert_issue_date")
    public String CertIssueDate;
    @SerializedName("cert_operation_time")
    public String CertOperationTime;

    public String getCertId() {
        return CertId;
    }

    public void setCertId(String certId) {
        CertId = certId;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getCertNo() {
        return CertNo;
    }

    public void setCertNo(String certNo) {
        CertNo = certNo;
    }

    public String getCertType() {
        return CertType;
    }

    public void setCertType(String certType) {
        CertType = certType;
    }

    public String getCertHolder() {
        return CertHolder;
    }

    public void setCertHolder(String certHolder) {
        CertHolder = certHolder;
    }

    public String getCertName() {
        return CertName;
    }

    public void setCertName(String certName) {
        CertName = certName;
    }

    public String getCertContent() {
        return CertContent;
    }

    public void setCertContent(String certContent) {
        CertContent = certContent;
    }

    public String getCertPdfPath() {
        return CertPdfPath;
    }

    public void setCertPdfPath(String certPdfPath) {
        CertPdfPath = certPdfPath;
    }

    public String getCertHash() {
        return CertHash;
    }

    public void setCertHash(String certHash) {
        CertHash = certHash;
    }

    public String getCertIssuer() {
        return CertIssuer;
    }

    public void setCertIssuer(String certIssuer) {
        CertIssuer = certIssuer;
    }

    public String getCertIssueDate() {
        return CertIssueDate;
    }

    public void setCertIssueDate(String certIssueDate) {
        CertIssueDate = certIssueDate;
    }

    public String getCertOperationTime() {
        return CertOperationTime;
    }

    public void setCertOperationTime(String certOperationTime) {
        CertOperationTime = certOperationTime;
    }

    public String getCertStatus() {
        return CertStatus;
    }

    public void setCertStatus(String certStatus) {
        CertStatus = certStatus;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    @SerializedName("cert_status")
    public String CertStatus;
    @SerializedName("remark")
    public String Remark;
}

package com.kingold.educationblockchain.bean;

import com.google.gson.annotations.SerializedName;

public class CertInfo {
    @SerializedName("cert_id")
    private String  CertId;
    @SerializedName("stu_id")
    private String StudentId;
    @SerializedName("cert_no")
    private String CertNo;
    @SerializedName("cert_type")
    private String CertType;
    @SerializedName("cert_holder")
    private String CertHolder;
    @SerializedName("cert_name")
    private String CertName;
    @SerializedName("cert_content")
    private String  CertContent;
    @SerializedName("cert_pdf_path")
    private String CertPdfPath;
    @SerializedName("cert_hash")
    private String  CertHash;
    @SerializedName("cert_issuer")
    private String  CertIssuer;
    @SerializedName("cert_issue_date")
    private String CertIssueDate;
    @SerializedName("cert_operation_time")
    private String CertOperationTime;

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
    private String CertStatus;
    @SerializedName("remark")
    private String Remark;
}

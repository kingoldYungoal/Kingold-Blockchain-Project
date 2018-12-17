package com.kingold.educationblockchain.util;

import com.google.gson.annotations.SerializedName;

public class CertInfo {
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
    @SerializedName("cert_status")
    public String CertStatus;
    @SerializedName("remark")
    public String Remark;
}

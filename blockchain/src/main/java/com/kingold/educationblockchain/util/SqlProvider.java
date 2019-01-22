package com.kingold.educationblockchain.util;

import com.kingold.educationblockchain.bean.paramBean.CertificateParam;
import org.apache.ibatis.jdbc.SQL;

public class SqlProvider {
    private final String mCertificate = "KG_ELECTRONICSCERTIFICATE";

    private final String mStudentProfile = "kg_studentprofile";

    public String QueryCertByParam(CertificateParam param){
        SQL sql = new SQL().SELECT("*").FROM(mCertificate);
        if (param.getTeacherId() != null && param.getTeacherId().trim().length() > 0) {
            String teacherId = param.getTeacherId().trim();
            sql.WHERE("kg_teacherid=#{teacherId}");
        }
        if (param.getStudentId() != null && param.getStudentId().trim().length() > 0) {
            String studentId = param.getStudentId().trim();
            sql.WHERE("kg_studentprofileid=#{studentId}");
        }
        int year = param.getYear();
        if(year > 0){
            sql.WHERE("to_number(to_char(kg_certificatedate,'yyyy'))=#{year}");
        }

        if(param.getClassName() != null && param.getClassName().trim().length() > 0){
            String className = param.getClassName().trim();
            sql.WHERE("kg_classname=#{className}");
        }

        return sql.toString();
    }
}

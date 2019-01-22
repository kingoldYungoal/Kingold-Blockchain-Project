package com.kingold.educationblockchain.util;

import com.kingold.educationblockchain.bean.paramBean.CertificateParam;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Value;

public class SqlProvider {

    @Value("${sqlTable.electronicscertificate}")
    private String mCertificate;

    public String QueryCertByParam(CertificateParam param){
        SQL sql = new SQL().SELECT("*").FROM(mCertificate);
        String teacherId = param.getTeacherId().trim();
        if (teacherId.length() > 0) {
            sql.WHERE("kg_teacherid=#{teacherId}");
        }
        int year = param.getYear();
        if(year > 0){
            sql.WHERE("to_number(to_char(kg_certificatedate,'yyyy'))=#{year}");
        }
        String className = param.getClassName().trim();
        if(className.length() > 0){
            sql.WHERE("kg_classname=#{className}");
        }

        return sql.toString();
    }
}

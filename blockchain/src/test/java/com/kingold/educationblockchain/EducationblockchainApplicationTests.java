package com.kingold.educationblockchain;

import com.kingold.educationblockchain.controller.ElectronicscertificateController;
import com.kingold.educationblockchain.util.StreamCommon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EducationblockchainApplicationTests {

    @Value("${Certificate.TemplatePath}")
    private String mCertificateTemplatePath;
    @Test
    public void contextLoads() {
        StringBuffer certificateName = new StringBuffer(UUID.randomUUID().toString())
                .append(".pdf");

        String certificateFilePath = new StringBuffer(mCertificateTemplatePath)
                .append(certificateName).toString();

        Map<String,String> map = new HashMap();
        String certType="毕业证书";
        map.put("name","萧峰");
        map.put("certType",certType);
        map.put("schoolName","侨鑫汇悦天启幼儿园");
        if(certType=="毕业证书") {
            map.put("yearFrom","2015");
            map.put("yearTo","2018");
            map.put("monthFrom","9");
            map.put("monthTo","6");
            map.put("certId", "1234567");
        }
        if(certType=="课程证书") {
            map.put("issueDate", "2019-1-16");
            map.put("certName","体育课结业考试第一名");
        }

        if(certType=="录取通知书") {
            map.put("issueDate",  "2019-1-16");
            map.put("nameEn","Xiao Feng");
            map.put("registrationTime","2019-8-29");
            map.put("certNo","1234567");
        }
        //map.put("teachername",cert.getKg_teachername());
        //map.put("certificatedate",cert.getKg_certificatedate());
        ElectronicscertificateController electronicscertificateController=new ElectronicscertificateController();
        try {

            StreamCommon mStreamCommon = new StreamCommon();
            //electronicscertificateController.GeneratePdfCertificate(certificateFilePath, map);
            Resource schoolMasterResource = new ClassPathResource("static/天启幼儿园园长.png");
            Resource presidentResource = new ClassPathResource("static/事业部总裁孙总.png");
            InputStream schoolMasterInputStream = schoolMasterResource.getInputStream();
            byte[] schoolMasterBytes = mStreamCommon.read(schoolMasterInputStream);
            InputStream presidentInputStream = presidentResource.getInputStream();
            byte[] presidentBytes = mStreamCommon.read(presidentInputStream);

            electronicscertificateController.GeneratePdfCertificate(certificateFilePath, map,schoolMasterBytes,presidentBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

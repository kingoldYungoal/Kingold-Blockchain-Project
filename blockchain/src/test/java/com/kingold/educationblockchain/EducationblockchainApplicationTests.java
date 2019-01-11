package com.kingold.educationblockchain;

import com.kingold.educationblockchain.controller.ElectronicscertificateController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
                .append(certificateName).toString() ;

        Map<String,String> map = new HashMap();
        map.put("yearFrom","2015");
        map.put("yearTo","2018");
        map.put("monthFrom","9");
        map.put("monthTo","6");

        map.put("name","萧峰");
        map.put("certId","1234567");
        //map.put("teachername",cert.getKg_teachername());
        //map.put("certificatedate",cert.getKg_certificatedate());
        ElectronicscertificateController electronicscertificateController=new ElectronicscertificateController();
        try {
            electronicscertificateController.GeneratePdfCertificate(certificateFilePath, map,"C:\\Users\\qiuti\\Desktop\\1png.png");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

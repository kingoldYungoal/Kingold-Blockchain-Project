package com.kingold.educationblockchain;

import com.kingold.educationblockchain.bean.CertInfo;
import com.kingold.educationblockchain.bean.EventInfo;
import com.kingold.educationblockchain.bean.StudentJson;
import com.kingold.educationblockchain.controller.CommonController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EducationblockchainApplicationTests {

    @Test
    public void contextLoads() {
        String studentId="stut1";
        StudentJson studentJson= new StudentJson();
        studentJson.setStudentId(studentId);
        studentJson.setCrmId("crmId");
        studentJson.setRemark("remark");
        studentJson.setStudentEducationNo("eduNO");
        studentJson.setStudentIdCardNo("idCard");
        studentJson.setStudentNameString("name");
        studentJson.setStudentOperationTime("operTime");

        CommonController commonController = new CommonController();
        commonController.InitStudent(studentJson, "channel1");
    }

    @Test
    public void testInsertAndQueryCert() {
        String certId = "certId49";
        CertInfo certInfo = new CertInfo();
        certInfo.setCertContent("certContent");
        certInfo.setCertHash("certHash");
        certInfo.setCertHolder("certHolder");
        certInfo.setCertId(certId);
        certInfo.setCertIssuer("CertIssuer");
        certInfo.setCertName("certName");
        certInfo.setCertNo("certNo");
        certInfo.setCertOperationTime("OperationTime");
        certInfo.setCertPdfPath("PdfPath");
        certInfo.setCertIssueDate("issueDate");
        certInfo.setCertStatus("Status");
        certInfo.setCertType("type");
        certInfo.setStudentId("stu1");
        certInfo.setRemark("remark");
        CommonController commonController = new CommonController();
        commonController.InsertCertinfo(certInfo, "channel1");
        assert commonController.QueryCertById(certId, "channel1").getCertId().compareTo(certId) == 0;
    }
    @Test
    public void testInsertAndQueryEvent() {
        String evtId = "evtId48";
        EventInfo eventInfo = new EventInfo();
        eventInfo.setStudentId("stu1");
        eventInfo.setEventOrg("EventOrg");
        eventInfo.setEventDate("EventDate");
        eventInfo.setEventId(evtId);
        eventInfo.setEventContent("EventContent");
        eventInfo.setEventOperationTime("EventOperationTime");
        eventInfo.setRemark("remark");
        CommonController commonController = new CommonController();
        commonController.InsertEventinfo(eventInfo, "channel1");
        assert commonController.QueryEventById(evtId, "channel1").getEventId().compareTo(evtId) == 0;
    }
}

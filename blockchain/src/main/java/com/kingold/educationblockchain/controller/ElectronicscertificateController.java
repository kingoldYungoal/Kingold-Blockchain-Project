package com.kingold.educationblockchain.controller;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.kingold.educationblockchain.bean.CertInfo;
import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.service.ElectronicscertificateService;
import com.kingold.educationblockchain.service.StudentProfileService;
import com.kingold.educationblockchain.util.BlockChainPayload;
import com.kingold.educationblockchain.util.DateHandler;
import com.kingold.educationblockchain.util.RetResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.ParseException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.client.HttpClientErrorException;

import static com.kingold.educationblockchain.util.ResultResponse.makeErrRsp;
import static com.kingold.educationblockchain.util.ResultResponse.makeOKRsp;

@RestController
@RequestMapping("/api")
public class ElectronicscertificateController {
    @Autowired
    private ElectronicscertificateService mElectronicscertificateService;

    @Autowired
    private StudentProfileService mStudentProfileService;

    @Value("${CECS.BaseUrl}")
    private String mBaseUrl;

    @Value("${CECS.Authorization}")
    private String mAuthorization;

    @Value("${CECS.CertificateFileParentId}")
    private String mCertificateFileParentId;

    @Value("${chainCode.channel}")
    private String mChannel;

    @Value("${Certificate.TemplatePath}")
    private String mCertificateTemplatePath;

    private DateHandler mDateHandler;
    private BlockChainPayload mPayload = new BlockChainPayload();

    /*
     * 证书生成api
     * */
    @RequestMapping(value = "/issuecertificate", method = RequestMethod.POST)
    public RetResult IssueCertificate (
            @RequestBody String jsonParam){
        String certid = "";
        try{
            Electronicscertificate cert = JSONObject.parseObject(jsonParam,Electronicscertificate.class);
            if(cert.getKg_studentprofileid().trim().length() > 0) {
                if (mStudentProfileService.GetStudentProfileById(cert.getKg_studentprofileid()) != null) {
                    // 生成pdf证书:名称为 uuid 随机生成
                    StringBuffer certificateName = new StringBuffer(UUID.randomUUID().toString())
                            .append(".pdf");

                    String certificateFilePath = new StringBuffer(mCertificateTemplatePath)
                            .append(certificateName).toString() ;

                    Map<String,String> map = new HashMap();
                    map.put("name",cert.getKg_studentname());
                    map.put("classname",cert.getKg_classname());
                    //map.put("teachername",cert.getKg_teachername());
                    //map.put("certificatedate",cert.getKg_certificatedate());
                    GeneratePdfCertificate(certificateFilePath, map);

                    String fileId = UploadFileToCECS(certificateFilePath, certificateName.toString());
                    certid = fileId;
                    if(fileId == null){
                        return makeErrRsp("上传证书失败");
                    }

                    // 存证书ID到数据库  fileId
                    cert.setKg_electronicscertificateid(fileId);
                    boolean flag = mElectronicscertificateService.AddCertificate(cert);
                    if(flag){
                        // 证书信息上链
                        CertInfo certInfo = new CertInfo();
                        certInfo.setCertId(cert.getKg_electronicscertificateid());
                        certInfo.setStudentId(cert.getKg_studentprofileid());
                        certInfo.setCertNo(cert.getKg_certificateno());
                        certInfo.setCertType(cert.getKg_certitype());
                        certInfo.setCertHolder(cert.getKg_studentname());
                        certInfo.setCertName(cert.getKg_name());
                        //certInfo.setCertContent(cert.getKg_explain());
                        certInfo.setCertPdfPath(fileId);
                        //certInfo.setCertHash();
                        certInfo.setCertIssuer(cert.getKg_schoolname());
                        certInfo.setCertIssueDate(cert.getKg_certificatedate());
                        mDateHandler = new DateHandler();
                        certInfo.setCertOperationTime(mDateHandler.GetCurrentTime());
                        certInfo.setCertStatus("0");
                        certInfo.setRemark(cert.getKg_certitype());
                        certInfo.setStuSchool(cert.getKg_schoolname());
                        certInfo.setStuClass(cert.getKg_classname());
                        certInfo.setStuTeacher(cert.getKg_teachername());
                        certInfo.setStuStudygrade(cert.getKg_studygrade());
                        InsertCertinfo(certInfo,mChannel);
                    }
                }
            }
            return makeOKRsp();
        }catch (HttpClientErrorException e1) {
            //删除表中新增的数据
            mElectronicscertificateService.DeleteCertificate(certid);
            return makeErrRsp("证书上链失败");
        }catch (Exception ex){
            return makeErrRsp(ex.getMessage());
        }
    }


    /*
     * 生成证书
     * */
    private void GeneratePdfCertificate(String certificateFilePath, Map<String,String> fields ) throws Exception{
        //Resource resource = new ClassPathResource("certificate-template.pdf");
        Resource resource = new ClassPathResource("static/certificate-template.pdf");
        File file = resource.getFile();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(file.getPath()), new PdfWriter(certificateFilePath));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        form.setGenerateAppearance(true);

        for(String fieldName: fields.keySet()){
            form.getField(fieldName).setValue(fields.get(fieldName)).setReadOnly(true).setFontSize(15);
        }

        pdfDoc.close();
    }

    /*
     * 上传证书
     * */
    private String UploadFileToCECS(String filePath, String fileName) throws ParseException, IOException{
        File file = new File(filePath);
        FileInputStream inStream = new FileInputStream(file);
        CloseableHttpClient client = HttpClientBuilder.create().build();

        String url = mBaseUrl + "data";
        HttpPost post = new HttpPost(url);
        post.addHeader("Authorization", mAuthorization);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        StringBuffer json = new StringBuffer();
        json.append("{").append("\"parentID\":\"").append(mCertificateFileParentId).append("\"}");
        builder.addTextBody("jsonInputParameters", json.toString(), ContentType.APPLICATION_JSON);

        builder.addBinaryBody("primaryFile", inStream, ContentType.create("application/pdf"), fileName);// 文件流

        HttpEntity entity = builder.build();
        post.setEntity(entity);
        HttpResponse response = client.execute(post);// 执行提交
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity responseEntity = response.getEntity();
        if ((200 <= statusCode && statusCode < 300) && responseEntity != null) {
            // 将响应内容转换为字符串
            String result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            JSONObject jsonObj = JSONObject.parseObject(result);
            return jsonObj.getString("id");
        }
        return  null;
    }

    /*
   证书信息上链
    */
    public String InsertCertinfo(CertInfo certInfo,String channelName) {
        try {
            return mPayload.GetPayload("insertCertinfo", getInsertCertJson(certInfo),channelName).toString();
        } catch (HttpClientErrorException ex) {
            throw ex;
        }
    }

    private String getInsertCertJson(CertInfo cert)
    {
        return  String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                cert.getCertId() ,
                cert.getStudentId(),
                cert.getCertNo(),
                cert.getCertType(),
                cert.getCertHolder(),
                cert.getCertName(),
                cert.getCertContent(),
                cert.getCertPdfPath(),
                cert.getCertHash(),
                cert.getCertIssuer(),
                cert.getCertIssueDate(),
                cert.getCertOperationTime(),
                cert.getCertStatus(),
                cert.getRemark(),
                cert.getStuSchool(),
                cert.getStuClass(),
                cert.getStuTeacher(),
                cert.getStuStudyGrade());
    }
}
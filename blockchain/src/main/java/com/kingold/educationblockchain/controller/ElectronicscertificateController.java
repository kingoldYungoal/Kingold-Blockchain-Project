package com.kingold.educationblockchain.controller;

import com.google.gson.Gson;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.service.ElectronicscertificateService;
import com.kingold.educationblockchain.service.StudentProfileService;
import com.kingold.educationblockchain.util.RetResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

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

    private static final String mCertificateTemplateFilePath = "./src/main/resources/static/certificate-template.pdf";

    @RequestMapping(value = "/issuecertificate", method = RequestMethod.POST)
    public RetResult IssueCertificate(@RequestBody Electronicscertificate electronicscertificate) {
        Gson gson = new Gson();
        boolean flag = false;
        try{
            if(electronicscertificate.getKg_studentprofileid().trim().length() > 0){
                if(mStudentProfileService.GetStudentProfileById(electronicscertificate.getKg_studentprofileid()) != null){
                    System.out.println(electronicscertificate.getKg_studentprofileid());
                    flag = mElectronicscertificateService.AddCertificate(electronicscertificate);
                }
            }
            if(flag){
                return makeOKRsp();
            }else{
                return makeErrRsp("添加证书信息失败");
            }
        }catch (Exception ex){
            return makeErrRsp(ex.getMessage());
        }
    }

    /*
     * 证书生成api
     * */
    @RequestMapping(value = "/generatepdfcertificate", method = RequestMethod.GET)
    public RetResult GeneratePdfCertificate (
            @RequestParam(value = "name", required = true)String name,
            @RequestParam(value = "classname", required = true)String classname){
        try{
            StringBuffer certificateName = new StringBuffer("certificate-template-")
                    .append(name).append(".pdf");

            String certificateFilePath = new StringBuffer("./src/main/resources/static/")
                    .append(certificateName).toString();

            Map<String,String> map = new HashMap();
            map.put("name",name);
            map.put("classname",classname);
            GeneratePdfCertificate(certificateFilePath, map);

            String fileId = UploadFileToCECS(certificateFilePath, certificateName.toString());

            if(fileId == null){
                return makeErrRsp("上传证书失败");
            }

            // 存证书ID到数据库  fileId
            // save(fileId)

            return makeOKRsp();
        }catch (Exception ex){
            return makeErrRsp(ex.getMessage());
        }
    }

    /*
     * 生成证书
     * */
    private void GeneratePdfCertificate(String certificateFilePath, Map<String,String> fields ) throws Exception{
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(mCertificateTemplateFilePath), new PdfWriter(certificateFilePath));

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
}
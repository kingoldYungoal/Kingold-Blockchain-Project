package com.kingold.educationblockchain.controller;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.kingold.educationblockchain.bean.CertInfo;
import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.service.ElectronicscertificateService;
import com.kingold.educationblockchain.service.ErrorLogService;
import com.kingold.educationblockchain.service.StudentProfileService;
import com.kingold.educationblockchain.util.*;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

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

import javax.servlet.http.HttpServletRequest;

import static com.itextpdf.io.font.PdfEncodings.IDENTITY_H;
import static com.kingold.educationblockchain.util.ResultResponse.makeErrRsp;
import static com.kingold.educationblockchain.util.ResultResponse.makeOKRsp;

@RestController
@RequestMapping("/api")
public class ElectronicscertificateController {
    @Autowired
    private ElectronicscertificateService mElectronicscertificateService;

    @Autowired
    private StudentProfileService mStudentProfileService;

    @Autowired
    private ErrorLogService mErrorLogService;

    @Value("${CECS.BaseUrl}")
    private String mBaseUrl;

    @Value("${CECS.Authorization}")
    private String mAuthorization;

    @Value("${CECS.CertificateFileParentId}")
    private String mCertificateFileParentId;

    @Value("${chainCode.channel}")
    private String mChannel;

    private DateHandler mDateHandler;
    private BlockChainPayload mPayload = new BlockChainPayload();
    private StreamCommon mStreamCommon = new StreamCommon();
    private StringWriter stringWriter = new StringWriter();
    private PrintWriter printWriter = new PrintWriter(stringWriter);
    private LoggerException loggerException = new LoggerException();
    private RecordErrorLog recordErrorLog = new RecordErrorLog();

    /*
     * 证书生成api
     * */
    @RequestMapping(value = "/issuecertificate", method = RequestMethod.POST)
    public RetResult IssueCertificate (
            @RequestBody String jsonParam, HttpServletRequest request){
        String certid = "";
        String absolutePath = "";
        try{
            Electronicscertificate cert = JSONObject.parseObject(jsonParam,Electronicscertificate.class);
            Electronicscertificate existCert = mElectronicscertificateService.GetCertificateByStudentIdAndCertno(cert.getKg_certificateno(),cert.getKg_studentprofileid());
            if(existCert != null){
                return makeErrRsp("证书已存在，无法重复添加");
            }
            if(cert.getKg_studentprofileid().trim().length() > 0) {
                if (mStudentProfileService.GetStudentProfileById(cert.getKg_studentprofileid()) != null) {
                    // 生成pdf证书:名称为 uuid 随机生成
                    StringBuffer certificateName = new StringBuffer(UUID.randomUUID().toString())
                            .append(".pdf");

                    Resource templeResource = new ClassPathResource("/");
                    File templeFile = templeResource.getFile();
                    String templePath = templeFile.getPath();
                    String newPaths = "/temp/"+certificateName.toString();
                    File tempCertFiles = new File(templePath + newPaths);
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), jsonParam, tempCertFiles.getAbsolutePath(), tempCertFiles.getPath(), "test"));
                    absolutePath = tempCertFiles.getAbsolutePath();
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), jsonParam, absolutePath, tempCertFiles.getPath(), "test"));
                    String tempCertpPath = tempCertFiles.getPath();
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), jsonParam, absolutePath, tempCertpPath, "test"));

                    String testPath = request.getSession().getServletContext().getRealPath("/");
                    String newPath = testPath + certificateName;
                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), jsonParam, testPath, newPath, "test1"));


                    Map<String,String> map = new HashMap();
                    map.put("name",cert.getKg_studentname());
                    map.put("certType",cert.getKg_certitype());
                    map.put("schoolName",cert.getKg_schoolname());

                    Resource schoolMasterResource;
                    switch(cert.getKg_schoolname()) {
                        case "侨鑫汇悦天启幼儿园":
                            schoolMasterResource = new ClassPathResource("static/天启幼儿园园长.png");
                            break;
                        case "侨鑫汇景新城幼儿园":
                            schoolMasterResource = new ClassPathResource("static/汇景幼儿园园长.png");
                            break;
                        default:
                            schoolMasterResource = new ClassPathResource("static/schoolmaster.png");
                    }

                    InputStream schoolMasterInputStream = schoolMasterResource.getInputStream();
                    byte[] schoolMasterBytes = mStreamCommon.read(schoolMasterInputStream);

                    Resource presidentResource = new ClassPathResource("static/事业部总裁孙总.png");
                    InputStream presidentInputStream = presidentResource.getInputStream();
                    byte[] presidentBytes = mStreamCommon.read(presidentInputStream);

                    if(cert.getKg_certitype().equals("毕业证书")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dateFrom = sdf.parse(cert.getKg_starttime());
                        Date dateTo = sdf.parse(cert.getKg_endtime());

                        Calendar calendarFrom = Calendar.getInstance();
                        calendarFrom.setTime(dateFrom);
                        map.put("yearFrom",String.valueOf(calendarFrom.get(Calendar.YEAR)));
                        map.put("monthFrom",String.valueOf(calendarFrom.get(Calendar.MONTH) + 1));

                        Calendar calendarTo = Calendar.getInstance();
                        calendarTo.setTime(dateTo);
                        map.put("yearTo",String.valueOf(calendarTo.get(Calendar.YEAR)));
                        map.put("monthTo",String.valueOf(calendarTo.get(Calendar.MONTH) + 1));

                        map.put("certId", cert.getKg_certificateno());

                        GeneratePdfCertificate(newPath,absolutePath, map, schoolMasterBytes, presidentBytes);
                    }
                    if(cert.getKg_certitype().equals("课程证书")) {
                        map.put("issueDate", cert.getKg_certificatedate());
                        map.put("certName",cert.getKg_name());
                        GeneratePdfCertificate(newPath,absolutePath, map, schoolMasterBytes, presidentBytes);
                    }

                    if(cert.getKg_certitype().equals("录取通知书")) {
                        map.put("issueDate", cert.getKg_certificatedate());
                        map.put("certNo",cert.getKg_certificateno());
                        map.put("nameEn",cert.getKg_studentenglishname());
                        map.put("registrationTime",cert.getKg_starttime());
                        try{
                            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), jsonParam, absolutePath, tempCertpPath, "test"));
                            GeneratePdfCertificate(newPath,absolutePath, map, schoolMasterBytes, presidentBytes);
                            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), jsonParam, absolutePath, tempCertpPath, "test"));
                        }catch(Exception ex){
                            ex.printStackTrace(printWriter);
                            ex.getMessage();
                            loggerException.PrintExceptionLog("GeneratePdfCertificate",this.getClass().getName(), jsonParam, ex, stringWriter.toString());
                            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
                        }
                    }

                    mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), jsonParam, absolutePath, tempCertpPath, "test"));

                    String fileId = UploadFileToCECS(newPath, certificateName.toString(),request);
                    certid = fileId;
                    if(fileId == null){
                        return makeErrRsp("上传证书失败");
                    }

                    // 删除生成的证书
                    File certFile = new File(newPath);
                    if(certFile.isFile() && certFile.exists()){
                        certFile.delete();
                    }

                    // 存证书ID到数据库  fileId
                    cert.setKg_electronicscertificateid(fileId);
                    cert.setKg_state(0);
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
                        try{
                            InsertCertinfo(certInfo,mChannel);
                        }catch(Exception e){
                            e.printStackTrace(printWriter);
                            e.getMessage();
                            loggerException.PrintExceptionLog("InsertCertinfo",this.getClass().getName(), jsonParam, e, stringWriter.toString());
                            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, jsonParam, absolutePath, "", stringWriter.toString()));
                        }
                    }
                }
            }
            return makeOKRsp();
        }catch (HttpClientErrorException e1) {
            //删除表中新增的数据
            mElectronicscertificateService.DeleteCertificate(certid);
            e1.printStackTrace(printWriter);
            e1.getMessage();
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, e1, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e1, jsonParam, absolutePath, "", stringWriter.toString()));
            return makeErrRsp("证书上链失败");
        }catch (Exception ex){
            ex.printStackTrace(printWriter);
            ex.getMessage();
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, absolutePath, "", stringWriter.toString()));
            return makeErrRsp(ex.getMessage());
        }
    }

    /*
     * 生成证书
     * */
    public void GeneratePdfCertificate(String tempCertpPath,String absolutePath, Map<String,String> fields,byte[] schoolMasterBytes, byte[] presidentBytes) throws Exception{
        String logoPath = "static/logo/";
        switch(fields.get("schoolName")){
            case "侨鑫汇景新城实验小学" :
                logoPath+="logo-01.png";
                fields.put("schoolNameEn","FAVORVIEW PALACE KINGOLD PRIMARY SCHOOL");
                break;
            case "侨鑫汇景新城幼儿园" :
                logoPath+="logo-02.png";
                fields.put("schoolNameEn","FAVORVIEW PALACE KINGOLD INTERNATIONAL KINDERGARTEN");
                break;
            case "侨鑫汇悦天启幼儿园" :
                logoPath+="logo-03.png";
                fields.put("schoolNameEn","THE BAYVIEW KINGOLD INTERNATIONAL KINDERGARTEN");
                break;
            case "侨鑫增城温可儿幼儿园" :
                logoPath+="logo-04.png";
                fields.put("schoolNameEn","WINNER ZENGCHENG KINGOLD INTERNATIONAL KINDERGARTEN");
                break;
            case "KIDS KING" :
                logoPath+="logo-05.png";
                fields.put("schoolNameEn","KINGOLD NURSERY");
                break;
        }
        Resource resource = new ClassPathResource("static/certificate-template.pdf");
        //File file = resource.getFile();
        //PdfDocument pdfDocRead = new PdfDocument(new PdfReader(file.getPath()));
        PdfDocument pdfDocRead = new PdfDocument(new PdfReader(resource.getInputStream()));

        PdfWriter pdfWriter = new PdfWriter(tempCertpPath);
        mErrorLogService.AddErrorLog(recordErrorLog.RecordError(new Exception(), "", absolutePath, tempCertpPath, "test"));
        PdfDocument pdfDocWrite = new PdfDocument(pdfWriter);
        int page=1;
        if(fields.get("certType").equals("录取通知书"))
        {
            if(fields.get("schoolName").endsWith("小学")){
                page=5;
            }
            else{
                page=4;
            }
        }
        else if(fields.get("certType").equals("课程证书")){
            page=3;
        }
        else if(fields.get("certType").equals("毕业证书")) {
            if(fields.get("schoolName").endsWith("小学")){
                page=1;
            }
            else{
                page=2;
            }
        }
        pdfDocRead.copyPagesTo(page,page,pdfDocWrite,new PdfPageFormCopier());
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocWrite, true);
        form.setGenerateAppearance(true);
        PdfFont fontRuiYun = PdfFontFactory.createFont("static/font/锐字云字库小标宋体GBK.TTF", IDENTITY_H ,false);
        PdfFont fontUtopia = PdfFontFactory.createFont("static/font/Utopia Regular.ttf", IDENTITY_H ,false);
        PdfFont fontYuWei = PdfFontFactory.createFont("static/font/禹卫书法行书繁体（优化版）.ttf", IDENTITY_H ,false);

        for(String fieldName: fields.keySet()){
            if(form.getField(fieldName)==null)
                continue;
            if(fieldName=="name") {
                form.getField(fieldName).setFontAndSize(fontYuWei,41);
            }
            else if(fieldName=="nameEn" || fieldName == "schoolNameEn") {
                form.getField(fieldName).setFontAndSize(fontUtopia,14);
            }
            else if(fieldName=="certName"){
                form.getField(fieldName).setFontAndSize(fontYuWei,18);
            }
            else if(fieldName=="registrationTime"||fieldName=="certNo"){
                form.getField(fieldName).setFontAndSize(fontRuiYun,11);
            }
            else {
                form.getField(fieldName).setFontAndSize(fontRuiYun,14);
            }
            form.getField(fieldName).setValue(fields.get(fieldName)).setReadOnly(true);
        }
        PdfPage pdfPage = pdfDocWrite.getPage(1);
        PdfCanvas canvas = new PdfCanvas(
                pdfPage.newContentStreamAfter(),
                pdfPage.getResources(),
                pdfDocWrite);
        Resource logoResource = new ClassPathResource(logoPath);
        File logoFile = logoResource.getFile();
        ImageData logo = ImageDataFactory.create(logoFile.getPath());
        canvas.addImage(logo,225, 650,150,false);

        ImageData sign1 = ImageDataFactory.create(schoolMasterBytes);

        if(fields.get("certType").equals("毕业证书")) {
            canvas.addImage(sign1,75, 230,100,false);
            ImageData teacherSign = ImageDataFactory.create(presidentBytes);
            canvas.addImage(teacherSign,400, 230,100,false);
        }
        else if(fields.get("certType").equals("录取通知书")) {
            canvas.addImage(sign1,100, 240,100,false);
        }
        else if(fields.get("certType").equals("课程证书")) {
            canvas.addImage(sign1,250, 170,100,false);
            ImageData teacherSign = ImageDataFactory.create(presidentBytes);
            canvas.addImage(teacherSign,75, 170,100,false);
        }

        pdfDocWrite.close();
        pdfDocRead.close();
    }

    /*
     * 上传证书
     * */
    private String UploadFileToCECS(String filePath, String fileName, HttpServletRequest request) throws ParseException, IOException{
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
            ex.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), getInsertCertJson(certInfo), ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, getInsertCertJson(certInfo), "", "", stringWriter.toString()));
            throw ex;
        } catch(Exception e){
            e.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), getInsertCertJson(certInfo), e, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(e, getInsertCertJson(certInfo), "", "", stringWriter.toString()));
            return null;
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

    /*
     * 证书撤销api
     * */
    @RequestMapping(value = "/revokecertificate", method = RequestMethod.POST)
    public RetResult RevokeCertificate (@RequestBody String jsonParam){
        try{
            JSONObject jsonx = JSONObject.parseObject(jsonParam);

            String certificateno = jsonx.getString("kg_certificateno");
            String studentid = jsonx.getString("kg_studentid");
            if(certificateno.trim().length() == 0 || studentid.trim().length() == 0){
                return makeErrRsp("证书编号或者学生档案号不能为空");
            }
            //查询对应的证书
            Electronicscertificate cert = mElectronicscertificateService.GetCertificateByStudentIdAndCertno(certificateno, studentid);
            if(cert == null){
                return makeErrRsp("证书不存在");
            }else{
                //执行撤销操作
                mElectronicscertificateService.DeleteCertificate(cert.getKg_electronicscertificateid());
                return makeOKRsp("证书撤销成功");
            }
        }catch (Exception ex){
            ex.printStackTrace(printWriter);
            loggerException.PrintExceptionLog(Thread.currentThread().getStackTrace()[1].getMethodName(),this.getClass().getName(), jsonParam, ex, stringWriter.toString());
            mErrorLogService.AddErrorLog(recordErrorLog.RecordError(ex, jsonParam, "", "", stringWriter.toString()));
            return makeErrRsp(ex.getMessage());
        }
    }
}
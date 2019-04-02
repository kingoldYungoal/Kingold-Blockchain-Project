package com.kingold.educationblockchain.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.kingold.educationblockchain.bean.CertificationType;
import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.service.ElectronicscertificateService;

@Controller
@RequestMapping("/electronicscertificate")
public class ShowElectronicscertificateController {
    @Autowired
    private ElectronicscertificateService mElectronicscertificateService;

    @Value("${CECS.BaseUrl}")
    private String mBaseUrl;

    @Value("${CECS.Authorization}")
    private String mAuthorization;

    @Value("${CECS.CertificateFileParentId}")
    private String mCertificateFileParentId;

    private Gson gson;

    private List<String> certIdArray = new ArrayList<>();
    
    @RequestMapping(value = "/certificationTypeList", method = RequestMethod.GET)
    @ResponseBody
    public String GetCertificationTypeList(@RequestParam(value = "classId", required = true)String classId) {
        gson = new Gson();
        List<CertificationType> list = mElectronicscertificateService.getCertificationTypeByClassId(classId);
        return gson.toJson(list);
    }

    /*
    * 学生证书页面
    * */
    @RequestMapping(value = "/studentcertificate", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView StudentCertificate(@RequestParam(value = "fileId", required = true)String fileId,@RequestParam(value = "device", required = true)String device) {
        ModelAndView model = new ModelAndView();
        model.addObject("certificateid",fileId);
        if(device.trim().equals("mobile")){
            model.setViewName("mobileStudentCert");
        }else{
            model.setViewName("studentcertdetail");
        }
        return model;
    }

    @RequestMapping(value = "/certificatelist", method = RequestMethod.POST)
    @ResponseBody
    public String GetCertificateList(@RequestBody JSONObject params){
        gson = new Gson();
        String classId = params.getString("classId");
        String certiType = params.getString("certiType");
        List<String> certIdList = mElectronicscertificateService.GetCertificateIdsByCertiTypeAndClassId(certiType, classId);
        certIdArray = certIdList;
        return gson.toJson(certIdList);
    }

    /*
    * 展示证书
    * */
    @RequestMapping(value = "/certificate/show", produces="application/jpg")
    public void ShowCertificate(@RequestParam(value = "fileid", required = true)String fileid,HttpServletResponse response) throws Exception{
        response.setContentType("application/jpg");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "filename=certificate.jpg");
        OutputStream outputStream = response.getOutputStream();
        GetJPGStreamFromCECS(fileid, outputStream);
    }

    /*
     * 展示pdf证书
     * */
    @RequestMapping(value = "/certificate/showPdf", produces="application/pdf")
    public void ShowPDFCertificate(HttpServletResponse response) throws Exception{
        String certificateName = "certificate.pdf";
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "inline;filename="+certificateName);
        if(certIdArray.size() > 0){
            GetPDFListStreamFromCECS(certIdArray,response.getOutputStream());
        }
    }

    /*
    * 下载证书
    * */
    @RequestMapping(value = "/certificate/download", produces="application/pdf")
    public void DownloadCertificate(@RequestParam(value = "fileid", required = true)String fileid,HttpServletResponse response) throws Exception{
        String certificateName = "certificate.pdf";

        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+certificateName);

        OutputStream outputStream = response.getOutputStream();
        GetPDFStreamFromCECS(fileid, outputStream);
    }

    /*
    * 从CECS中获取文件流
    * */
    private void GetPDFStreamFromCECS(String fileid, OutputStream outputStream) throws Exception{
        InputStream inputStream = DownloadFileFromCECS(fileid);

        if(inputStream == null){
            throw new Exception("获取证书失败");
        }

        int intByte;
        while((intByte = inputStream.read()) != -1) {
            outputStream.write(intByte);
        }
        outputStream.close();
    }

    /*
     * 从CECS中获取文件流
     * */
    private void GetPDFListStreamFromCECS(List<String> fileids, OutputStream outputStream) throws Exception{
        if(!fileids.isEmpty()) {
            PdfDocument pdfDocWriter = new PdfDocument(new PdfWriter(outputStream));
            for (String fileid : fileids) {
                InputStream inputStream = DownloadFileFromCECS(fileid);
                PdfDocument pdfDocReader = new PdfDocument(new PdfReader(inputStream));
                pdfDocReader.copyPagesTo(1, 1, pdfDocWriter);
                pdfDocReader.close();
            }
            pdfDocWriter.close();
        }
    }


    /*
     * 从CECS中获取JPG文件流
     * */
    private void GetJPGStreamFromCECS(String fileid, OutputStream outputStream) throws Exception{
        InputStream certificateInputStream = DownloadFileFromCECS(fileid);

        if(certificateInputStream == null){
            throw new Exception("获取证书失败");
        }

        PDDocument doc = PDDocument.load(certificateInputStream);
        PDFRenderer pdfRenderer = new PDFRenderer(doc);
        PDPageTree pages = doc.getPages();
        int pageCount = pages.getCount();
        if(pageCount<1){
            throw new Exception("PDF证书转化为JPG图片失败");
        }

        BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 200);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bim, "jpg", os);
        byte[] datas = os.toByteArray();
        InputStream jpgInputStream = new ByteArrayInputStream(datas);

        int intByte;
        while((intByte = jpgInputStream.read()) != -1) {
            outputStream.write(intByte);
        }
        outputStream.close();
    }

    /*
     * 从CECS中获取文件流
     * */
    private InputStream DownloadFileFromCECS(String fileId) throws Exception{
        HttpResponse response;

        try{
            String url = mBaseUrl + fileId + "/data?version=1";
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("Authorization", mAuthorization);
            httpget.addHeader("Connection", "keep-alive");
            response = httpClient.execute(httpget);

            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            if ((200 <= statusCode && statusCode < 300) && responseEntity != null) {
                return responseEntity.getContent();
            }
        }catch(Exception ex){
            throw new Exception(ex.getMessage());
        }
        throw new Exception(response.toString());
    }

    // 对学生id重复的去重
    public ArrayList<Electronicscertificate> RemoveDuplicateCert(List<Electronicscertificate> certs){
        Set<Electronicscertificate> set = new TreeSet<Electronicscertificate>(new Comparator<Electronicscertificate>() {
            @Override
            public int compare(Electronicscertificate e1, Electronicscertificate e2) {
                //字符串,则按照asicc码升序排列
                return e1.getKg_electronicscertificateid().compareTo(e2.getKg_electronicscertificateid());
            }
        });
        set.addAll(certs);
        return new ArrayList<Electronicscertificate>(set);
    }
}

package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.bean.PageBean;
import com.kingold.educationblockchain.bean.StudentInfo;
import com.kingold.educationblockchain.bean.TeacherInformation;
import com.kingold.educationblockchain.bean.paramBean.CertificateParam;
import com.kingold.educationblockchain.service.ElectronicscertificateService;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

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

    /*
    * 学生证书页面
    * */
    @RequestMapping(value = "/studentcertificate", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView StudentCertificate(@RequestParam(value = "fileId", required = true)String fileId) {
        //获取证书Id逻辑
        ModelAndView model = new ModelAndView();
        model.addObject("certificateid",fileId);
        //model.setViewName("studentcertdetail");
        model.setViewName("mobileStudentCert");
        return model;
    }

    @RequestMapping(value = "/certificatelist", method = RequestMethod.POST)
    @ResponseBody
    public String GetCertificateList(@RequestBody JSONObject params){
        gson = new Gson();
        String className = params.getString("className");
        int year = params.getInteger("year");
        String teacherId = params.getString("teacherId");
        //ArrayList<String> studentIdList = (ArrayList) params.get("stuIds");
        List<String> certIds = new ArrayList<>();
        //if(studentIdList.size() > 0){
        List<Electronicscertificate> allCertList = new ArrayList<>();
        CertificateParam param = new CertificateParam();
        param.setClassName(className);
        param.setYear(year);
        param.setTeacherId(teacherId);
//            for(String stuId: studentIdList){
//                param.setStudentId(stuId);

        List<Electronicscertificate> certList = mElectronicscertificateService.GetCertificatesByParam(param);
        allCertList.addAll(certList);
//            }
            List<Electronicscertificate> newCerts = RemoveDuplicateCert(allCertList);
            for(Electronicscertificate cert : newCerts){
                certIds.add(cert.getKg_electronicscertificateid());
            }
            certIdArray = certIds;
        //}

        return gson.toJson(certIds);
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
        //SSLContextBuilder builder = new SSLContextBuilder();
        HttpResponse response;

        try{
            //builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            //SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
            //        builder.build());
            //RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(150000).setSocketTimeout(150000).build();
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

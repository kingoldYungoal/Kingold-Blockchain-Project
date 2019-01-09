package com.kingold.educationblockchain.controller;

import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

@Controller
@RequestMapping("/electronicscertificate")
public class ShowElectronicscertificateController {
    @Value("${CECS.BaseUrl}")
    private String mBaseUrl;

    @Value("${CECS.Authorization}")
    private String mAuthorization;

    @Value("${CECS.CertificateFileParentId}")
    private String mCertificateFileParentId;

    /*
    * 学生证书页面
    * */
    @RequestMapping(value = "/studentcertificate", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView StudentCertificate(@RequestParam(value = "fileId", required = true)String fileId) {
        //获取证书Id逻辑
        ModelAndView model = new ModelAndView();
        model.addObject("certificateid",fileId);
        model.setViewName("studentcertdetail");
        return model;
    }

    /*
    * 展示证书
    * */
    @RequestMapping(value = "/certificate/show", produces="application/jpg")
    public void ShowCertificate(@RequestParam(value = "fileid", required = true)String fileid,HttpServletResponse response) throws Exception{
        response.setContentType("application/jpg");
        response.setCharacterEncoding("utf-8");
        //response.setHeader("Content-Disposition", "filename=certificate.jpg");
        OutputStream outputStream = response.getOutputStream();
        //DownloadFileFromCECS(fileid, outputStream);
        GetJPGStreamFromCECS(fileid, outputStream);
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
}

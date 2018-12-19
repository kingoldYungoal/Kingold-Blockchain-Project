package com.kingold.educationblockchain.controller;

import org.apache.http.ParseException;
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
    public ModelAndView StudentCertificate(@RequestParam(value = "fileId", required = true)String fileId) {
        //获取证书Id逻辑
        // fileId ="";
        //String fileIds = "D74E601160ACFA522860ACB1B85DDA650E6B10A3A8FA";

        ModelAndView model = new ModelAndView();
        model.addObject("certificateid",fileId);
        model.setViewName("studentcertdetail");
        return model;
    }

    /*
    * 展示证书
    * */
    @RequestMapping(value = "/certificate/show", produces="application/pdf")
    public void ShowCertificate(@RequestParam(value = "fileid", required = true)String fileid,HttpServletResponse response) throws Exception{
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "filename=certificate.pdf");
        OutputStream outputStream = response.getOutputStream();
        DownloadFileFromCECS(fileid, outputStream);
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
        DownloadFileFromCECS(fileid, outputStream);
    }

    /*
    * 从CECS中获取文件流
    * */
    private void DownloadFileFromCECS(String fileId, OutputStream outputStream) throws ParseException, IOException{
        CloseableHttpClient client = HttpClientBuilder.create().build();

        String url = mBaseUrl + fileId + "/data?version=1";
        HttpGet get = new HttpGet(url);
        get.addHeader("Authorization", mAuthorization);

        HttpResponse response = client.execute(get);

        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity responseEntity = response.getEntity();
        if ((200 <= statusCode && statusCode < 300) && responseEntity != null) {
            InputStream inputStream = responseEntity.getContent();
            int intByte;
            while((intByte=inputStream.read()) != -1) {
                outputStream.write(intByte);
            }
            outputStream.close();
        }
    }
}

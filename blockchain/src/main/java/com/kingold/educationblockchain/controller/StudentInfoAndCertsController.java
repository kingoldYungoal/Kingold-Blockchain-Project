package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.kingold.educationblockchain.util.CertInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Controller

public class StudentInfoAndCertsController {

    @RequestMapping(value="/certList",method = RequestMethod.GET)
    public String list(ModelMap model, HttpServletRequest request) {
        try {
            model.addAttribute("title", "学生信息及所获证书");
            CommonController commonController =new CommonController();
            List<CertInfo> json=  commonController.QueryCertByCRMId(request.getParameter("crm_id"),request.getParameter("channel"));
            model.addAttribute("json", json);
        }
        catch (HttpClientErrorException ex)
        {
            String errorMessage = ex.getResponseBodyAsString();
            model.addAttribute("err_message", errorMessage);
            return "error";
        }

        return "studentinfoandcerts";
    }
}

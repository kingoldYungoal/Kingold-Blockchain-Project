package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


@Controller

public class StudentInfoAndCertsController {
    private static final String  authCode = "Basic aGVsaXgubGl1QG9yYWNsZS5jb206QWJjZDEyMzQ=";
    @RequestMapping(value="/certList",method = RequestMethod.GET)
    public String list(ModelMap model) {
        try {
            model.addAttribute("title", "学生信息及所获证书");

            String urlStr="https://yungoal-kingoldcloud.blockchain.ocp.oraclecloud.com:443/restproxy1/bcsgw/rest/v1/transaction/invocation";
            RestTemplate restTemplate=new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            String requestStr="{\"channel\":\"channel1\",\"chaincode\":\"kingold\",\"method\":\"queryEventByEducationNo\",\"args\":[\"edu1\"],\"chaincodeVer\":\"v7\"}";
            headers.add("Content-Type","application/json");
            headers.add("Connection", "keep-alive");
            headers.add("Authorization","Basic aGVsaXgubGl1QG9yYWNsZS5jb206QWJjZDEyMzQ=");
            HttpEntity<String> request1=new HttpEntity<String>(requestStr,headers);
            ResponseEntity<String> respose = restTemplate.postForEntity(urlStr, request1,String.class);

            model.addAttribute("json", respose.getBody());
        }
        catch (HttpClientErrorException ex)
        {
            String s = ex.getResponseBodyAsString();
            return "error";
        }

        return "studentinfoandcerts";
    }
}

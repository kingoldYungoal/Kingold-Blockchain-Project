package com.kingold.educationblockchain.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/smscode")
public class SmsCodeController {

    @Value("${sms.smsSendUserName}")
    private String smsSendUserName;

    @Value("${sms.smsSendPassword}")
    private String smsSendPassword;

    @Value("${sms.smsSendCodeUrl}")
    private String smsSendCodeUrl;

    @Value("${sms.smsGetCodeUrl}")
    private String smsGetCodeUrl;

    private Gson gson;

    @RequestMapping(value = "/sendsmscode", method = RequestMethod.POST)
    @ResponseBody
    public String SendSmsCode(@RequestBody JSONObject params){
        String phone = params.getString("phone");
        if(phone != null && phone.trim().length() > 0){
            JSONObject requestJson = new JSONObject();
            requestJson.put("userName", smsSendUserName);
            requestJson.put("password", smsSendPassword);
            requestJson.put("phone", phone);

            return CommonSmsCode(smsSendCodeUrl, requestJson.toString());
        }else{
            return "";
        }
    }

    @RequestMapping(value = "/getsmscode", method = RequestMethod.POST)
    @ResponseBody
    public String GetSmsCode(@RequestBody JSONObject params){
        String phone = params.getString("phone");
        if(phone != null && phone.trim().length() > 0) {
            JSONObject requestJson = new JSONObject();
            requestJson.put("phone", phone);
            return CommonSmsCode(smsGetCodeUrl, requestJson.toString());
        }else{
            return "";
        }
    }

    public String CommonSmsCode(String requestUrl, String requestParams){
        try{
            gson = new Gson();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type","application/json");
            HttpEntity<String> requests = new HttpEntity<String>(requestParams,headers);
            ResponseEntity<String> respose = restTemplate.postForEntity(requestUrl, requests,String.class);
            return respose.getBody();
        }catch(HttpClientErrorException ex){
            return "error";
        }catch (Exception e){
            return "error";
        }
    }
}

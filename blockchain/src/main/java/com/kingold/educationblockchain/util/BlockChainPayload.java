package com.kingold.educationblockchain.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingold.educationblockchain.controller.ChainCodeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;

public class BlockChainPayload {

    @Autowired
    private RestTemplateConfig mRestTemplateConfig = new RestTemplateConfig();
    /*
    获取返回结果
     */
    public JsonElement GetPayload(String functionName, String argJson, String  channelName)
    {
        RestTemplate restTemplate = mRestTemplateConfig.restTemplate();
        //HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //RestTemplate restTemplate = new RestTemplate(factory);
        //RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String requestStr = String.format("{\"channel\":\"%s\",\"chaincode\":\"%s\",\"method\":\"%s\",\"args\":[%s],\"chaincodeVer\":\"%s\"}",
                channelName,
                ChainCodeConfig.getProperty("chainCode.chainCodeName"),
                functionName,
                argJson,
                ChainCodeConfig.getProperty("chainCode.chainCodeVer"));
        headers.add("Content-Type", "application/json");
        headers.add("Connection", "keep-alive");
        headers.add("Authorization", ChainCodeConfig.getProperty("chainCode.authorizationKey"));

        HttpEntity<String> request1 = null;
        try{
            request1 = new HttpEntity<>(new String(requestStr.getBytes("UTF-8"),"ISO-8859-1"), headers);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ResponseEntity<String> response = restTemplate.postForEntity(ChainCodeConfig.getProperty("chainCode.hostUrl"), request1, String.class);
        String errMsg;
        if(response.getStatusCode()== HttpStatus.OK)
        {
            JsonParser parse= new JsonParser();
            JsonObject jsonObject;
            try{
                 jsonObject= (JsonObject) parse.parse(new String(response.getBody().getBytes("iso-8859-1"),"utf-8"));
            }catch(UnsupportedEncodingException e){
                throw new Error(e.getMessage());
            }

            if(jsonObject.has("returnCode")&&jsonObject.get("returnCode").getAsString().compareTo("Success")==0)
            {
                if(jsonObject.has("result")) {
                    String payload = jsonObject.getAsJsonObject("result").get("payload").getAsString();
                    return parse.parse(payload);
                }
                return jsonObject.get("returnCode");
            }
            errMsg = jsonObject.get("info").toString();
        }
        else {
            errMsg = response.getBody();
    }
        throw new Error(errMsg);
    }
}

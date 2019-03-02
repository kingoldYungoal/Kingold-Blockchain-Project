package com.kingold.educationblockchain.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kingold.educationblockchain.controller.ChainCodeConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.logging.LogException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

public class BlockChainPayload {

    private static Logger logger = Logger.getLogger(LogException.class.getName());
    private StringWriter stringWriter = new StringWriter();
    private PrintWriter printWriter = new PrintWriter(stringWriter);
    /*
    获取返回结果
     */
    public JsonElement GetPayload(String functionName, String argJson, String channelName) {
        String url = ChainCodeConfig.getProperty("chainCode.hostUrl");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type", "application/json; charset=UTF-8");
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Authorization", ChainCodeConfig.getProperty("chainCode.authorizationKey"));

        String requestStr = String.format("{\"channel\":\"%s\",\"chaincode\":\"%s\",\"method\":\"%s\",\"args\":[%s],\"chaincodeVer\":\"%s\"}",
                channelName,
                ChainCodeConfig.getProperty("chainCode.chainCodeName"),
                functionName,
                argJson,
                ChainCodeConfig.getProperty("chainCode.chainCodeVer"));

        StringEntity entity = new StringEntity(requestStr, "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        post.setEntity(entity);
        try {
            HttpResponse resp = httpClient.execute(post);
            if (resp.getStatusLine().getStatusCode() == 200) {
                JsonParser parse= new JsonParser();
                JsonObject jsonObject = new JsonObject();
                try{
                    String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
                    jsonObject= (JsonObject) parse.parse(content);
                }catch(UnsupportedEncodingException e){
                    e.printStackTrace(printWriter);
                    PrintExceptionLog(functionName, argJson, channelName, requestStr, e);
                }

                if(jsonObject.has("returnCode")&&jsonObject.get("returnCode").getAsString().compareTo("Success")==0)
                {
                    if(jsonObject.has("result")) {
                        String payload = jsonObject.getAsJsonObject("result").get("payload").getAsString();
                        return parse.parse(payload);
                    }
                    return jsonObject.get("returnCode");
                }
            }
        } catch (IOException e) {
            e.printStackTrace(printWriter);
            PrintExceptionLog(functionName, argJson, channelName, requestStr, e);
        } catch (Exception ex){
            ex.printStackTrace(printWriter);
            PrintExceptionLog(functionName, argJson, channelName, requestStr, ex);
            ex.getMessage();
        }
        return null;
    }

    private void PrintExceptionLog(String functionName, String argJson, String channelName, String requestStr, Exception ex){
        logger.info("=================================================");
        logger.warning("Error Message is " + ex.getMessage());
        logger.info("functionName is " + functionName);
        logger.info("argJson is " + argJson);
        logger.info("channelName is " + channelName);
        logger.info("requestStr is " + requestStr);
        logger.info("url is " + ChainCodeConfig.getProperty("chainCode.hostUrl"));
        logger.warning("StackTrace is " + stringWriter.toString());
        logger.info("=================================================");
    }
}

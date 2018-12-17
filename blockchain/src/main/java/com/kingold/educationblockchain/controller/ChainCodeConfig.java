package com.kingold.educationblockchain.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class ChainCodeConfig {
    private static Properties props ;

    public ChainCodeConfig(){

        try {
            Resource resource = new ClassPathResource("/application.properties");//
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getProperty(String key){

        return props == null ? null :  props.getProperty(key);

    }
}

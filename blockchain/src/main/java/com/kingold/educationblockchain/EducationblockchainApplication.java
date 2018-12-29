package com.kingold.educationblockchain;

import com.github.pagehelper.PageHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Properties;

//扫描两个包：controller 和 service
@SpringBootApplication(scanBasePackages = {"com.kingold.educationblockchain.controller","com.kingold.educationblockchain.service.impl"})
@MapperScan(basePackages = {"com.kingold.educationblockchain.dao"}) //要扫描的dao包
@EnableSwagger2
public class EducationblockchainApplication extends SpringBootServletInitializer {

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(EducationblockchainApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(EducationblockchainApplication.class, args);
    }

    //配置mybatis的分页插件pageHelper
    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","true");
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}

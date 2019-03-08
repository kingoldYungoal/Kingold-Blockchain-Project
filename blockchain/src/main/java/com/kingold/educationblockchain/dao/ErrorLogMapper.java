package com.kingold.educationblockchain.dao;

import com.kingold.educationblockchain.bean.ErrorLog;
import org.apache.ibatis.annotations.*;

public interface ErrorLogMapper {
    /**
     * 根据错误日志id查询错误信息
     */
    @Select("SELECT * FROM kg_errorlog where ID=#{id}")
    @Results({
            @Result(property = "kg_errorlogid", column = "ID"),
            @Result(property = "kg_errorlogmodule", column = "OPERATIONMODULE"),
            @Result(property = "kg_errorlogtime", column = "OPERATIONTIME"),
            @Result(property = "kg_errorlogurl", column = "URL"),
            @Result(property = "kg_errorlogmethod", column = "METHOD"),
            @Result(property = "kg_errorlogparameter", column = "PARAMETER"),
            @Result(property = "kg_errorlogresponse", column = "RESPONSE"),
            @Result(property = "kg_errorlogcatchmessage", column = "CATCHMESSAGE"),
            @Result(property = "kg_errorlogstacktracemessage", column = "STACKTRACEMESSAGE")
    })
    ErrorLog FindErrorLogById(String id);

    /**
     * 根据method查询错误信息
     */
    @Select("SELECT * FROM kg_errorlog where kg_errorlogmethod=#{methodName}")
    @Results({
            @Result(property = "kg_errorlogid", column = "ID"),
            @Result(property = "kg_errorlogmodule", column = "OPERATIONMODULE"),
            @Result(property = "kg_errorlogtime", column = "OPERATIONTIME"),
            @Result(property = "kg_errorlogurl", column = "URL"),
            @Result(property = "kg_errorlogmethod", column = "METHOD"),
            @Result(property = "kg_errorlogparameter", column = "PARAMETER"),
            @Result(property = "kg_errorlogresponse", column = "RESPONSE"),
            @Result(property = "kg_errorlogcatchmessage", column = "CATCHMESSAGE"),
            @Result(property = "kg_errorlogstacktracemessage", column = "STACKTRACEMESSAGE")
    })
    ErrorLog FindErrorlogByMethod(String methodName);

    /**
     * 错误日志数据新增
     */
    @Insert("insert into kg_errorlog(OPERATIONMODULE,OPERATIONTIME,URL,METHOD,PARAMETER,RESPONSE,CATCHMESSAGE,STACKTRACEMESSAGE) values (#{kg_errorlogmodule},to_date(#{kg_errorlogtime},'yyyy-mm-dd hh24:mi:ss'),#{kg_errorlogurl},#{kg_errorlogmethod},#{kg_errorlogparameter},#{kg_errorlogresponse},#{kg_errorlogcatchmessage},#{kg_errorlogstacktracemessage})")
    void AddErrorLog(ErrorLog errorlog);
}

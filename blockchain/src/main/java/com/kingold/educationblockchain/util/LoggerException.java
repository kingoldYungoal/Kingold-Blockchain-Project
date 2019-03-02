package com.kingold.educationblockchain.util;

import org.apache.ibatis.logging.LogException;

import java.util.logging.Logger;

public class LoggerException {
    private static Logger logger = Logger.getLogger(LogException.class.getName());

    public void PrintExceptionLog(String methodName,String className, String requestStr, Exception ex, String StackTraceException){
        logger.info("=================================================");
        logger.warning("Error Message is " + ex.getMessage());
        logger.info("methodName is " + methodName);
        logger.info("className is " + className);
        logger.info("requestStr is " + requestStr);
        logger.warning("StackTrace is " + StackTraceException);
        logger.info("=================================================");
    }
}

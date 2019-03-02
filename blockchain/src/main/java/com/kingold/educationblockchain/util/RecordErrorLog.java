package com.kingold.educationblockchain.util;

import com.kingold.educationblockchain.bean.ErrorLog;
import com.kingold.educationblockchain.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordErrorLog {
    @Autowired
    private ErrorLogService mErrorLogService;

    public void RecordError(Exception e, String parameter, String url, String response, String traceMessage) {
        ErrorLog errorlog = new ErrorLog();

        //跟踪模块
        String error_module = Thread.currentThread().getStackTrace()[2].getClassName();
        errorlog.setKg_errorlogmodule(error_module);

        //设置日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = df.format(new Date());
        errorlog.setKg_errorlogtime(time);

        //获取url
        errorlog.setKg_errorlogurl(url);

        //获取调用该方法的方法
        String error_method = Thread.currentThread().getStackTrace()[2].getMethodName();
        errorlog.setKg_errorlogmethod(error_method);

        //错误参数
        errorlog.setKg_errorlogparameter(parameter);

        //错误响应
        errorlog.setKg_errorlogresponse(response);

        //错误catchmessage
        errorlog.setKg_errorlogcatchmessage(e.getMessage());

        //堆栈消息
//        StackTraceElement[] trace = e.getStackTrace();
//        String message = null;
//        for(int i=0 ; i < trace.length/2 ; i++) {
//            message += trace[i]+"\n";
//        }
        errorlog.setKg_errorlogstacktracemessage(traceMessage);

        mErrorLogService.AddErrorLog(errorlog);
    }
}

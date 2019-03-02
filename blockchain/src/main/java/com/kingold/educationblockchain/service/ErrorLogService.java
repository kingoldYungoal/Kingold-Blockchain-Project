package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.ErrorLog;

public interface ErrorLogService {
    /**
     * 根据错误信息id查询错误信息
     */
    ErrorLog FindErrorLogById(String id);

    /**
     * 根据错误类型查询错误信息
     */
    ErrorLog FindErrorlogByMethod(String methodName);

    /**
     * 错误数据新增
     */
    boolean AddErrorLog(ErrorLog errorlog);
}

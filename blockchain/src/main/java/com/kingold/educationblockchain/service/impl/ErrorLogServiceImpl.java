package com.kingold.educationblockchain.service.impl;

import com.kingold.educationblockchain.bean.ErrorLog;
import com.kingold.educationblockchain.dao.ErrorLogMapper;
import com.kingold.educationblockchain.service.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ErrorLogServiceImpl implements ErrorLogService {
    @Autowired
    @Resource
    private ErrorLogMapper mErrorLogMapper;

    /**
     * 根据错误信息id查询错误信息
     */
    @Override
    public ErrorLog FindErrorLogById(String id){
        return mErrorLogMapper.FindErrorLogById(id);
    }

    /**
     * 根据错误类型查询错误信息
     */
    @Override
    public ErrorLog FindErrorlogByMethod(String methodName){
        return mErrorLogMapper.FindErrorlogByMethod(methodName);
    }

    /**
     * 错误数据新增
     */
    @Override
    public boolean AddErrorLog(ErrorLog errorlog){
        boolean flag = false;
        try{
            mErrorLogMapper.AddErrorLog(errorlog);
            flag = true;
        }catch(Exception e){
            System.out.println("错误信息新增失败!");
            e.printStackTrace();
        }
        return flag;
    }
}

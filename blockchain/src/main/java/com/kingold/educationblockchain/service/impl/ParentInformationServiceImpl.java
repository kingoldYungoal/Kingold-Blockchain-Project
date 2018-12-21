package com.kingold.educationblockchain.service.impl;

import com.kingold.educationblockchain.bean.ParentInformation;
import com.kingold.educationblockchain.dao.ParentInformationMapper;
import com.kingold.educationblockchain.service.ParentInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ParentInformationServiceImpl implements ParentInformationService {

    @Autowired
    @Resource
    private ParentInformationMapper mParentInformationMapper;

    /**
     * 根据家长信息id查询家长信息
     */
    @Override
    public ParentInformation FindParentInformationById(String id){
        return mParentInformationMapper.FindParentInformationById(id);
    }

    /**
     * 根据家长手机号查询家长信息
     */
    @Override
    public ParentInformation FindParentInformationByPhone(String phone){
        return mParentInformationMapper.FindParentInformationByPhone(phone);
    }

    /**
     * 家长数据新增
     */
    @Override
    public boolean AddParentInformation(ParentInformation parentInformation){
        boolean flag = false;
        try{
            mParentInformationMapper.AddParentInformation(parentInformation);
            flag = true;
        }catch(Exception e){
            System.out.println("家長數據新增失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 家长数据修改
     */
    @Override
    public boolean UpdateParentInformation(ParentInformation parentInformation){
        boolean flag = false;
        try{
            mParentInformationMapper.UpdateParentInformation(parentInformation);
            flag = true;
        }catch(Exception e){
            System.out.println("家長數據更新失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 家长数据删除
     */
    @Override
    public boolean DeleteParentInformation(String id){
        boolean flag = false;
        try{
            mParentInformationMapper.DeleteParentInformation(id);
            flag = true;
        }catch(Exception e){
            System.out.println("家長信息删除失败!");
            e.printStackTrace();
        }
        return flag;
    }
}

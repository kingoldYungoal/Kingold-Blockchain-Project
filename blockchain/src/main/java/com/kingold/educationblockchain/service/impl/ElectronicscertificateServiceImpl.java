package com.kingold.educationblockchain.service.impl;

import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.dao.ElectronicscertificateMapper;
import com.kingold.educationblockchain.service.ElectronicscertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ElectronicscertificateServiceImpl implements ElectronicscertificateService {

    @Autowired
    @Resource
    private ElectronicscertificateMapper mElectronicscertificateMapper;

    /**
     * 根據id查詢证书信息
     */
    @Override
    public Electronicscertificate GetCertificateById(int id)
    {
        return mElectronicscertificateMapper.GetCertificateById(id);
    }

    /**
     * 根據crmid查詢证书信息
     */
    @Override
    public List<Electronicscertificate> GetCertificatesByStudentId(int studentId){
        return mElectronicscertificateMapper.GetCertificatesByStudentId(studentId);
    }

    /**
     * 证书信息新增
     */
    @Override
    public boolean AddCertificate(Electronicscertificate electronicscertificate)
    {
        boolean flag = false;
        try{
            mElectronicscertificateMapper.AddCertificate(electronicscertificate);
            flag = true;
        }catch(Exception e){
            System.out.println("證書新增失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 证书信息更新
     */
    @Override
    public boolean UpdateCertificate(Electronicscertificate certificates)
    {
        boolean flag = false;
        try{
            mElectronicscertificateMapper.UpdateCertificate(certificates);
            flag = true;
        }catch(Exception e){
            System.out.println("证书信息更新失败!");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 证书信息刪除
     */
    @Override
    public boolean DeleteCertificate(int id)
    {
        boolean flag = false;
        try{
            mElectronicscertificateMapper.DeleteCertificate(id);
            flag = true;
        }catch(Exception e){
            System.out.println("证书信息刪除失败!");
            e.printStackTrace();
        }
        return flag;
    }
}


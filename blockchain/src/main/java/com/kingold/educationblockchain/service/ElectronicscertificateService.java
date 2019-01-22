package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.bean.paramBean.CertificateParam;

import java.util.List;

public interface ElectronicscertificateService {

    /**
     * 根據id查詢证书信息
     */
    Electronicscertificate GetCertificateById(String id);

    /**
     * 根據crmid查詢某个学生的所有证书信息
     */
    List<Electronicscertificate> GetCertificatesByStudentId(String studentId);

    /**
     * 根據crmid和certno查詢某个学生的某个证书信息
     */
    Electronicscertificate GetCertificateByStudentIdAndCertno(String certificateno, String studentId);

    /**
     * 根據多个参数查詢多个证书信息
     */
    List<Electronicscertificate> GetCertificatesByParam(CertificateParam param);

    /**
     * 新增电子证书数据
     */
    boolean AddCertificate(Electronicscertificate electronicscertificate);

    /**
     * 更新电子证书数据
     */
    boolean UpdateCertificate(Electronicscertificate certificates);

    /**
     * 刪除电子证书数据
     */
    boolean DeleteCertificate(String id);
}

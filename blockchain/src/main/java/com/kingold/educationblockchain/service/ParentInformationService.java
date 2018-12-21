package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.ParentInformation;

public interface ParentInformationService {

    /**
     * 根据家长信息id查询家长信息
     */
    ParentInformation FindParentInformationById(String id);

    /**
     * 根据家长手机号查询家长信息
     */
    ParentInformation FindParentInformationByPhone(String phone);

    /**
     * 家长数据新增
     */
    boolean AddParentInformation(ParentInformation parentInformation);

    /**
     * 家长数据修改
     */
    boolean UpdateParentInformation(ParentInformation parentInformation);

    /**
     * 家长数据删除
     */
    boolean DeleteParentInformation(String id);
}

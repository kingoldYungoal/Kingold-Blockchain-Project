package com.kingold.educationblockchain.dao;

import com.kingold.educationblockchain.bean.ParentInformation;
import org.apache.ibatis.annotations.*;

public interface ParentInformationMapper {

    /**
     * 根据家长信息id查询家长信息
     */
    @Select("SELECT * FROM kg_parentinformation where kg_parentinformationid=#{id} and kg_state = 0")
    @Results({
            @Result(property = "kg_parentinformationid", column = "KG_PARENTINFORMATIONID"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_phonenumber", column = "KG_PHONENUMBER"),
            @Result(property = "kg_parentrelationship", column = "KG_PARENTRELATIONSHIP"),
            @Result(property = "kg_countryname", column = "KG_COUNTRYNAME"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    ParentInformation FindParentInformationById(String id);

    /**
     * 根据家长手机号查询家长信息
     */
    @Select("SELECT * FROM kg_parentinformation where kg_phonenumber=#{phone} and kg_state = 0")
    @Results({
            @Result(property = "kg_parentinformationid", column = "KG_PARENTINFORMATIONID"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_phonenumber", column = "KG_PHONENUMBER"),
            @Result(property = "kg_parentrelationship", column = "KG_PARENTRELATIONSHIP"),
            @Result(property = "kg_countryname", column = "KG_COUNTRYNAME"),
            @Result(property = "kg_state", column = "KG_STATE")
    })
    ParentInformation FindParentInformationByPhone(String phone);

    /**
     * 家长数据新增
     */
    @Insert("insert into kg_parentinformation(kg_parentinformationid,kg_name,kg_phonenumber,kg_parentrelationship,kg_countryname,kg_state) values (#{kg_parentinformationid},#{kg_name},#{kg_phonenumber},#{kg_parentrelationship},#{kg_countryname},0)")
    void AddParentInformation(ParentInformation parentInformation);

    /**
     * 家长数据修改
     */
    @Update("update kg_parentinformation set kg_name=#{kg_name},kg_phonenumber=#{kg_phonenumber},kg_parentrelationship=#{kg_parentrelationship},kg_countryname=#{kg_countryname} where kg_parentinformationid=#{kg_parentinformationid} and kg_state=0")
    void UpdateParentInformation(ParentInformation parentInformation);

    /**
     * 家长数据删除
     */
    @Update("update kg_parentinformation set kg_state = 1 where kg_parentinformationid=#{id}")
    void DeleteParentInformation(String id);
}

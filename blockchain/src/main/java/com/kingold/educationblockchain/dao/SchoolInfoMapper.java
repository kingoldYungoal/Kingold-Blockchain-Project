package com.kingold.educationblockchain.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kingold.educationblockchain.bean.SchoolInfo;

/**
 * 
 * @author Bob Tang
 *
 */
public interface SchoolInfoMapper {
	@Select("select kg_schoolid,kg_name from kg_school where kg_state = 0")
	public List<SchoolInfo> getAll();
	
	@Select("select kg_schoolid,kg_name,kg_state from kg_school where kg_schoolid = #{id} and kg_state = 0")
	public SchoolInfo getById(String id);

	@Insert("insert into kg_school(kg_schoolid,kg_name,kg_state) values (#{kg_schoolid},#{kg_name},#{kg_state})")
	public void add(SchoolInfo schoolInfo);

	@Update("update kg_school set kg_schoolid=#{kg_schoolid},kg_name=#{kg_name},kg_state=#{kg_state}  where kg_schoolid=#{kg_schoolid}")
	public void update(SchoolInfo schoolInfo);

	@Update("update kg_school set kg_state=1 where kg_schoolid=#{id}")
	public void delete(String id);
}

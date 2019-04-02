package com.kingold.educationblockchain.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kingold.educationblockchain.bean.ClassInfo;

/**
 * 
 * @author Bob Tang
 *
 */
@Mapper
public interface ClassInfoMapper {

	@Select("select kg_classid,kg_name,kg_schoolid from kg_class where kg_schoolid=#{schoolId} and kg_state = 0 order by kg_name")
	public List<ClassInfo> getClassesBySchoolId(String schoolId);
	
	@Select("select kg_classid,kg_name,kg_schoolid from kg_class where kg_classid=#{id}")
	public ClassInfo getById(String id);

	@Insert("insert into kg_class(kg_classid,kg_name,kg_schoolid,kg_state) values (#{kg_classid},#{kg_name},#{kg_schoolid},#{kg_state})")
	public void add(ClassInfo classInfo);

	@Update("update kg_class set kg_name=#{kg_name},kg_schoolid=#{kg_schoolid},kg_state=#{kg_state} where kg_classid=#{kg_classid}")
	public void update(ClassInfo classInfo);

	@Update("update kg_class set kg_state=1 where kg_classid=#{id}")
	public void delete(String id);
}

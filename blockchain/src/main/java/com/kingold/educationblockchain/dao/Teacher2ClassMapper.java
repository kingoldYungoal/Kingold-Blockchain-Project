package com.kingold.educationblockchain.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kingold.educationblockchain.bean.ClassInfo;
import com.kingold.educationblockchain.bean.SchoolInfo;
import com.kingold.educationblockchain.bean.Teacher2Class;

@Mapper
public interface Teacher2ClassMapper {
	@Select("select kg_teacherinformationid,kg_classid,kg_state from kg_class_teacherinformation where kg_teacherinformationid=#{teacherId} and kg_state = 0")
	public List<Teacher2Class> queryByTeachId(String teacherId);
	
	@Select("select kg_teacherinformationid,kg_classid,kg_state from kg_class_teacherinformation where kg_teacherinformationid=#{teacherId} and kg_classid=#{classId}")
	public Teacher2Class getById(String teacherId, String classId);
	
	@Select("select b.* "
			+ "from kg_class_teacherinformation a "
			+ "inner join kg_class b on a.kg_classid=b.kg_classid "
			+ "where a.kg_teacherinformationid=#{teacherId} and a.kg_state = 0 and b.kg_state = 0")
	public List<ClassInfo> getClassInfoListByTeacherId(String teacherId);
	
	@Select("select distinct c.* "
			+ "from kg_class_teacherinformation a "
			+ "inner join kg_class b on a.kg_classid=b.kg_classid "
			+ "inner join kg_school c on b.kg_schoolid=c.kg_schoolid "
			+ "where a.kg_teacherinformationid=#{teacherId} and a.kg_state = 0 and b.kg_state = 0 and c.kg_state = 0")
	public List<SchoolInfo> getSchoolsByTeacherId(String teacherId);

	@Insert("insert into kg_class_teacherinformation(kg_teacherinformationid,kg_classid,kg_state) values (#{kg_teacherinformationid},#{kg_classid},#{kg_state})")
	public void add(Teacher2Class teacher2Class);

	@Update("update kg_class_teacherinformation set kg_teacherinformationid=#{kg_teacherinformationid},kg_classid=#{kg_classid},kg_state=#{kg_state} where kg_teacherinformationid=#{kg_teacherinformationid} and kg_classid=#{kg_classid}")
	public void update(Teacher2Class teacher2Class);

	@Update("update kg_class_teacherinformation set kg_state=1 where kg_teacherinformationid=#{teacherId} and kg_classid=#{classId}")
	public void delete(String teacherId, String classId);
	
	@Update("update kg_class_teacherinformation set kg_state=1 where kg_teacherinformationid=#{teacherId}")
	public void deleteByTeacherId(String teacherId);

}

package com.kingold.educationblockchain.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kingold.educationblockchain.bean.Student2Class;

public interface Student2ClassMapper {

	@Insert("insert into kg_studentprofile_class(kg_studentprofileid,kg_classid,kg_state) values (#{kg_studentprofileid},#{kg_classid},#{kg_state})")
	public void add(Student2Class student2Class);

	@Update("update kg_studentprofile_class set kg_studentprofileid=#{kg_studentprofileid},kg_classid=#{kg_classid},kg_state=#{kg_state} where kg_studentprofileid=#{kg_studentprofileid} and kg_classid=#{kg_classid}")
	public void update(Student2Class student2Class);

	@Update("update kg_studentprofile_class set kg_state=1 where kg_studentprofileid=#{studentId} and kg_classid=#{classId}")
	public void delete(String studentId, String classId);
	
	@Update("update kg_studentprofile_class set kg_state=1 where kg_studentprofileid=#{studentId}")
	public void deleteByStudentId(String studentId);
	
	@Select("select kg_studentprofileid,kg_classid,kg_state from kg_studentprofile_class where kg_studentprofileid=#{studentId} and kg_classid=#{classId}")
	public Student2Class getById(String studentId, String classId);

}

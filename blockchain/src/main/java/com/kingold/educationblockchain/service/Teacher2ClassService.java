package com.kingold.educationblockchain.service;

import java.util.List;

import com.kingold.educationblockchain.bean.ClassInfo;
import com.kingold.educationblockchain.bean.SchoolInfo;
import com.kingold.educationblockchain.bean.Teacher2Class;

public interface Teacher2ClassService {

	boolean add(Teacher2Class teacher2Class);

	boolean update(Teacher2Class teacher2Class);

	boolean delete(String teacherId, String classId);
	
	boolean deleteByTeacherId(String teacherId);

	Teacher2Class getById(String teacherId, String classId);
	
	List<SchoolInfo> getSchoolsByTeacherId(String teacherId);
	
	List<ClassInfo> getClassInfoListByTeacherId(String teacherId);

}
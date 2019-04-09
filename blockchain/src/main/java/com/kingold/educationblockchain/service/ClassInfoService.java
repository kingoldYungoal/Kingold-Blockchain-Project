package com.kingold.educationblockchain.service;

import java.util.List;

import com.kingold.educationblockchain.bean.ClassInfo;

public interface ClassInfoService {
	public boolean delete(String id);

	public boolean add(ClassInfo classInfo);

	public boolean update(ClassInfo classInfo);

	public List<ClassInfo> getClassesBySchoolId(String teacherinformationid, String schoolId);
	
	public ClassInfo getById(String id);
}

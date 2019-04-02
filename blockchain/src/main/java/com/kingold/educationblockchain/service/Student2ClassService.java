package com.kingold.educationblockchain.service;

import com.kingold.educationblockchain.bean.Student2Class;

public interface Student2ClassService {

	boolean add(Student2Class student2Class);

	boolean update(Student2Class student2Class);

	boolean delete(String studentId, String classId);

	Student2Class getById(String studentId, String classId);
	
	boolean deleteByStudentId(String studentId);

}
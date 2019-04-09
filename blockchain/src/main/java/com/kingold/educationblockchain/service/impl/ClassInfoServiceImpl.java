package com.kingold.educationblockchain.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kingold.educationblockchain.bean.ClassInfo;
import com.kingold.educationblockchain.dao.ClassInfoMapper;
import com.kingold.educationblockchain.service.ClassInfoService;

@Service
public class ClassInfoServiceImpl implements ClassInfoService {

	@Autowired
	@Resource
	private ClassInfoMapper classInfoMapper;

	@Override
	public boolean delete(String id) {
		try {
			classInfoMapper.delete(id);
			return true;
		} catch (Exception e) {
			System.out.println("班级信息刪除失败!");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean add(ClassInfo classInfo) {
		try {
			classInfoMapper.add(classInfo);
			return true;
		} catch (Exception e) {
			System.out.println("班级信息新增失败!");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean update(ClassInfo classInfo) {
		try {
			classInfoMapper.update(classInfo);
			return true;
		} catch (Exception e) {
			System.out.println("班级信息更新失败!");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public
	List<ClassInfo> getClassesBySchoolId(String teacherinformationid, String schoolId){
		return classInfoMapper.getClassesBySchoolId(teacherinformationid, schoolId);
	}

	@Override
	public ClassInfo getById(String id) {
		// TODO Auto-generated method stub
		return classInfoMapper.getById(id);
	}

}

package com.kingold.educationblockchain.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kingold.educationblockchain.bean.ClassInfo;
import com.kingold.educationblockchain.bean.SchoolInfo;
import com.kingold.educationblockchain.bean.Teacher2Class;
import com.kingold.educationblockchain.dao.Teacher2ClassMapper;
import com.kingold.educationblockchain.service.Teacher2ClassService;

@Service
public class Teacher2ClassServiceImpl implements Teacher2ClassService {
	
	@Autowired
	@Resource
	private Teacher2ClassMapper teacher2ClassMapper;
	
	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Teacher2ClassService#add(com.kingold.educationblockchain.bean.Teacher2Class)
	 */
	@Override
	public boolean add(Teacher2Class teacher2Class) {
		try {
			teacher2ClassMapper.add(teacher2Class);
			return true;
		}catch (Exception e) {
			System.out.println("新增老师班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Teacher2ClassService#update(com.kingold.educationblockchain.bean.Teacher2Class)
	 */
	@Override
	public boolean update(Teacher2Class teacher2Class) {
		try {
			teacher2ClassMapper.update(teacher2Class);
			return true;
		}catch (Exception e) {
			System.out.println("更新老师班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Teacher2ClassService#delete(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean delete(String teacherId, String classId) {
		try {
			teacher2ClassMapper.delete(teacherId, classId);
			return true;
		}catch (Exception e) {
			System.out.println("删除老师班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Teacher2ClassService#getById(java.lang.String, java.lang.String)
	 */
	@Override
	public Teacher2Class getById(String teacherId, String classId) {
		return teacher2ClassMapper.getById(teacherId, classId);
	}

	@Override
	public boolean deleteByTeacherId(String teacherId) {
		try {
			teacher2ClassMapper.deleteByTeacherId(teacherId);
			return true;
		}catch (Exception e) {
			System.out.println("删除老师班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<SchoolInfo> getSchoolsByTeacherId(String teacherId) {
		// TODO Auto-generated method stub
		return teacher2ClassMapper.getSchoolsByTeacherId(teacherId);
	}

	@Override
	public List<ClassInfo> getClassInfoListByTeacherId(String teacherId) {
		// TODO Auto-generated method stub
		return teacher2ClassMapper.getClassInfoListByTeacherId(teacherId);
	}
}

package com.kingold.educationblockchain.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kingold.educationblockchain.bean.Student2Class;
import com.kingold.educationblockchain.dao.Student2ClassMapper;
import com.kingold.educationblockchain.service.Student2ClassService;

@Service
public class Student2ClassServiceImpl implements Student2ClassService {

	@Autowired
	@Resource
	private Student2ClassMapper student2ClassMapper;

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Student2ClassService#add(com.kingold.educationblockchain.bean.Student2Class)
	 */
	@Override
	public boolean add(Student2Class student2Class) {
		try {
			student2ClassMapper.add(student2Class);
			return true;
		} catch (Exception e) {
			System.out.println("新增学生班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Student2ClassService#update(com.kingold.educationblockchain.bean.Student2Class)
	 */
	@Override
	public boolean update(Student2Class student2Class) {
		try {
			student2ClassMapper.update(student2Class);
			return true;
		} catch (Exception e) {
			System.out.println("更新学生班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Student2ClassService#delete(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean delete(String studentId, String classId) {
		try {
			student2ClassMapper.delete(studentId, classId);
			return true;
		} catch (Exception e) {
			System.out.println("删除学生班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.Student2ClassService#getById(java.lang.String, java.lang.String)
	 */
	@Override
	public Student2Class getById(String studentId, String classId) {
		return student2ClassMapper.getById(studentId, classId);
	}

	@Override
	public boolean deleteByStudentId(String studentId) {
		try {
			student2ClassMapper.deleteByStudentId(studentId);
			return true;
		} catch (Exception e) {
			System.out.println("删除学生班级关联信息失败");
			e.printStackTrace();
			return false;
		}
	}
}

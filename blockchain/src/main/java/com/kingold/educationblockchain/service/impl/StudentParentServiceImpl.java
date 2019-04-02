package com.kingold.educationblockchain.service.impl;

import com.kingold.educationblockchain.bean.StudentParent;
import com.kingold.educationblockchain.dao.StudentParentMapper;
import com.kingold.educationblockchain.service.StudentParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class StudentParentServiceImpl implements StudentParentService {

	@Autowired
	@Resource
	private StudentParentMapper mStudentParentMapper;

	/**
	 * 根据家長信息id查询
	 */
	@Override
	public List<StudentParent> FindStudentParentByParentId(String parentId) {
		return mStudentParentMapper.FindStudentParentByParentId(parentId);
	}

	/**
	 * 根据学生id查询
	 */
	@Override
	public List<StudentParent> FindStudentParentByStudentId(String studentId) {
		return mStudentParentMapper.FindStudentParentByStudentId(studentId);
	}

	/**
	 * 根据学生id,家長信息id查询
	 */
	@Override
	public StudentParent FindStudentParent(String parentId, String studentId) {
		return mStudentParentMapper.FindStudentParent(parentId, studentId);
	}

	/**
	 * 學生家長關係新增
	 */
	@Override
	public boolean AddStudentParent(StudentParent studentParent) {
		boolean flag = false;
		try {
			mStudentParentMapper.AddStudentParent(studentParent);
			flag = true;
		} catch (Exception e) {
			System.out.println("學生家長關係新增失败!");
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 學生家長關係刪除
	 */
	@Override
	public boolean DeleteStudentParent(String parentId, String studentId) {
		boolean flag = false;
		try {
			mStudentParentMapper.DeleteStudentParent(parentId, studentId);
			flag = true;
		} catch (Exception e) {
			System.out.println("學生家長關係刪除失败!");
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public boolean UpdateStudentParent(StudentParent studentParent) {
		boolean flag = false;
		try {
			mStudentParentMapper.UpdateStudentParent(studentParent);
			flag = true;
		} catch (Exception e) {
			System.out.println("學生家長關係更新失败!");
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public boolean deleteByStudentId(String studentId) {
		boolean flag = false;
		try {
			mStudentParentMapper.deleteByStudentId(studentId);
			flag = true;
		} catch (Exception e) {
			System.out.println("學生家長關係刪除失败!");
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public boolean deleteByParentId(String parentId) {
		boolean flag = false;
		try {
			mStudentParentMapper.deleteByParentId(parentId);
			flag = true;
		} catch (Exception e) {
			System.out.println("學生家長關係刪除失败!");
			e.printStackTrace();
		}
		return flag;
	}
}

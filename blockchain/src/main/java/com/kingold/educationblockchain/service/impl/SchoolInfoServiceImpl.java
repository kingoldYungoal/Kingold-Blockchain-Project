package com.kingold.educationblockchain.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kingold.educationblockchain.bean.SchoolInfo;
import com.kingold.educationblockchain.dao.SchoolInfoMapper;
import com.kingold.educationblockchain.service.SchoolInfoService;

@Service
public class SchoolInfoServiceImpl implements SchoolInfoService {

	@Autowired
	@Resource
	private SchoolInfoMapper schoolInfoMapper;

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.SchoolInfoService#add(com.kingold.educationblockchain.bean.SchoolInfo)
	 */
	@Override
	public boolean add(SchoolInfo schoolInfo) {
		try {
			schoolInfoMapper.add(schoolInfo);
			return true;
		} catch (Exception e) {
			System.out.println("学校信息新增失败");
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.SchoolInfoService#update(com.kingold.educationblockchain.bean.SchoolInfo)
	 */
	@Override
	public boolean update(SchoolInfo schoolInfo) {
		try {
			schoolInfoMapper.update(schoolInfo);
			return true;
		} catch (Exception e) {
			System.out.println("学校信息更新失败");
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.SchoolInfoService#delete(java.lang.String)
	 */
	@Override
	public boolean delete(String id) {
		try {
			schoolInfoMapper.delete(id);
			return true;
		} catch (Exception e) {
			System.out.println("学校信息删除失败");
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.SchoolInfoService#getById(java.lang.String)
	 */
	@Override
	public SchoolInfo getById(String id) {
		return schoolInfoMapper.getById(id);
	}

	/* (non-Javadoc)
	 * @see com.kingold.educationblockchain.service.impl.SchoolInfoService#getAll()
	 */
	@Override
	public List<SchoolInfo> getAll() {
		return schoolInfoMapper.getAll();
	}

}

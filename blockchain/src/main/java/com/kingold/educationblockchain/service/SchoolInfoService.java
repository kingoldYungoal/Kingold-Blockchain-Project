package com.kingold.educationblockchain.service;

import java.util.List;

import com.kingold.educationblockchain.bean.SchoolInfo;

public interface SchoolInfoService {

	boolean add(SchoolInfo schoolInfo);

	boolean update(SchoolInfo schoolInfo);

	boolean delete(String id);

	SchoolInfo getById(String id);

	List<SchoolInfo> getAll();

}
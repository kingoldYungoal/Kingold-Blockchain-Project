package com.kingold.educationblockchain.dao;

import com.kingold.educationblockchain.bean.CertificationType;
import com.kingold.educationblockchain.bean.Electronicscertificate;
import com.kingold.educationblockchain.bean.paramBean.CertificateParam;
import com.kingold.educationblockchain.util.SqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ElectronicscertificateMapper {

	/**
	 * 根據id查詢证书信息
	 */
	@Select("select "
			+ "a.kg_electronicscertificateid,"
			+ "a.kg_certificateno,"
			+ "a.kg_studentprofileid,"
			+ "a.kg_studentname,"
			+ "a.kg_sex,"
			+ "to_char(a.kg_certificatedate,'yyyy-mm-dd') kg_certificatedate,"
			+ "b.kg_name kg_classname,"
			+ "a.kg_certitype,"
			+ "c.kg_name kg_schoolname,"
			+ "a.kg_teachername,"
			+ "to_char(a.kg_starttime,'yyyy-mm-dd') kg_starttime,"
			+ "to_char(a.kg_endtime,'yyyy-mm-dd') kg_endtime,"
			+ "a.kg_classstype,"
			+ "a.kg_explain,"
			+ "a.kg_name,"
			+ "a.kg_state "
			+ "from KG_ELECTRONICSCERTIFICATE a "
			+ "left join kg_class b on a.kg_classid = b.kg_classid "
			+ "left join kg_school c on a.kg_schoolid = c.kg_schoolid "
			+ "where a.kg_electronicscertificateid = #{id} and a.kg_state = 0 and b.kg_state = 0  and c.kg_state = 0")
	Electronicscertificate GetCertificateById(String id);

	/**
	 * 根據crmid查詢某个学生的所有证书信息
	 */
	@Select("select "
			+ "a.kg_electronicscertificateid,"
			+ "a.kg_certificateno,"
			+ "a.kg_studentprofileid,"
			+ "a.kg_studentname,"
			+ "a.kg_sex,"
			+ "to_char(a.kg_certificatedate,'yyyy-mm-dd') kg_certificatedate,"
			+ "b.kg_name kg_classname,"
			+ "a.kg_certitype,"
			+ "c.kg_name kg_schoolname,"
			+ "a.kg_teachername,"
			+ "to_char(a.kg_starttime,'yyyy-mm-dd') kg_starttime,"
			+ "to_char(a.kg_endtime,'yyyy-mm-dd') kg_endtime,"
			+ "a.kg_classstype,"
			+ "a.kg_explain,"
			+ "a.kg_name,"
			+ "a.kg_state "
			+ "from KG_ELECTRONICSCERTIFICATE a "
			+ "left join kg_class b on a.kg_classid = b.kg_classid "
			+ "left join kg_school c on a.kg_schoolid = c.kg_schoolid "
			+ "where a.kg_studentprofileid=#{studentId} and a.kg_state = 0 and b.kg_state = 0  and c.kg_state = 0")
	List<Electronicscertificate> GetCertificatesByStudentId(String studentId);

	/**
	 * 根據crmid和certno查詢某个学生的某个证书信息
	 */
	@Select("select "
			+ "a.kg_electronicscertificateid,"
			+ "a.kg_certificateno,"
			+ "a.kg_studentprofileid,"
			+ "a.kg_studentname,"
			+ "a.kg_sex,"
			+ "to_char(a.kg_certificatedate,'yyyy-mm-dd') kg_certificatedate,"
			+ "b.kg_name kg_classname,"
			+ "a.kg_certitype,"
			+ "c.kg_name kg_schoolname,"
			+ "a.kg_teachername,"
			+ "to_char(a.kg_starttime,'yyyy-mm-dd') kg_starttime,"
			+ "to_char(a.kg_endtime,'yyyy-mm-dd') kg_endtime,"
			+ "a.kg_classstype,"
			+ "a.kg_explain,"
			+ "a.kg_name,"
			+ "a.kg_state "
			+ "from KG_ELECTRONICSCERTIFICATE a "
			+ "left join kg_class b on a.kg_classid = b.kg_classid "
			+ "left join kg_school c on a.kg_schoolid = c.kg_schoolid "
			+ "where a.kg_studentprofileid=#{studentId} and a.KG_CERTIFICATENO=#{certificateno} and a.kg_state = 0 and b.kg_state = 0  and c.kg_state = 0")
	Electronicscertificate GetCertificateByStudentIdAndCertno(String certificateno, String studentId);

	/**
	 * 根據多个参数查詢多个证书信息
	 */
	@SelectProvider(type = SqlProvider.class, method = "QueryCertByParam")
	List<Electronicscertificate> GetCertificatesByParam(CertificateParam param);

	/**
	 * 新增电子证书数据
	 */
	@Insert("insert into KG_ELECTRONICSCERTIFICATE(kg_electronicscertificateid,kg_certificateno,kg_studentprofileid,kg_studentname,kg_sex,kg_certificatedate,kg_classid,kg_certitype,kg_schoolid,kg_teachername,kg_starttime,kg_endtime,kg_classstype,kg_explain,kg_name,kg_state,kg_teacherid) values (#{kg_electronicscertificateid},#{kg_certificateno},#{kg_studentprofileid},#{kg_studentname},#{kg_sex},to_date(#{kg_certificatedate},'yyyy-mm-dd'),#{kg_classid},#{kg_certitype},#{kg_schoolid},#{kg_teachername},to_date(#{kg_starttime},'yyyy-mm-dd'),to_date(#{kg_endtime},'yyyy-mm-dd'),#{kg_classstype},#{kg_explain},#{kg_name},#{kg_state},#{kg_teacherid})")
	void AddCertificate(Electronicscertificate electronicscertificate);

	/**
	 * 更新电子证书数据
	 */
	@Update("update KG_ELECTRONICSCERTIFICATE set kg_electronicscertificateid=#{kg_electronicscertificateid},kg_studentname=#{kg_studentname},kg_sex=#{kg_sex},kg_certificatedate=to_date(#{kg_certificatedate},'yyyy-mm-dd'),kg_classid=#{kg_classid},kg_certitype=#{kg_certitype},kg_schoolid=#{kg_schoolid},kg_teachername=#{kg_teachername},kg_starttime=to_date(#{kg_starttime},'yyyy-mm-dd'),kg_endtime=to_date(#{kg_endtime},'yyyy-mm-dd'),kg_classstype=#{kg_classstype},kg_explain=#{kg_explain},kg_name=#{kg_name},kg_teacherid=#{kg_teacherid} where kg_certificateno=#{kg_certificateno} and kg_studentprofileid=#{kg_studentprofileid} and kg_state=0)")
	void UpdateCertificate(Electronicscertificate certificates);

	/**
	 * 刪除电子证书数据
	 */
	@Update("update KG_ELECTRONICSCERTIFICATE set kg_state=1 where kg_electronicscertificateid=#{id}")
	void DeleteCertificate(String id);

	@Select("SELECT "
			+ "kg_certitype certificationType, "
			+ "count(0) as count "
			+ "FROM kg_electronicscertificate "
			+ "where kg_classid=#{classId} and kg_state='0' "
			+ "group by kg_certitype") // and  (kg_certificatedate between ADD_MONTHS(sysdate, -2) and sysdate)
	List<CertificationType> getCertificationTypeByClassId(String classId);

	@Select("select distinct a.kg_electronicscertificateid from KG_ELECTRONICSCERTIFICATE a where a.kg_certitype=#{certiType} and a.kg_classid=#{classId} and a.kg_state = 0") //and (a.kg_certificatedate between ADD_MONTHS(sysdate, -2) and sysdate)
	List<String> GetCertificateByCertiTypeAndClassId(String certiType, String classId);
}

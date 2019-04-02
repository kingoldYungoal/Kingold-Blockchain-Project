package com.kingold.educationblockchain.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kingold.educationblockchain.bean.StudentInfo;
import com.kingold.educationblockchain.bean.StudentProfile;

public interface StudentProfileMapper {
	
	/**
	   *      根据班级Id查询学生
	 * @param classId
	 * @return
	 */
	@Select("select "
	        + "a.kg_studentprofileid kg_studentprofileid,"
	        + "b.kg_name kg_classname,"
	        + "c.kg_name kg_schoolname,"
	        + "a.kg_educationnumber kg_educationnumber,"
	        + "a.kg_studentnumber kg_studentnumber,"
	        + "a.kg_name kg_name,"
	        + "a.kg_fullname kg_fullname,"
	        + "a.kg_sex kg_sex,"
	        + "a.kg_age kg_age,"
	        + "a.kg_countryname kg_countryname,"
	        + "to_char(a.kg_birthday,'yyyy-mm-dd') kg_birthday,"
	        + "a.kg_registeredresidence kg_registeredresidence,"
	        + "a.kg_passportnumberoridnumber kg_passportnumberoridnumber,"
	        + "a.kg_entrancestate kg_entrancestate,"
	        + "to_char(a.kg_jointime,'yyyy-mm-dd') kg_jointime,"
	        + "d.kg_parentname,"
	        + "d.kg_parentphonenumber "
	        + "from KG_STUDENTPROFILE a "
	        + "left join kg_class b  on a.kg_classid = b.kg_classid "
	        + "left join kg_school c on a.kg_schoolid = c.kg_schoolid "
	        + "left join(select "
	        + "e.kg_studentprofileid,"
	        + "listagg(kg_parentrelationship||':'||f.kg_name,';') WITHIN GROUP (ORDER BY e.kg_studentprofileid) as kg_parentname, "
	        + "listagg(f.kg_phonenumber,';') WITHIN GROUP (ORDER BY e.kg_studentprofileid) as kg_parentphonenumber "
	        + "from kg_student_parent e "
	        + "inner join kg_parentinformation f on e.kg_parentinformationid = f.kg_parentinformationid "
	        + "where e.kg_state = 0 and f.kg_state = 0 group by e.kg_studentprofileid"
	        + ") d on a.kg_studentprofileid = d.kg_studentprofileid "
	        + "where a.kg_classid=#{classId} and a.kg_state = 0 and b.kg_state = 0 and c.kg_state = 0")
   public List<StudentInfo> queryStudentsByClassId(String classId);

    /**
     * 根據id查詢學生信息
     */
	@Select("select "
	        + "a.kg_studentprofileid kg_studentprofileid,"
	        + "b.kg_name kg_classname,"
	        + "c.kg_name kg_schoolname,"
	        + "a.kg_educationnumber kg_educationnumber,"
	        + "a.kg_studentnumber kg_studentnumber,"
	        + "a.kg_name kg_name,"
	        + "a.kg_fullname kg_fullname,"
	        + "a.kg_sex kg_sex,"
	        + "a.kg_age kg_age,"
	        + "a.kg_countryname kg_countryname,"
	        + "to_char(a.kg_birthday,'yyyy-mm-dd') kg_birthday,"
	        + "a.kg_registeredresidence kg_registeredresidence,"
	        + "a.kg_passportnumberoridnumber kg_passportnumberoridnumber,"
	        + "a.kg_entrancestate kg_entrancestate,"
	        + "to_char(a.kg_jointime,'yyyy-mm-dd') kg_jointime "
	        + "from KG_STUDENTPROFILE a "
	        + "left join kg_class b on a.kg_classid = b.kg_classid "
	        + "left join kg_school c on a.kg_schoolid = c.kg_schoolid "
	        + "where a.kg_studentprofileid = #{id} and a.kg_state = 0 and b.kg_state = 0 and c.kg_state = 0")
      @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_classname", column = "KG_CLASSNAME"),
            @Result(property = "kg_schoolname", column = "KG_SCHOOLNAME"),
            @Result(property = "kg_educationnumber", column = "KG_EDUCATIONNUMBER"),
            @Result(property = "kg_studentnumber", column = "KG_STUDENTNUMBER"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_fullname", column = "KG_FULLNAME"),
            @Result(property = "kg_sex", column = "KG_SEX"),
            @Result(property = "kg_age", column = "KG_AGE"),
            @Result(property = "kg_countryname", column = "KG_COUNTRYNAME"),
            @Result(property = "kg_birthday", column = "KG_BIRTHDAY"),
            @Result(property = "kg_registeredresidence", column = "KG_REGISTEREDRESIDENCE"),
            @Result(property = "kg_passportnumberoridnumber", column = "KG_PASSPORTNUMBERORIDNUMBER"),
            @Result(property = "kg_entrancestate", column = "KG_ENTRANCESTATE"),
            @Result(property = "kg_state", column = "KG_STATE"),
            @Result(property = "kg_jointime", column = "KG_JOINTIME")
    })
    StudentProfile GetStudentProfileById(String id);

    /**
     * 根據学号或者学籍号查詢學生信息
     */
    @Select("select "
	        + "a.kg_studentprofileid kg_studentprofileid,"
	        + "b.kg_name kg_classname,"
	        + "c.kg_name kg_schoolname,"
	        + "a.kg_educationnumber kg_educationnumber,"
	        + "a.kg_studentnumber kg_studentnumber,"
	        + "a.kg_name kg_name,"
	        + "a.kg_fullname kg_fullname,"
	        + "a.kg_sex kg_sex,"
	        + "a.kg_age kg_age,"
	        + "a.kg_countryname kg_countryname,"
	        + "to_char(a.kg_birthday,'yyyy-mm-dd') kg_birthday,"
	        + "a.kg_registeredresidence kg_registeredresidence,"
	        + "a.kg_passportnumberoridnumber kg_passportnumberoridnumber,"
	        + "a.kg_entrancestate kg_entrancestate,"
	        + "to_char(a.kg_jointime,'yyyy-mm-dd') kg_jointime "
	        + "from KG_STUDENTPROFILE a "
	        + "left join kg_class b on a.kg_classid = b.kg_classid "
	        + "left join kg_school c on a.kg_schoolid = c.kg_schoolid "
	        + "where (a.kg_educationnumber = #{eduNumber} or a.kg_studentnumber = #{stuNumber}) and a.kg_state = 0 and b.kg_state = 0 and c.kg_state = 0")
      @Results({
            @Result(property = "kg_studentprofileid", column = "KG_STUDENTPROFILEID"),
            @Result(property = "kg_classname", column = "KG_CLASSNAME"),
            @Result(property = "kg_schoolname", column = "KG_SCHOOLNAME"),
            @Result(property = "kg_educationnumber", column = "KG_EDUCATIONNUMBER"),
            @Result(property = "kg_studentnumber", column = "KG_STUDENTNUMBER"),
            @Result(property = "kg_name", column = "KG_NAME"),
            @Result(property = "kg_fullname", column = "KG_FULLNAME"),
            @Result(property = "kg_sex", column = "KG_SEX"),
            @Result(property = "kg_age", column = "KG_AGE"),
            @Result(property = "kg_countryname", column = "KG_COUNTRYNAME"),
            @Result(property = "kg_birthday", column = "KG_BIRTHDAY"),
            @Result(property = "kg_registeredresidence", column = "KG_REGISTEREDRESIDENCE"),
            @Result(property = "kg_passportnumberoridnumber", column = "KG_PASSPORTNUMBERORIDNUMBER"),
            @Result(property = "kg_entrancestate", column = "KG_ENTRANCESTATE"),
            @Result(property = "kg_state", column = "KG_STATE"),
            @Result(property = "kg_jointime", column = "KG_JOINTIME")
    })
    List<StudentProfile> GetStudentProfileByNumber(String eduNumber, String stuNumber);
    
    
    /**
                 * 根据老师和班级查询学生
     * @param teacherId
     * @param classId
     * @return
     */
	@Select("select "
	        + "a.kg_studentprofileid kg_studentprofileid,"
	        + "b.kg_name kg_classname,"
	        + "c.kg_name kg_schoolname,"
	        + "a.kg_educationnumber kg_educationnumber,"
	        + "a.kg_studentnumber kg_studentnumber,"
	        + "a.kg_name kg_name,"
	        + "a.kg_fullname kg_fullname,"
	        + "a.kg_sex kg_sex,"
	        + "a.kg_age kg_age,"
	        + "a.kg_countryname kg_countryname,"
	        + "to_char(a.kg_birthday,'yyyy-mm-dd') kg_birthday,"
	        + "a.kg_registeredresidence kg_registeredresidence,"
	        + "a.kg_passportnumberoridnumber kg_passportnumberoridnumber,"
	        + "a.kg_entrancestate kg_entrancestate,"
	        + "to_char(a.kg_jointime,'yyyy-mm-dd') kg_jointime "
	        + "from KG_STUDENTPROFILE a "
	        + "inner join kg_class b  on a.kg_classid = b.kg_classid "
	        + "inner join kg_class_teacherinformation c on b.kg_classid = c.kg_classid "
	        + "inner join kg_school d on a.kg_schoolid = d.kg_schoolid "
	        + "where a.kg_classid=#{classId} and c.kg_teacherinformationid=#{teacherId} and a.kg_state = 0 and b.kg_state = 0 and c.kg_state = 0 and d.kg_state = 0")
      List<StudentProfile> GetStudentsByClassAndTeacher(String teacherId, String classId);

    /**
     * 根據多个参数查詢多个学生信息
     */
    @Select("<script>"
            + "SELECT * from kg_studentprofile"
            + " Where kg_studentprofileid in ("
            + "select distinct kg_studentprofileid from KG_ELECTRONICSCERTIFICATE where 1=1"
            + "<if test='teacherId!=\"\" and teacherId != null'>"
            + " and kg_teacherid=#{teacherId}"
            + "</if>"
            + "<if test='classid!=\"\" and classid != null'>"
            + " and kg_classid=#{classid}"
            + "</if>"
            + "<if test='year>0'>"
            + " and to_number(to_char(kg_certificatedate,'yyyy'))=#{year}"
            + "</if>"
            + " and kg_state=0) and kg_state=0"
            + "</script>")
    List<StudentProfile> GetStudentsByParam(String teacherId, String classid, int year);

    /**
     * 新增學生信息
     */
    @Insert("insert into KG_STUDENTPROFILE(kg_studentprofileid, kg_classid,kg_schoolid,kg_educationnumber,kg_studentnumber,kg_name,kg_fullname,kg_sex,kg_age,kg_countryname,kg_birthday,kg_registeredresidence,kg_passportnumberoridnumber,kg_entrancestate,kg_jointime) values (#{kg_studentprofileid},#{kg_classid},#{kg_schoolid},#{kg_educationnumber},#{kg_studentnumber},#{kg_name},#{kg_fullname},#{kg_sex},#{kg_age},#{kg_countryname},to_date(#{kg_birthday},'yyyy-mm-dd'),#{kg_registeredresidence},#{kg_passportnumberoridnumber},#{kg_entrancestate},to_date(#{kg_jointime},'yyyy-mm-dd'))")
    void AddStudentProfile(StudentProfile studentProfile);

    /**
     * 更新學生信息
     */
    @Update("update KG_STUDENTPROFILE set KG_classid=#{kg_classid},KG_schoolid=#{kg_schoolid},KG_EDUCATIONNUMBER=#{kg_educationnumber},KG_STUDENTNUMBER=#{kg_studentnumber},KG_NAME=#{kg_name},KG_FULLNAME=#{kg_fullname},KG_SEX=#{kg_sex},KG_AGE=#{kg_age},KG_COUNTRYNAME=#{kg_countryname},KG_BIRTHDAY=to_date(#{kg_birthday},'yyyy-mm-dd'),KG_REGISTEREDRESIDENCE=#{kg_registeredresidence},KG_PASSPORTNUMBERORIDNUMBER=#{kg_passportnumberoridnumber},KG_ENTRANCESTATE=#{kg_entrancestate},KG_JOINTIME=to_date(#{kg_jointime},'yyyy-mm-dd') where kg_studentprofileid=#{kg_studentprofileid} and kg_state=0")
    void UpdateStudentProfile(StudentProfile studentProfile);

    /**
     * 刪除學生信息
     */
    @Update("update KG_STUDENTPROFILE set kg_state=1 where kg_studentprofileid=#{id}")
    void DeleteStudentProfile(String id);

    /**
     * 刪除學生信息
     */
    @Delete("delete from KG_STUDENTPROFILE where kg_studentprofileid=#{id}")
    void RelDeleteStudentProfile(String id);
}

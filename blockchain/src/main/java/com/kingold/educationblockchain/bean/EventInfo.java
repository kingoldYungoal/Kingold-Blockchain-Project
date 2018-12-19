package com.kingold.educationblockchain.bean;

import com.google.gson.annotations.SerializedName;

public class EventInfo {
    @SerializedName("evt_id")
    private String  EventId;
    @SerializedName("stu_id")
    private String StudentId;
    @SerializedName("evt_content")
    private String EventContent;
    @SerializedName("evt_date")
    private String EventDate;
    @SerializedName("evt_org")
    private String EventOrg;

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getEventContent() {
        return EventContent;
    }

    public void setEventContent(String eventContent) {
        EventContent = eventContent;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    public String getEventOrg() {
        return EventOrg;
    }

    public void setEventOrg(String eventOrg) {
        EventOrg = eventOrg;
    }

    public String getEventOperationTime() {
        return EventOperationTime;
    }

    public void setEventOperationTime(String eventOperationTime) {
        EventOperationTime = eventOperationTime;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    @SerializedName("evt_operation_time")
    private String EventOperationTime;
    @SerializedName("remark")
    private String Remark;
}

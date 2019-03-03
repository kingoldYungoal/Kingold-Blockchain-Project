package com.kingold.educationblockchain.bean;

import java.util.Date;

public class DisplayInfo implements Comparable<DisplayInfo>{
    public Date InfoDate;

    public CertInfo DisplayCertInfo=null;

    public EventInfo  DisplayEventInfo=null;

    public int compareTo(DisplayInfo arg0) {
        return -this.InfoDate.compareTo(arg0.InfoDate);
    }

    public Date getInfoDate() {
        return InfoDate;
    }

    public void setInfoDate(Date date) {
        InfoDate = date;
    }
    public CertInfo getDisplayCertInfo() {
        return DisplayCertInfo;
    }

    public void setDisplayCertInfo(CertInfo certInfo) {
        DisplayCertInfo = certInfo;
    }

    public EventInfo getDisplayEventInfo() {
        return DisplayEventInfo;
    }

    public void setDisplayEventInfo(EventInfo certInfo) {
        DisplayEventInfo = certInfo;
    }
}
package com.simsme.mycustomcalendar;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ActiveAlarms {
    @PrimaryKey(autoGenerate = true)
    int serialNum;
    int rqCode;
    String from,title;

    public ActiveAlarms(int serialNum, int rqCode, String from, String title){
        this.serialNum=serialNum;
        this.rqCode=rqCode;
        this.from=from;
        this.title=title;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public int getRqCode() {
        return rqCode;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public void setRqCode(int rqCode) {
        this.rqCode = rqCode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

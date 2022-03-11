package com.simsme.mycustomcalendar;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    private int serial_num; //일정일련번호
    private int alarm_rqCode; //알람요청코드
    private String date; //날짜(년/월/일)
    private String title; //제목
    private String alarm; //알람시간(:)

    public Schedule(int serial_num, String date, String title, String alarm, int alarm_rqCode){
        this.serial_num=serial_num;
        this.date=date;
        this.title=title;
        this.alarm=alarm;
        this.alarm_rqCode=alarm_rqCode;
    }

    public int getSerial_num() {
        return serial_num;
    }

    public void setSerial_num(int serial_num) {
        this.serial_num = serial_num;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getAlarm() {
        return alarm;
    }

    public int getAlarm_rqCode() {
        return alarm_rqCode;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public void setAlarm_rqCode(int alarm_rqCode) {
        this.alarm_rqCode = alarm_rqCode;
    }
}

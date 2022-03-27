package com.simsme.mycustomcalendar.calendar;

public class ScheduleItem {
    String title,alarm,date;
    int rqCode;

    public ScheduleItem(String title, String alarm, String date, int rqCode){
        this.title=title;
        this.alarm=alarm;
        this.date=date;
        this.rqCode=rqCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRqCode() {
        return rqCode;
    }

    public void setRqCode(int rqCode) {
        this.rqCode = rqCode;
    }
}

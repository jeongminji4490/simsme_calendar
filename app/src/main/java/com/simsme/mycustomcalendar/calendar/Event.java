package com.simsme.mycustomcalendar.calendar;


import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"date"}, unique = true)})
public class Event {
    @PrimaryKey(autoGenerate = true)
    int serialNum;
    String date;

    public Event(int serialNum, String date){
        this.serialNum=serialNum;
        this.date=date;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

package com.example.mycustomcalendar;

import android.content.Context;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class ScheduleRepository {
    public ScheduleDao dao;
    Observable<List<Schedule>> scheduleList;
    Database db;

    public ScheduleRepository(Context context){
        db=Database.getInstance(context);
        dao=db.scheduleDao();
        scheduleList=dao.findAll();
    }

    public Observable<List<Schedule>> getScheduleList(){
        return scheduleList;
    }

    public int getRowCount(){
        return dao.getRowCount();
    }

    Completable Insert(Schedule s){
        return dao.insert(s);
    }

    Completable Delete(String t, String a, String d){
        return dao.delete(t,a,d);
    }

    Completable Update(Schedule schedule){
        return dao.update(schedule);
    }
}

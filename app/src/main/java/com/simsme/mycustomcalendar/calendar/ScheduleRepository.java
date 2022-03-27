package com.simsme.mycustomcalendar.calendar;

import android.content.Context;

import com.simsme.mycustomcalendar.db.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

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
        return dao.findAll()
                .subscribeOn(Schedulers.io());
    }

    Completable Insert(Schedule s){
        return dao.insert(s)
                .subscribeOn(Schedulers.io());
    }

    Completable Delete(String t, String a, String d){
        return dao.delete(t,a,d)
                .subscribeOn(Schedulers.io());
    }
}

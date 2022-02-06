package com.example.mycustomcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EventRepository {
    public EventDao dao;
    Observable<List<Event>> eventList;

    public EventRepository(Context context){
        Database db=Database.getInstance(context);
        dao=db.eventDao();
        eventList=dao.findAll();
    }

    public Observable<List<Event>> getAllList(){
        return eventList
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Completable Insert(Event event){
        return dao.insert(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    Completable Delete(String date){
        return dao.delete(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

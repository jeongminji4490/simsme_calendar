package com.example.mycustomcalendar;

import android.content.Context;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class EventRepository {
    public EventDao dao;
    Observable<List<Event>> eventList;

    public EventRepository(Context context){
        Database db=Database.getInstance(context);
        dao=db.eventDao();
        eventList=dao.findAll();
    }

    public Observable<List<Event>> getAllList(){
        return eventList;
    }

    Completable Insert(Event event){
        return dao.insert(event);
    }

    Completable Delete(String date){
        return dao.delete(date);
    }
}

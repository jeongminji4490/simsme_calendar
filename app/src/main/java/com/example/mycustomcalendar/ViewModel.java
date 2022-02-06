package com.example.mycustomcalendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class ViewModel extends AndroidViewModel {

    private final EventRepository e;
    private final ScheduleRepository s;

    public ViewModel(@NonNull Application application) {
        super(application);
        e=new EventRepository(application);
        s=new ScheduleRepository(application);
    }

    Observable<List<Schedule>> getAllSchedules(){
        return s.getScheduleList();
    }

    Completable InsertSchedule(Schedule schedule){
        return s.Insert(schedule);
    }

    Completable DeleteSchedule(String t, String a, String d){
        return s.Delete(t,a,d);
    }

    Observable<List<Event>> getAllEvents(){
        return e.getAllList();
    }

    Completable InsertEvent(Event event){
        return e.Insert(event);
    }

    Completable DeleteEvent(String date){
        return e.Delete(date);
    }

}

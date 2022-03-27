package com.simsme.mycustomcalendar.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import io.reactivex.Observable;

public class ViewModel extends AndroidViewModel {

    private final EventRepository e;
    private final ScheduleRepository s;
    private final Observable<List<Schedule>> sList;
    private final Observable<List<Event>> eList;

    public ViewModel(@NonNull Application application) {
        super(application);
        e=new EventRepository(application);
        s=new ScheduleRepository(application);
        sList=s.getScheduleList();
        eList=e.getAllList();
    }

    Observable<List<Schedule>> getAllSchedules(){
        return sList;
    }

    //구독을 해주지 않으면 해당 기능을 수행할 수 없음
    void InsertSchedule(Schedule schedule){
        s.Insert(schedule).subscribe();
    }

    void DeleteSchedule(String t, String a, String d){
        s.Delete(t,a,d).subscribe();
    }

    Observable<List<Event>> getAllEvents(){
        return eList;
    }

    void InsertEvent(Event event){
        e.Insert(event).subscribe();
    }

    void DeleteEvent(String date){
        e.Delete(date).subscribe();
    }

}

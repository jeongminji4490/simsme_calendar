package com.simsme.mycustomcalendar.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.simsme.mycustomcalendar.alarm.ActiveAlarms;
import com.simsme.mycustomcalendar.alarm.ActiveAlarmsDao;
import com.simsme.mycustomcalendar.calendar.Event;
import com.simsme.mycustomcalendar.calendar.EventDao;
import com.simsme.mycustomcalendar.calendar.Schedule;
import com.simsme.mycustomcalendar.calendar.ScheduleDao;

@androidx.room.Database(entities = {Schedule.class, Event.class, ActiveAlarms.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract EventDao eventDao();
    public abstract ScheduleDao scheduleDao();
    public abstract ActiveAlarmsDao alarmsDao();
    //싱글톤 패턴 사용
    private static volatile Database INSTANCE = null;

    public static Database getInstance(Context context){
        if (INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                    Database.class, "schedule_db").allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}

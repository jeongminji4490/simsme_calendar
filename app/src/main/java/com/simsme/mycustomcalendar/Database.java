package com.simsme.mycustomcalendar;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

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

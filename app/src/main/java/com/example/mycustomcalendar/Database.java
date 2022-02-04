package com.example.mycustomcalendar;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Event.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract EventDao eventDao();
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
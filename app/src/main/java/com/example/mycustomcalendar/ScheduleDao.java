package com.example.mycustomcalendar;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface ScheduleDao {
    @Query("SELECT * FROM Schedule")
    Observable<List<Schedule>> findAll();
    //List<Schedule> findAll();

    @Query("SELECT COUNT(serial_num) FROM Schedule")
    int getRowCount();

    @Insert
    Completable insert(Schedule schedule);

    //abstract
    @Query("DELETE FROM Schedule WHERE title = :title AND alarm = :alarm AND date = :date")
    Completable delete(String title, String alarm, String date);

    @Update
    Completable update(Schedule schedule);
}

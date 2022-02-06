package com.example.mycustomcalendar;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface ActiveAlarmsDao {
    @Query("select * from ActiveAlarms")
    List<ActiveAlarms> findAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ActiveAlarms alarm);

    @Query("DELETE FROM ActiveAlarms WHERE rqCode = :rqCode")
    void delete(int rqCode);
}

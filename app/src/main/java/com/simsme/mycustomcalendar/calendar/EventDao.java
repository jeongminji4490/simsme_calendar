package com.simsme.mycustomcalendar.calendar;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface EventDao {
    @Query("select * from Event")
    Observable<List<Event>> findAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Event event);

    @Query("DELETE FROM Event WHERE date = :date")
    Completable delete(String date);
}

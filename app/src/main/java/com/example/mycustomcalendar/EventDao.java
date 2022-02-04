package com.example.mycustomcalendar;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {
    @Query("select * from Event")
    List<Event> findAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    @Query("DELETE FROM Event WHERE date = :date")
    void delete(String date);
}

package com.josephb.personaleventplannerapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {

    @Query("SELECT * FROM events ORDER BY dateTime ASC")
    LiveData<List<Event>> observeAll();

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    Event findById(long id);

    @Insert
    long insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);
}

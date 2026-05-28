package com.josephb.lostandfoundmapapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert
    long insert(Item item);

    @Query("SELECT * FROM items ORDER BY id DESC")
    List<Item> getAll();
}

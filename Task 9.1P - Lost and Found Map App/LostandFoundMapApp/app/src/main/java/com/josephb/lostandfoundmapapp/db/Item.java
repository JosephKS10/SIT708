package com.josephb.lostandfoundmapapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String type;
    public String name;
    public String phone;
    public String description;
    public String date;
    public String locationName;
    public double latitude;
    public double longitude;

    public Item() {}
}

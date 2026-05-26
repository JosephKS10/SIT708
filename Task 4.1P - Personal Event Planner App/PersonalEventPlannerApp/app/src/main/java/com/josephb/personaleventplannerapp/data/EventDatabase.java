package com.josephb.personaleventplannerapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {

    private static volatile EventDatabase instance;

    public abstract EventDao eventDao();

    public static EventDatabase get(Context context) {
        if (instance == null) {
            synchronized (EventDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            EventDatabase.class,
                            "events.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}


package com.josephb.personaleventplannerapp;

import android.app.Application;

import com.josephb.personaleventplannerapp.data.EventRepository;

public class EventApp extends Application {

    private EventRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new EventRepository(this);
    }

    public EventRepository getRepository() {
        return repository;
    }
}
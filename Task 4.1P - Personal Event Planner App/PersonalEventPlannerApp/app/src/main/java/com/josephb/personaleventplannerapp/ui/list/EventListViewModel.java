package com.josephb.personaleventplannerapp.ui.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.josephb.personaleventplannerapp.EventApp;
import com.josephb.personaleventplannerapp.data.Event;
import com.josephb.personaleventplannerapp.data.EventRepository;

import java.util.List;

public class EventListViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final LiveData<List<Event>> events;

    public EventListViewModel(@NonNull Application application) {
        super(application);
        repository = ((EventApp) application).getRepository();
        events = repository.observeEvents();
    }

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public void delete(Event event) {
        repository.delete(event, null);
    }
}

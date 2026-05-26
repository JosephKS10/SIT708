package com.josephb.personaleventplannerapp.ui.add;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.josephb.personaleventplannerapp.EventApp;
import com.josephb.personaleventplannerapp.data.Event;
import com.josephb.personaleventplannerapp.data.EventRepository;

public class AddEventViewModel extends AndroidViewModel {

    private final EventRepository repository;

    public AddEventViewModel(@NonNull Application application) {
        super(application);
        repository = ((EventApp) application).getRepository();
    }

    public void save(String title, String category, String location, long dateTime, Runnable onDone) {
        Event event = new Event(title.trim(), category, location.trim(), dateTime);
        repository.save(event, id -> onDone.run());
    }
}
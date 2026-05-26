package com.josephb.personaleventplannerapp.ui.edit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.josephb.personaleventplannerapp.EventApp;
import com.josephb.personaleventplannerapp.data.Event;
import com.josephb.personaleventplannerapp.data.EventRepository;

public class EditEventViewModel extends AndroidViewModel {

    private final EventRepository repository;
    private final MutableLiveData<Event> event = new MutableLiveData<>();

    public EditEventViewModel(@NonNull Application application) {
        super(application);
        repository = ((EventApp) application).getRepository();
    }

    public LiveData<Event> getEvent() {
        return event;
    }

    public void load(long id) {
        repository.getEvent(id, event::postValue);
    }

    public void update(Event updated, Runnable onDone) {
        repository.update(updated, onDone);
    }

    public void delete(Event existing, Runnable onDone) {
        repository.delete(existing, onDone);
    }
}

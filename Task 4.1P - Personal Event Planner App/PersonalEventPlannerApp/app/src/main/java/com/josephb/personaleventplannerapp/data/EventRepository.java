package com.josephb.personaleventplannerapp.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {

    private final EventDao dao;
    private final ExecutorService executor;

    public EventRepository(Context context) {
        dao = EventDatabase.get(context).eventDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Event>> observeEvents() {
        return dao.observeAll();
    }

    public void getEvent(long id, Callback<Event> callback) {
        executor.execute(() -> {
            Event event = dao.findById(id);
            callback.onResult(event);
        });
    }

    public void save(Event event, Callback<Long> callback) {
        executor.execute(() -> {
            long id = dao.insert(event);
            if (callback != null) callback.onResult(id);
        });
    }

    public void update(Event event, Runnable onDone) {
        executor.execute(() -> {
            dao.update(event);
            if (onDone != null) onDone.run();
        });
    }

    public void delete(Event event, Runnable onDone) {
        executor.execute(() -> {
            dao.delete(event);
            if (onDone != null) onDone.run();
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}

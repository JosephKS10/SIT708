package com.josephb.lostandfoundmapapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.josephb.lostandfoundmapapp.db.AppDatabase;
import com.josephb.lostandfoundmapapp.db.Item;
import com.josephb.lostandfoundmapapp.ui.ItemAdapter;

import java.util.List;
import java.util.concurrent.Executors;

public class AllItemsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        rv = findViewById(R.id.rvItems);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Item> items = AppDatabase.get(this).itemDao().getAll();
            runOnUiThread(() -> {
                if (items.isEmpty()) {
                    tvEmpty.setVisibility(android.view.View.VISIBLE);
                    rv.setVisibility(android.view.View.GONE);
                } else {
                    tvEmpty.setVisibility(android.view.View.GONE);
                    rv.setVisibility(android.view.View.VISIBLE);
                    rv.setAdapter(new ItemAdapter(items));
                }
            });
        });
    }
}

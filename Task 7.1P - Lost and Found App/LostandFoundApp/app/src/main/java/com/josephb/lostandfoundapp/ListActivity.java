package com.josephb.lostandfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ItemAdapter adapter;
    private RecyclerView rv;
    private TextView tvEmpty;
    private SearchView searchView;
    private Spinner spFilter;

    private String currentCategoryFilter = "All";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = new DatabaseHelper(this);

        rv = findViewById(R.id.rv_items);
        tvEmpty = findViewById(R.id.tv_empty);
        searchView = findViewById(R.id.search_view);
        spFilter = findViewById(R.id.sp_filter_category);

        adapter = new ItemAdapter(item -> {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("item_id", item.getId());
            startActivity(i);
        });
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        // Category filter spinner: "All" + all real categories
        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("All");
        Collections.addAll(filterOptions, Item.CATEGORIES);
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, filterOptions);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);
        spFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentCategoryFilter = filterOptions.get(position);
                refresh();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query == null ? "" : query;
                refresh();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText == null ? "" : newText;
                refresh();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<Item> items = db.getItemsFiltered(currentCategoryFilter, currentSearchQuery);
        adapter.setItems(items);
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
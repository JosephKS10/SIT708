package com.josephb.lostandfoundmapapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.josephb.lostandfoundmapapp.R;
import com.josephb.lostandfoundmapapp.db.Item;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {

    private final List<Item> data;

    public ItemAdapter(List<Item> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Item it = data.get(position);
        h.tvType.setText(it.type);
        h.tvName.setText(it.name);
        h.tvDescription.setText(it.description);
        h.tvDate.setText(it.date);
        h.tvLocation.setText(it.locationName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvType, tvName, tvDescription, tvDate, tvLocation;

        VH(@NonNull View v) {
            super(v);
            tvType = v.findViewById(R.id.tvType);
            tvName = v.findViewById(R.id.tvName);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvDate = v.findViewById(R.id.tvDate);
            tvLocation = v.findViewById(R.id.tvLocation);
        }
    }
}

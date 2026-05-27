package com.josephb.lostandfoundapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final List<Item> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public ItemAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Item> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lost_found, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Item item = items.get(position);
        String prefix = Item.TYPE_LOST.equals(item.getPostType()) ? "Lost " : "Found ";
        h.tvTitle.setText(prefix + item.getName());
        h.tvSubtitle.setText(item.getCategory() + " · " + TimeFormatter.relative(item.getCreatedAt()));

        h.ivThumb.setImageBitmap(null);
        if (item.getImagePath() != null) {
            Bitmap bmp = decodeSampledBitmap(item.getImagePath(), 200, 200);
            if (bmp != null) h.ivThumb.setImageBitmap(bmp);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle, tvSubtitle;

        VH(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_thumb);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubtitle = itemView.findViewById(R.id.tv_subtitle);
        }
    }

    private static Bitmap decodeSampledBitmap(String path, int reqW, int reqH) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);

        int sample = 1;
        int h = opts.outHeight, w = opts.outWidth;
        while ((h / sample) > reqH || (w / sample) > reqW) sample *= 2;

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = sample;
        return BitmapFactory.decodeFile(path, opts);
    }
}
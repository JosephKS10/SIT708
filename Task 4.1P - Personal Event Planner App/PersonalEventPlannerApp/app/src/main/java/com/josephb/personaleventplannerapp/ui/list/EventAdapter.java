package com.josephb.personaleventplannerapp.ui.list;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.josephb.personaleventplannerapp.R;
import com.josephb.personaleventplannerapp.data.Event;
import com.josephb.personaleventplannerapp.databinding.ItemEventBinding;
import com.josephb.personaleventplannerapp.util.DateUtils;

public class EventAdapter extends ListAdapter<Event, EventAdapter.VH> {

    public interface OnEventClickListener {
        void onClick(Event event);
    }

    private final OnEventClickListener listener;

    public EventAdapter(OnEventClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventBinding binding = ItemEventBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }

    class VH extends RecyclerView.ViewHolder {

        private final ItemEventBinding binding;

        VH(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.titleView.setText(event.getTitle());
            binding.dateView.setText(DateUtils.format(event.getDateTime()));
            binding.locationView.setText(
                    event.getLocation().isEmpty() ? "—" : event.getLocation());
            binding.categoryChip.setText(event.getCategory());

            int bgColorRes;
            int fgColorRes;
            String category = event.getCategory();
            String ctxString = binding.getRoot().getContext().getString(R.string.cat_work);

            if (category.equals(binding.getRoot().getContext().getString(R.string.cat_work))) {
                bgColorRes = R.color.chip_work_bg;
                fgColorRes = R.color.chip_work_fg;
            } else if (category.equals(binding.getRoot().getContext().getString(R.string.cat_social))) {
                bgColorRes = R.color.chip_social_bg;
                fgColorRes = R.color.chip_social_fg;
            } else if (category.equals(binding.getRoot().getContext().getString(R.string.cat_travel))) {
                bgColorRes = R.color.chip_travel_bg;
                fgColorRes = R.color.chip_travel_fg;
            } else {
                bgColorRes = R.color.chip_other_bg;
                fgColorRes = R.color.chip_other_fg;
            }

            GradientDrawable bg = (GradientDrawable) binding.categoryChip.getBackground().mutate();
            bg.setColor(ContextCompat.getColor(binding.getRoot().getContext(), bgColorRes));
            binding.categoryChip.setTextColor(
                    ContextCompat.getColor(binding.getRoot().getContext(), fgColorRes));

            binding.getRoot().setOnClickListener(v -> listener.onClick(event));
        }
    }

    private static final DiffUtil.ItemCallback<Event> DIFF = new DiffUtil.ItemCallback<Event>() {
        @Override
        public boolean areItemsTheSame(@NonNull Event a, @NonNull Event b) {
            return a.getId() == b.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Event a, @NonNull Event b) {
            return a.getTitle().equals(b.getTitle())
                    && a.getCategory().equals(b.getCategory())
                    && a.getLocation().equals(b.getLocation())
                    && a.getDateTime() == b.getDateTime();
        }
    };
}

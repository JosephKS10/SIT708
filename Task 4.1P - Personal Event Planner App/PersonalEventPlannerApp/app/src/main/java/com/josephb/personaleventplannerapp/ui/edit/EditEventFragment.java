package com.josephb.personaleventplannerapp.ui.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.josephb.personaleventplannerapp.R;
import com.josephb.personaleventplannerapp.data.Event;
import com.josephb.personaleventplannerapp.databinding.FragmentEditEventBinding;
import com.josephb.personaleventplannerapp.util.DateUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class EditEventFragment extends Fragment {

    private FragmentEditEventBinding binding;
    private EditEventViewModel viewModel;

    private Event loadedEvent = null;
    private Long pickedDateMillis = null;
    private Integer pickedHour = null;
    private Integer pickedMinute = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEditEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(EditEventViewModel.class);

        long eventId = getArguments() != null ? getArguments().getLong("eventId", -1L) : -1L;
        viewModel.load(eventId);

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null && loadedEvent == null) {
                loadedEvent = event;
                prefill(event);
            }
        });

        binding.pickDateBtn.setOnClickListener(v -> showDatePicker());
        binding.pickTimeBtn.setOnClickListener(v -> showTimePicker());
        binding.saveBtn.setOnClickListener(v -> attemptUpdate());
        binding.deleteBtn.setOnClickListener(v -> attemptDelete());
    }

    private void prefill(Event event) {
        binding.titleField.setText(event.getTitle());
        binding.locationField.setText(event.getLocation());
        binding.dateDisplay.setText(DateUtils.format(event.getDateTime()));

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(event.getDateTime());
        pickedDateMillis = event.getDateTime();
        pickedHour = cal.get(Calendar.HOUR_OF_DAY);
        pickedMinute = cal.get(Calendar.MINUTE);

        int chipId;
        String category = event.getCategory();
        if (category.equals(getString(R.string.cat_work))) chipId = R.id.chip_work;
        else if (category.equals(getString(R.string.cat_social))) chipId = R.id.chip_social;
        else if (category.equals(getString(R.string.cat_travel))) chipId = R.id.chip_travel;
        else chipId = R.id.chip_other;

        binding.categoryGroup.check(chipId);
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.action_pick_date))
                .setSelection(pickedDateMillis != null
                        ? pickedDateMillis
                        : MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        picker.addOnPositiveButtonClickListener(millis -> {
            pickedDateMillis = millis;
            refreshDateDisplay();
        });
        picker.show(getParentFragmentManager(), "date");
    }

    private void showTimePicker() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(pickedHour != null ? pickedHour : 9)
                .setMinute(pickedMinute != null ? pickedMinute : 0)
                .setTitleText(getString(R.string.action_pick_time))
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            pickedHour = picker.getHour();
            pickedMinute = picker.getMinute();
            refreshDateDisplay();
        });
        picker.show(getParentFragmentManager(), "time");
    }

    private void refreshDateDisplay() {
        Long combined = combineDateAndTime();
        if (combined == null) return;
        binding.dateDisplay.setText(DateUtils.format(combined));
    }

    private Long combineDateAndTime() {
        if (pickedDateMillis == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(pickedDateMillis);
        cal.set(Calendar.HOUR_OF_DAY, pickedHour != null ? pickedHour : 9);
        cal.set(Calendar.MINUTE, pickedMinute != null ? pickedMinute : 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private void attemptUpdate() {
        if (loadedEvent == null) return;

        String title = binding.titleField.getText() != null
                ? binding.titleField.getText().toString() : "";

        if (title.trim().isEmpty()) {
            binding.titleInputLayout.setError(getString(R.string.err_title_required));
            return;
        }
        binding.titleInputLayout.setError(null);

        Long combined = combineDateAndTime();
        if (combined == null) combined = loadedEvent.getDateTime();

        int checkedId = binding.categoryGroup.getCheckedChipId();
        String category;
        if (checkedId == R.id.chip_work) category = getString(R.string.cat_work);
        else if (checkedId == R.id.chip_social) category = getString(R.string.cat_social);
        else if (checkedId == R.id.chip_travel) category = getString(R.string.cat_travel);
        else category = getString(R.string.cat_other);

        String location = binding.locationField.getText() != null
                ? binding.locationField.getText().toString() : "";

        loadedEvent.setTitle(title.trim());
        loadedEvent.setCategory(category);
        loadedEvent.setLocation(location.trim());
        loadedEvent.setDateTime(combined);

        viewModel.update(loadedEvent, () -> requireActivity().runOnUiThread(() -> {
            Snackbar.make(binding.getRoot(), R.string.msg_updated, Snackbar.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
        }));
    }

    private void attemptDelete() {
        if (loadedEvent == null) return;

        viewModel.delete(loadedEvent, () -> requireActivity().runOnUiThread(() -> {
            Snackbar.make(binding.getRoot(), R.string.msg_deleted, Snackbar.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigateUp();
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
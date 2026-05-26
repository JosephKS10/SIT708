package com.josephb.personaleventplannerapp.ui.add;

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
import com.josephb.personaleventplannerapp.databinding.FragmentAddEventBinding;
import com.josephb.personaleventplannerapp.util.DateUtils;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class AddEventFragment extends Fragment {

    private FragmentAddEventBinding binding;
    private AddEventViewModel viewModel;

    private Long pickedDateMillis = null;
    private Integer pickedHour = null;
    private Integer pickedMinute = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AddEventViewModel.class);

        binding.chipWork.setChecked(true);

        binding.pickDateBtn.setOnClickListener(v -> showDatePicker());
        binding.pickTimeBtn.setOnClickListener(v -> showTimePicker());
        binding.saveBtn.setOnClickListener(v -> attemptSave());
    }

    private void showDatePicker() {
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.action_pick_date))
                .setCalendarConstraints(constraints)
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
        int hour = pickedHour != null ? pickedHour : 9;
        int minute = pickedMinute != null ? pickedMinute : 0;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(pickedDateMillis);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private void attemptSave() {
        String title = binding.titleField.getText() != null
                ? binding.titleField.getText().toString() : "";

        if (title.trim().isEmpty()) {
            binding.titleInputLayout.setError(getString(R.string.err_title_required));
            return;
        }
        binding.titleInputLayout.setError(null);

        Long combined = combineDateAndTime();
        if (combined == null) {
            Snackbar.make(binding.getRoot(), R.string.err_date_required, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (combined < System.currentTimeMillis()) {
            Snackbar.make(binding.getRoot(), R.string.err_date_past, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String category = selectedCategory();
        String location = binding.locationField.getText() != null
                ? binding.locationField.getText().toString() : "";

        viewModel.save(title, category, location, combined, () -> {
            requireActivity().runOnUiThread(() -> {
                Snackbar.make(binding.getRoot(), R.string.msg_saved, Snackbar.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigate(R.id.eventListFragment);
                binding.titleField.setText("");
                binding.locationField.setText("");
                binding.dateDisplay.setText("—");
                pickedDateMillis = null;
                pickedHour = null;
                pickedMinute = null;
            });
        });
    }

    private String selectedCategory() {
        int checkedId = binding.categoryGroup.getCheckedChipId();
        if (checkedId == R.id.chip_work) return getString(R.string.cat_work);
        if (checkedId == R.id.chip_social) return getString(R.string.cat_social);
        if (checkedId == R.id.chip_travel) return getString(R.string.cat_travel);
        return getString(R.string.cat_other);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
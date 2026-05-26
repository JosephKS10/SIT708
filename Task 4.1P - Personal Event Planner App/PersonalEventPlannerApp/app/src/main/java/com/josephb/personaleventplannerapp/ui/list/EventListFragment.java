package com.josephb.personaleventplannerapp.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.josephb.personaleventplannerapp.R;
import com.josephb.personaleventplannerapp.databinding.FragmentEventListBinding;

public class EventListFragment extends Fragment {

    private FragmentEventListBinding binding;
    private EventListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(EventListViewModel.class);

        EventAdapter adapter = new EventAdapter(event -> {
            Bundle args = new Bundle();
            args.putLong("eventId", event.getId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_list_to_edit, args);
        });

        binding.eventList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.eventList.setAdapter(adapter);

        viewModel.getEvents().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            binding.emptyLabel.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

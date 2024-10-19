package com.koistorynew.ui.consult;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.koistorynew.databinding.FragmentConsultBinding;

public class ConsultFragment extends Fragment {

    private FragmentConsultBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ConsultViewModel consultViewModel =
                new ViewModelProvider(this).get(ConsultViewModel.class);

        binding = FragmentConsultBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textConsult;
        consultViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

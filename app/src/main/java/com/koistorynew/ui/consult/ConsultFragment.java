package com.koistorynew.ui.consult;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.databinding.FragmentConsultBinding;
import com.koistorynew.ui.consult.adapter.ConsultAdapter;


public class ConsultFragment extends Fragment {

    private FragmentConsultBinding binding;
    private ConsultViewModel consultViewModel;
    private ConsultAdapter consultAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        consultViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ConsultViewModel(getContext());
            }
        }).get(ConsultViewModel.class);

        binding = FragmentConsultBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewMarket;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

//        consultViewModel.getMarketPostsLiveData().observe(getViewLifecycleOwner(), postMarket -> {
//            if (consultAdapter == null) {
//                consultAdapter = new ConsultAdapter(postMarket);
//                recyclerView.setAdapter(consultAdapter);
//            } else {
//                consultAdapter.updateData(postMarket);
//            }
//        });
        consultViewModel.getConsult().observe(getViewLifecycleOwner(), postBlogs -> {
            consultAdapter = new ConsultAdapter(postBlogs);
            recyclerView.setAdapter(consultAdapter);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        consultViewModel.fetchConsultPosts();
    }
}

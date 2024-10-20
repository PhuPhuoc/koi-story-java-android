package com.koistorynew.ui.myconsult;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koistorynew.databinding.FragmentConsultBinding;
import com.koistorynew.databinding.FragmentMyconsultBinding;
import com.koistorynew.ui.consult.ConsultViewModel;
import com.koistorynew.ui.consult.adapter.ConsultAdapter;
import com.koistorynew.ui.myconsult.adapter.MyConsultAdapter;
import com.koistorynew.ui.mymarket.AddMarketActivity;

public class MyConsultFragment extends Fragment {
    private FragmentMyconsultBinding binding;
    private MyConsultViewModel myConsultViewModel;
    private MyConsultAdapter myConsultAdapter;
    public static final int REQUEST_CODE_ADD_MARKET = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        myConsultViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyConsultViewModel(getContext());
            }
        }).get(MyConsultViewModel.class);

        binding = FragmentMyconsultBinding.inflate(inflater, container, false);
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
        myConsultViewModel.getConsult().observe(getViewLifecycleOwner(), postBlogs -> {
            myConsultAdapter = new MyConsultAdapter(postBlogs);
            recyclerView.setAdapter(myConsultAdapter);
        });
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddConsultActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MARKET);
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
//        consultViewModel.fetchConsult();
    }
}

package com.koistorynew.ui.market;

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

import com.koistorynew.databinding.FragmentMarketBinding;
import com.koistorynew.ui.market.adapter.MarketAdapter;

public class MarketFragment extends Fragment {
    private FragmentMarketBinding binding;
    private MarketAdapter marketAdapter;
    private MarketViewModel marketViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        marketViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MarketViewModel(getContext());
            }
        }).get(MarketViewModel.class);

        binding = FragmentMarketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewMarket;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        marketViewModel.getMarketPostsLiveData().observe(getViewLifecycleOwner(), postMarket -> {
            if (marketAdapter == null) {
                marketAdapter = new MarketAdapter(postMarket);
                recyclerView.setAdapter(marketAdapter);
            } else {
                marketAdapter.updateData(postMarket);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        marketViewModel.fetchMarketPosts();
    }
}

package com.koistorynew.ui.market;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.databinding.FragmentMarketBinding;
import com.koistorynew.ui.market.adapter.MarketAdapter;

public class MarketFragment extends Fragment {
    private FragmentMarketBinding binding;
    private MarketAdapter marketAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MarketViewModel marketViewModel =
                new ViewModelProvider(this).get(MarketViewModel.class);

        // Sử dụng View Binding để lấy view từ fragment_blog.xml
        binding = FragmentMarketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Thiết lập RecyclerView
        RecyclerView recyclerView = binding.recyclerViewMarket;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Quan sát LiveData từ BlogViewModel để cập nhật dữ liệu cho RecyclerView khi thay đổi
        marketViewModel.getDataFromBlogViewModel().observe(getViewLifecycleOwner(), postMarket -> {
            marketAdapter = new MarketAdapter(postMarket);
            recyclerView.setAdapter(marketAdapter);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng tài nguyên khi View bị hủy
    }
}

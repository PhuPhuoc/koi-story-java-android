package com.koistorynew.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.koistorynew.R;
import com.koistorynew.databinding.FragmentHomeBinding;
import com.koistorynew.ui.fish.adapter.KoiFishAdapter;
import com.koistorynew.ui.fish.model.KoiFish;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private GridView koiGrid;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Khởi tạo GridView từ binding
        koiGrid = binding.koiGrid; // Đảm bảo rằng bạn đã khai báo GridView trong binding

        // Tạo danh sách KoiFish
        List<KoiFish> koiFishList = new ArrayList<>();
        koiFishList.add(new KoiFish(R.drawable.asagi, "Asagi"));
        koiFishList.add(new KoiFish(R.drawable.gin_matsuba, "Gin Matsuba"));

        // Tạo adapter và gán cho GridView
        KoiFishAdapter adapter = new KoiFishAdapter(getActivity(), koiFishList);
        koiGrid.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

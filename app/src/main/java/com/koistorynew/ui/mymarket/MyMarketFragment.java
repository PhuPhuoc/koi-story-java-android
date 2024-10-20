package com.koistorynew.ui.mymarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koistorynew.databinding.FragmentMymarketBinding;
import com.koistorynew.ui.mymarket.adapter.MyMarketAdapter;
import static android.app.Activity.RESULT_OK;
public class MyMarketFragment extends Fragment {
    private FragmentMymarketBinding binding;
    private MyMarketAdapter myMarketAdapter;
    private static final int REQUEST_CODE_ADD_MARKET = 1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyMarketViewModel marketViewModel =
                new ViewModelProvider(this).get(MyMarketViewModel.class);

        // Sử dụng View Binding để lấy view từ fragment_blog.xml
        binding = FragmentMymarketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Thiết lập RecyclerView
        RecyclerView recyclerView = binding.recyclerViewMarket;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Quan sát LiveData từ BlogViewModel để cập nhật dữ liệu cho RecyclerView khi thay đổi
        marketViewModel.getDataFromBlogViewModel().observe(getViewLifecycleOwner(), postMarket -> {
            myMarketAdapter = new MyMarketAdapter(postMarket);
            recyclerView.setAdapter(myMarketAdapter);
        });

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(v -> {
            // Mở AddMarketActivity khi bấm FAB
            Intent intent = new Intent(getActivity(), AddMarketActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MARKET);
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_MARKET && resultCode == RESULT_OK && data != null) {
            // Nhận dữ liệu từ AddMarketActivity
            String name = data.getStringExtra("name");
            String imageUrl = data.getStringExtra("imageUrl");
            int price = data.getIntExtra("price", 0);
            String description = data.getStringExtra("description");

            // TODO: Xử lý dữ liệu nhận được và cập nhật vào danh sách
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng tài nguyên khi View bị hủy
    }
}

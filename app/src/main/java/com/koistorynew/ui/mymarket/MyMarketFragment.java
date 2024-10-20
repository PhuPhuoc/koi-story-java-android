package com.koistorynew.ui.mymarket;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koistorynew.databinding.FragmentMymarketBinding;
import com.koistorynew.ui.mymarket.adapter.MyMarketAdapter;

public class MyMarketFragment extends Fragment {
    private FragmentMymarketBinding binding;
    private MyMarketAdapter myMarketAdapter;
    private MyMarketViewModel myMarketViewModel;
    public static final int REQUEST_CODE_ADD_MARKET = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        myMarketViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyMarketViewModel(getContext());
            }
        }).get(MyMarketViewModel.class);

        binding = FragmentMymarketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewMarket;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        // Quan sát LiveData từ BlogViewModel để cập nhật dữ liệu cho RecyclerView khi thay đổi
        myMarketViewModel.getMyMarketPostsLiveData().observe(getViewLifecycleOwner(), postMarket -> {
            if (myMarketAdapter == null) {
                myMarketAdapter = new MyMarketAdapter(getContext(),postMarket, itemId -> showDeleteConfirmationDialog(itemId));
                recyclerView.setAdapter(myMarketAdapter);
            } else {
                myMarketAdapter.updateData(postMarket);
            }
        });

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMarketActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MARKET);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        myMarketViewModel.fetchMyMarketPosts();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng tài nguyên khi View bị hủy
    }

    private void showDeleteConfirmationDialog(String itemId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa mục này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Log ID của mục
                    Log.d("MyMarketFragment", "Đã xóa mục có ID: " + itemId);

                    Toast.makeText(getContext(), "Mục đã được xóa", Toast.LENGTH_SHORT).show();
                    onResume();
                    myMarketViewModel.deleteMarketItem(itemId);
                    myMarketViewModel.refreshMarketPosts();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

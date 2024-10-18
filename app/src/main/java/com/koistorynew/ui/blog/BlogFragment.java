package com.koistorynew.ui.blog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.databinding.FragmentBlogBinding;
import com.koistorynew.ui.blog.adapter.BlogAdapter;

public class BlogFragment extends Fragment {

    private FragmentBlogBinding binding;
    private BlogAdapter blogAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BlogViewModel blogViewModel =
                new ViewModelProvider(this).get(BlogViewModel.class);

        // Sử dụng View Binding để lấy view từ fragment_blog.xml
        binding = FragmentBlogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Thiết lập RecyclerView
        RecyclerView recyclerView = binding.recyclerViewBlog;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Quan sát LiveData từ BlogViewModel để cập nhật dữ liệu cho RecyclerView khi thay đổi
        blogViewModel.getDataFromBlogViewModel().observe(getViewLifecycleOwner(), postBlogs -> {
            blogAdapter = new BlogAdapter(postBlogs);
            recyclerView.setAdapter(blogAdapter);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng tài nguyên khi View bị hủy
    }
}

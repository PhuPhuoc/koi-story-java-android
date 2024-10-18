package com.koistorynew.ui.blog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.koistorynew.ui.blog.model.PostBlog;

import java.util.ArrayList;
import java.util.List;

public class BlogViewModel extends ViewModel {
    private final MutableLiveData<List<PostBlog>> arr_post_blog; // khai báo data sẽ sài trong blog fragment

    public BlogViewModel() {
        arr_post_blog = new MutableLiveData<>();
        arr_post_blog.setValue(generateDummyData());
    }

    // lấy data sẽ sài trong blog fragment
    public LiveData<List<PostBlog>> getDataFromBlogViewModel() {
        return arr_post_blog;
    }

    private List<PostBlog> generateDummyData() {
        List<PostBlog> dummyList = new ArrayList<>();

        // Thêm các bài đăng mẫu vào danh sách
        dummyList.add(new PostBlog("1", "John Doe", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", "Introduction to Android"));
        dummyList.add(new PostBlog("2", "Jane Smith", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", "Understanding ViewModel"));
        dummyList.add(new PostBlog("3", "Michael Lee", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", "Working with Fragments"));
        dummyList.add(new PostBlog("4", "Emma Brown", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", "Best Practices for UI Design"));

        return dummyList;
    }
}

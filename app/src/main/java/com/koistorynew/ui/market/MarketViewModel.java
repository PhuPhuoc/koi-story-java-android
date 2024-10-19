package com.koistorynew.ui.market;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.koistorynew.ui.market.model.PostMarket;

import java.util.ArrayList;
import java.util.List;

public class MarketViewModel extends ViewModel {
    private final MutableLiveData<List<PostMarket>> arr_post_market; // khai báo data sẽ sài trong blog fragment

    public MarketViewModel() {
        arr_post_market = new MutableLiveData<>();
        arr_post_market.setValue(generateDummyData());
    }

    // lấy data sẽ sài trong blog fragment
    public LiveData<List<PostMarket>> getDataFromBlogViewModel() {
        return arr_post_market;
    }

    private List<PostMarket> generateDummyData() {
        List<PostMarket> dummyList = new ArrayList<>();

        // Thêm các bài đăng mẫu vào danh sách
        // dummyList.add(new PostBlog());

        return dummyList;
    }
}

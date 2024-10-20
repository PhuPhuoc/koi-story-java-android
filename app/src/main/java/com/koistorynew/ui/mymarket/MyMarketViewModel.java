package com.koistorynew.ui.mymarket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.koistorynew.ui.market.model.PostMarket;
import com.koistorynew.ui.mymarket.model.MyMarket;

import java.util.ArrayList;
import java.util.List;


public class MyMarketViewModel extends ViewModel {
    private final MutableLiveData<List<MyMarket>> arr_post_market;

    public MyMarketViewModel() {
        arr_post_market = new MutableLiveData<>();
        arr_post_market.setValue(generateDummyData());
    }

    public LiveData<List<MyMarket>> getDataFromBlogViewModel() {
        return arr_post_market;
    }

    private List<MyMarket> generateDummyData() {
        List<MyMarket> dummyList = new ArrayList<>();

        dummyList.add(new MyMarket("Pencilin", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", 12,"123456789231456789"));
        dummyList.add(new MyMarket("Pencilin", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", 12,"1234567891278212"));
        dummyList.add(new MyMarket("Pencilin", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", 12,"ahahaahahahhahaha"));
        dummyList.add(new MyMarket("Pencilin", "https://pantravel.vn/wp-content/uploads/2023/11/phong-canh-thien-nhien-dep-nhat-the-gioi-1.jpg", 12,"for testing onlin"));
        return dummyList;
    }
}

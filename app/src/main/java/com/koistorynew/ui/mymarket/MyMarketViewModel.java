package com.koistorynew.ui.mymarket;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.koistorynew.ApiService;
import com.koistorynew.ui.mymarket.model.MyMarket;

import java.util.ArrayList;
import java.util.List;


public class MyMarketViewModel extends ViewModel {
    private final MutableLiveData<List<MyMarket>> arr_post_market;
    private static final String TAG = "MyMarketViewModel";
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;
    private final ApiService apiService;

    public MyMarketViewModel(Context context) {
        arr_post_market = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.apiService = new ApiService(requestQueue);
        fetchMyMarketPosts(); // L
    }

    public LiveData<List<MyMarket>> getMyMarketPostsLiveData() {
        return arr_post_market;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchMyMarketPosts() {
        isLoading.setValue(true);
        error.setValue(null);

        apiService.getMyMarketPosts(new ApiService.DataMyMarketCallback<List<MyMarket>>() {
            @Override
            public void onSuccess(List<MyMarket> data) {
                arr_post_market.setValue(data);
                isLoading.setValue(false);
                error.setValue(null);
            }

            @Override
            public void onError() {
                Log.e(TAG, "Failed to fetch market posts.");
                isLoading.setValue(false);
                error.setValue("Failed to fetch market posts.");
            }
        });
    }

    public void refreshMarketPosts() {
        fetchMyMarketPosts();
    }

    public void deleteMarketItem(String itemId) {
        apiService.deleteMarketPost(itemId, new ApiService.DataCallback<String>() {
            @Override
            public void onSuccess(String message) {
                List<MyMarket> currentList = arr_post_market.getValue();
                if (currentList != null) {
                    List<MyMarket> updatedList = new ArrayList<>(currentList);
                    updatedList.removeIf(item -> item.getId().equals(itemId));
                    arr_post_market.setValue(updatedList);
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "Failed to delete market item.");
                error.setValue("Failed to delete market item");
            }
        });
    }


    // Method to clear error
    public void clearError() {
        error.setValue(null);
    }
}

package com.koistorynew.ui.market;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.koistorynew.ApiService;
import com.koistorynew.ui.market.model.PostMarket;

import java.util.List;

public class MarketViewModel extends ViewModel {
    private static final String TAG = "MarketViewModel";

    private final MutableLiveData<List<PostMarket>> arr_post_market;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;
    private final ApiService apiService;

    // Constructor
    public MarketViewModel(Context context) {
        arr_post_market = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        // Instantiate ApiService using the provided RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.apiService = new ApiService(requestQueue);
        fetchMarketPosts(); // Load initial data
    }

    // Getter methods for LiveData
    public LiveData<List<PostMarket>> getMarketPostsLiveData() {
        return arr_post_market;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // Fetch market posts using ApiService
    public void fetchMarketPosts() {
        isLoading.setValue(true);
        error.setValue(null);

        apiService.getMarketPosts(new ApiService.DataCallback<List<PostMarket>>() {
            @Override
            public void onSuccess(List<PostMarket> data) {
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
        fetchMarketPosts();
    }

    // Method to clear error
    public void clearError() {
        error.setValue(null);
    }
}

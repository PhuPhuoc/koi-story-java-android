package com.koistorynew.ui.myconsult;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.koistorynew.ApiService;
import com.koistorynew.UserSessionManager;
import com.koistorynew.ui.consult.model.Consult;
import com.koistorynew.ui.myconsult.model.MyConsult;
import com.koistorynew.ui.mymarket.model.MyMarket;

import java.util.ArrayList;
import java.util.List;

public class MyConsultViewModel extends ViewModel {
    private static final String TAG = "MyConsultViewModel";

    private final MutableLiveData<List<MyConsult>> arr_post_market;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;
    private final ApiService apiService;

    // Constructor
    public MyConsultViewModel(Context context) {
        arr_post_market = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        // Instantiate ApiService using the provided RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.apiService = new ApiService(requestQueue);

        fetchMyConsultPosts(); // Load dummy data for testing
    }

    // Getter methods for LiveData
    public LiveData<List<MyConsult>> getConsult() {
        return arr_post_market;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // Method to generate dummy data
    public void fetchMyConsultPosts() {
        isLoading.setValue(true);
        error.setValue(null);
        String userId = UserSessionManager.getInstance().getFbUid();
        apiService.getMyConsultPosts(userId, new ApiService.DataMyMarketCallback<List<MyConsult>>() {

            @Override
            public void onSuccess(List<MyConsult> data) {
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

    public void refreshConsultPosts() {
        fetchMyConsultPosts();
    }

    public void deleteMarketItem(String itemId) {
        apiService.deleteConsultPost(itemId, new ApiService.DataCallback<String>() {
            @Override
            public void onSuccess(String message) {
                List<MyConsult> currentList = arr_post_market.getValue();
                if (currentList != null) {
                    List<MyConsult> updatedList = new ArrayList<>(currentList);
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

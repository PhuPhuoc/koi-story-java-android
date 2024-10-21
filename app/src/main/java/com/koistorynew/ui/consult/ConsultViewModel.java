package com.koistorynew.ui.consult;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.koistorynew.ApiService;
import com.koistorynew.ui.consult.model.Consult;

import java.util.List;

public class ConsultViewModel extends ViewModel {
    private static final String TAG = "ConsultViewModel";
    private final MutableLiveData<List<Consult>> arr_post_consult;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;
    private final ApiService apiService;

    // Constructor
    public ConsultViewModel(Context context) {
        arr_post_consult = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        // Instantiate ApiService using the provided RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.apiService = new ApiService(requestQueue);

        fetchConsultPosts();
    }


    // Getter methods for LiveData
    public LiveData<List<Consult>> getConsult() {
        return arr_post_consult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchConsultPosts() {
        isLoading.setValue(true);
        error.setValue(null);

        apiService.getConsultPosts(new ApiService.DataCallback<List<Consult>>() {
            @Override
            public void onSuccess(List<Consult> data) {
                arr_post_consult.setValue(data);
                isLoading.setValue(false);
                error.setValue(null);
            }

            @Override
            public void onError() {
                Log.e(TAG, "Failed to fetch consult posts.");
                isLoading.setValue(false);
                error.setValue("Failed to fetch consult posts.");
            }
        });
    }

    // Method to clear error
    public void clearError() {
        error.setValue(null);
    }
}

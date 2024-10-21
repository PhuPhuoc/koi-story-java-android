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

import java.util.ArrayList;
import java.util.List;

public class ConsultViewModel extends ViewModel {

    private static final String TAG = "ConsultViewModel";

    private final MutableLiveData<List<Consult>> arr_post_market;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;
    private final ApiService apiService;

    // Constructor
    public ConsultViewModel(Context context) {
        arr_post_market = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();
        // Instantiate ApiService using the provided RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.apiService = new ApiService(requestQueue);

        generateDummyData(); // Load dummy data for testing
    }

    // Getter methods for LiveData
    public LiveData<List<Consult>> getConsult() {
        return arr_post_market;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // Method to generate dummy data
    private void generateDummyData() {
        List<Consult> dummyData = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            dummyData.add(new Consult(
//                    "id" + i,
//                    "https://example.com/image" + i + ".jpg", // Placeholder image URL
//                    "User " + i,
//                    "Title " + i,
//                    "This is a sample question " + i + "?",
//                    "https://example.com/avatar" + i + ".jpg" // Placeholder avatar URL
//            ));
//        }
        arr_post_market.setValue(dummyData); // Set the dummy data
    }

    // Method to clear error
    public void clearError() {
        error.setValue(null);
    }
}

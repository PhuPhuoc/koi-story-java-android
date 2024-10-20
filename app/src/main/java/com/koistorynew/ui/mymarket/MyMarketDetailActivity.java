package com.koistorynew.ui.mymarket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.koistorynew.ApiService;
import com.koistorynew.R;
import com.koistorynew.ui.market.MarketCommentActivity;
import com.koistorynew.ui.market.MarketDetailsActivity;
import com.koistorynew.ui.market.MarketViewModel;
import com.koistorynew.ui.market.adapter.ImageSliderAdapter;
import com.koistorynew.ui.market.model.PostMarketDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyMarketDetailActivity extends AppCompatActivity {

    private Handler sliderHandler = new Handler();
    private static final String TAG = "MyMarketDetailActivity";
    private ViewPager2 viewPager;
    private ImageSliderAdapter sliderAdapter;
    private ApiService apiService;
    private RequestQueue requestQueue;
    private MyMarketViewModel myMarketViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_mymarket_detail);

        // Thiết lập nút back trên ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Market Details");

        myMarketViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyMarketViewModel(MyMarketDetailActivity.this);
            }
        }).get(MyMarketViewModel.class);


        Intent intent = getIntent();
        String productId = intent.getStringExtra("PRODUCT_ID");

        // Initialize RequestQueue and ApiService
        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);

        // Initialize views
        initializeViews();

        // Fetch market post detail
        fetchMyMarketPostDetail(productId);

        Button seeMoreButton = findViewById(R.id.seeMoreButton);
        seeMoreButton.setOnClickListener(v -> {
            Intent commentIntent = new Intent(MyMarketDetailActivity.this, MyMarketCommentActivity.class);
            commentIntent.putExtra("PRODUCT_ID", productId);
            startActivity(commentIntent);
        });

    }


    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        sliderAdapter = new ImageSliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        viewPager.setPageTransformer(new MarginPageTransformer(40));

        // Setup navigation arrows
        ImageView leftArrow = findViewById(R.id.leftArrow);
        ImageView rightArrow = findViewById(R.id.rightArrow);

        leftArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1);
            }
        });

        rightArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < sliderAdapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });
    }

    private void fetchMyMarketPostDetail(String productId) {
        apiService.getMarketPostDetail(productId, new ApiService.DetailCallback() {
            @Override
            public void onSuccess(PostMarketDetail postMarketDetail) {
                updateUI(postMarketDetail);
                Log.d("postMarketDetail", "onSuccess: " + postMarketDetail);
            }

            @Override
            public void onError() {
                Log.e(TAG, "Failed to fetch market post detail.");
                // Handle error - show toast or error message
            }
        });
    }


    private void updateUI(PostMarketDetail detail) {
        TextView nameTextView = findViewById(R.id.artNameTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView priceTextView = findViewById(R.id.priceTextView);
        TextView colorTextView = findViewById(R.id.colorTextView);
        TextView sizeTextView = findViewById(R.id.sizeTextView);
        TextView ageTextView = findViewById(R.id.ageTextView);
        TextView typeTextView = findViewById(R.id.typeTextView);
        Button callButton = findViewById(R.id.callButton);
        TextView addressTextView = findViewById(R.id.sellerAddress);
        TextView createAtTextView = findViewById(R.id.createAt);

        nameTextView.setText(detail.getProductName());
        descriptionTextView.setText(detail.getDescription());
        priceTextView.setText(String.format(Locale.getDefault(), "₫%,.0f", detail.getPrice()));
        colorTextView.setText(String.format("Color: %s", detail.getColor()));
        sizeTextView.setText(String.format("Size: %s", detail.getSize()));
        ageTextView.setText(String.format("Age: %s", detail.getOld()));
        typeTextView.setText(String.format("Type: %s", detail.getType()));
        addressTextView.setText(String.format("Address: %s", detail.getSellerAddress()));
        String createAt = detail.getCreatedAt();
        if (createAt != null) {
            String formattedDate = formatDate(createAt);
            createAtTextView.setText(formattedDate);
        } else {
            createAtTextView.setText("N/A");
        }

        if (detail.getListImage() != null && !detail.getListImage().isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (PostMarketDetail.ImageData imageData : detail.getListImage()) {
                imageUrls.add(imageData.getFilePath());
            }
            sliderAdapter.setImageUrls(imageUrls);
        }

        final String phoneNumber = detail.getPhoneNumber();
        callButton.setOnClickListener(v -> {
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                String contactNumber = "tel:" + phoneNumber;
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(contactNumber));
                startActivity(callIntent);
            }
        });
    }

    private String formatDate(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Trả về chuỗi gốc nếu có lỗi
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        myMarketViewModel.refreshMarketPosts();
        return true;
    }
}

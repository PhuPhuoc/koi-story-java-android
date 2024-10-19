package com.koistorynew.ui.market;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.koistorynew.R;
import com.koistorynew.ui.blog.BlogCommentActivity;
import com.koistorynew.ui.blog.BlogDetailsActivity;
import com.koistorynew.ui.market.adapter.ImageSliderAdapter;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class MarketDetailsActivity extends AppCompatActivity {
    private Handler sliderHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_market_detail);

        // Thiết lập nút back trên ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Market Details");

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String productName = intent.getStringExtra("PRODUCT_NAME");
        String productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION");
        String productImage = intent.getStringExtra("PRODUCT_IMAGE");
        String productPrice = intent.getStringExtra("PRODUCT_PRICE");

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TextView nameTextView = findViewById(R.id.artNameTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView priceTextView = findViewById(R.id.priceTextView);
        Button callButton = findViewById(R.id.callButton);
        ImageView leftArrow = findViewById(R.id.leftArrow);
        ImageView rightArrow = findViewById(R.id.rightArrow);
        TextView colorTextView = findViewById(R.id.colorTextView);
        TextView sizeTextView = findViewById(R.id.sizeTextView);
        TextView ageTextView = findViewById(R.id.ageTextView);
        TextView typeTextView = findViewById(R.id.typeTextView);

        TextView typeOtherTextView = findViewById(R.id.typeOtherTextView);
        TextView originTextView = findViewById(R.id.originTextView);
        TextView sizeOtherTextView = findViewById(R.id.sizeOtherTextView);
        TextView usageTextView = findViewById(R.id.usageTextView);


        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        viewPager.setPageTransformer(new MarginPageTransformer(40));

        nameTextView.setText(productName);
        descriptionTextView.setText(productDescription);
        priceTextView.setText(String.valueOf(productPrice));

        List<String> images = Arrays.asList(
                productImage,
                productImage,
                productImage
        );
        sliderAdapter.setImageUrls(images);

        String productType = "koi";
        if ("koi".equals(productType)) {
            colorTextView.setText("Color: red"); // Assuming these methods exist
            sizeTextView.setText("Size: 30 - 45 cm");
            ageTextView.setText("Age: 18");
            typeTextView.setText("Type: Ja"); // Assuming `getForm()` method exists

            typeOtherTextView.setVisibility(View.GONE);
            originTextView.setVisibility(View.GONE);
            sizeOtherTextView.setVisibility(View.GONE);
            usageTextView.setVisibility(View.GONE);
        } else {
            typeOtherTextView.setText("Type: food");
            originTextView.setText("Ja");
            sizeOtherTextView.setText("Size: small");
            usageTextView.setText("Usage: None");

            colorTextView.setVisibility(View.GONE);
            sizeTextView.setVisibility(View.GONE);
            ageTextView.setVisibility(View.GONE);
            typeTextView.setVisibility(View.GONE);
        }


        callButton.setOnClickListener(v -> {
            String contactNumber = "tel:" + "123456789";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(contactNumber));
            startActivity(callIntent);
        });

        leftArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1);
            }
        });

        rightArrow.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(currentItem + 1);
        });
        // Set up the "View Comments" button
        Button viewCommentsButton = findViewById(R.id.seeMoreButton);
        viewCommentsButton.setOnClickListener(v -> {
            Intent commentIntent = new Intent(MarketDetailsActivity.this, MarketCommentActivity.class);
            commentIntent.putExtra("BLOG_TITLE", productName);
            startActivity(commentIntent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Quay lại trang trước đó
        return true;
    }
}

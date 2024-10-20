package com.koistorynew.ui.mymarket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.koistorynew.ApiService;
import com.koistorynew.R;
import com.koistorynew.ui.mymarket.adapter.MyMarketAdapter;
import com.koistorynew.ui.mymarket.model.MarketImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class EditImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewImages;
    private MyMarketAdapter adapter;
    private List<MarketImage> imageList = new ArrayList<>();
    private MyMarketViewModel myMarketViewModel;
    private ApiService apiService;
    private RequestQueue requestQueue;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_images);

        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        Button uploadButton = findViewById(R.id.uploadButton);
        Intent intentId = getIntent();
        String productId = intentId.getStringExtra("ID");
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new MyMarketAdapter(this, imageList, itemId -> {});

        recyclerViewImages.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Images");
        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);
        fetchMarketData(productId);

        myMarketViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyMarketViewModel(EditImagesActivity.this);
            }
        }).get(MyMarketViewModel.class);

        // Change the Intent to allow only single image selection
        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
    }

    // Handle the result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData(); // Only one image is selected
            if (imageUri != null) {
                addImageToContainer(imageUri);
            }
        }
    }

    private void fetchMarketData(String productId) {
        String url = "http://api.koistory.site/api/v1/markets/" + productId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray imagesArray = response.optJSONArray("images");

                        if (imagesArray == null || imagesArray.length() == 0) {
                            Toast.makeText(EditImagesActivity.this, "No images available", Toast.LENGTH_SHORT).show();
                            return; // Không có hình ảnh nào, dừng hàm
                        }

                        // Lặp qua mảng hình ảnh
                        for (int i = 0; i < imagesArray.length(); i++) {
                            JSONObject imageObject = imagesArray.getJSONObject(i);
                            String filePath = imageObject.getString("file_path");

                            // Thêm filePath vào imageList
                            imageList.add(new MarketImage(filePath));
                        }
                        // Cập nhật adapter sau khi lấy dữ liệu
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(EditImagesActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FetchError", "Error: " + error.getMessage());
                    Toast.makeText(EditImagesActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        // Thêm yêu cầu vào hàng đợi
        requestQueue.add(jsonObjectRequest);
    }



    private void addImageToContainer(Uri imageUri) {
        // Layout and display logic for each selected image
        LinearLayout imageContainer = findViewById(R.id.image_container); // Make sure to have this in your layout
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load image with Glide
        Glide.with(this)
                .load(imageUri)
                .override(200, 200) // Set size limits
                .into(imageView);

        ImageButton closeButton = new ImageButton(this);
        closeButton.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
        closeButton.setBackgroundResource(android.R.color.transparent);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);

        // Remove image functionality
        closeButton.setOnClickListener(v -> {
            imageContainer.removeView((View) v.getParent());
            imageList.remove(imageUri); // Update the image list accordingly
            Toast.makeText(EditImagesActivity.this, "Image removed", Toast.LENGTH_SHORT).show();
        });

        // Add image view and close button to a parent layout
        LinearLayout frameLayout = new LinearLayout(this);
        frameLayout.setOrientation(LinearLayout.VERTICAL);
        frameLayout.addView(imageView);
        frameLayout.addView(closeButton);
        imageContainer.addView(frameLayout);
    }

    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {
        try {
            // Get the dimensions of the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Only get size
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

            // Calculate sample size
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false; // Get the actual bitmap

            // Decode the bitmap with sample size
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate largest inSampleSize value that is a power of 2
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

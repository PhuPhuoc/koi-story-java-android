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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.koistorynew.ApiService;
import com.koistorynew.R;
import com.koistorynew.UserSessionManager;
import com.koistorynew.ui.market.model.PostMarketDetail;
import com.koistorynew.ui.mymarket.model.PostMarketRequest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class EditMarketActivity extends AppCompatActivity {
    private EditText editTextName, editTextPostType, editTextColor, editTextOld, editTextAddress,
            editTextSize, editTextTitle, editTextProductType, editTextType, editTextPhone,
            editTextPrice, editTextDescription;
    private Button editButton;
    private LinearLayout imageContainer; // Container for selected images
    private List<Uri> selectedImages;
    private ApiService apiService;
    private RequestQueue requestQueue;
    private MyMarketViewModel myMarketViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_market);
        String id = UserSessionManager.getInstance().getFbUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Market Details");
        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);
        Intent intentId = getIntent();
        String productId = intentId.getStringExtra("ID");

        fetchMyMarketPostDetail(productId);

        myMarketViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyMarketViewModel(EditMarketActivity.this);
            }
        }).get(MyMarketViewModel.class);

        String itemId = getIntent().getStringExtra("ITEM_ID");
        if (itemId != null) {
            Log.d("EditMarketActivity", "Editing item with ID: " + itemId);
        }

        editTextName = findViewById(R.id.edit_text_name);
        editTextPostType = findViewById(R.id.edit_text_post_type);
        editTextColor = findViewById(R.id.edit_text_color);
        editTextOld = findViewById(R.id.edit_text_old);
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextSize = findViewById(R.id.edit_text_size);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextProductType = findViewById(R.id.edit_text_product_type);
        editTextType = findViewById(R.id.edit_text_type);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextDescription = findViewById(R.id.edit_text_description);
        editButton = findViewById(R.id.button_edit);
        imageContainer = findViewById(R.id.image_container);
        selectedImages = new ArrayList<>();

        // Open intent to pick multiple images from the gallery
        ActivityResultLauncher<Intent> pickImagesLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Get the list of selected images
                        if (result.getData().getClipData() != null) {
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                selectedImages.add(imageUri);  // Add to the list
                                addImageToContainer(imageUri); // Display the image
                            }
                        } else if (result.getData().getData() != null) {
                            Uri imageUri = result.getData().getData();
                            selectedImages.add(imageUri);  // Add single image
                            addImageToContainer(imageUri); // Display the image
                        }
                        Toast.makeText(EditMarketActivity.this, "Selected " + selectedImages.size() + " images", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        editButton.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String postType = editTextPostType.getText().toString().trim();
            String color = editTextColor.getText().toString().trim();
            String old = editTextOld.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String size = editTextSize.getText().toString().trim();
            String title = editTextTitle.getText().toString().trim();
            String productType = editTextProductType.getText().toString().trim();
            String type = editTextType.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String priceStr = editTextPrice.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(EditMarketActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert price to integer
            int price;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(EditMarketActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create PostMarketRequest object
            PostMarketRequest request = new PostMarketRequest();
            request.setProductName(name);
            request.setPrice(price);
            request.setDescription(description);
            request.setPostType(postType);
            request.setColor(color);
            request.setOld(old);
            request.setSellerAddress(address);
            request.setSize(size);
            request.setTitle(title);
            request.setProductType(productType);
            request.setType(type);
            request.setPhoneNumber(phone);

            apiService.updateMarketPost(productId, request, new ApiService.DataCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(EditMarketActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity or navigate back
                }

                @Override
                public void onError() {
                    Toast.makeText(EditMarketActivity.this, "Error updating product", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void addImageToContainer(Uri imageUri) {
        // FrameLayout để chứa cả ảnh và nút "X"
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        frameLayout.setPadding(8, 8, 8, 8); // Khoảng cách giữa các ảnh

        // ImageView để hiển thị ảnh
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Load ảnh vào ImageView sử dụng Glide
        Glide.with(this)
                .load(imageUri)
                .override(200, 200) // Giới hạn kích thước
                .into(imageView);

        // Nút ImageButton "X" để xóa ảnh
        ImageButton closeButton = new ImageButton(this);
        FrameLayout.LayoutParams closeButtonParams = new FrameLayout.LayoutParams(60, 60);
        closeButtonParams.gravity = Gravity.TOP | Gravity.END;
        closeButton.setLayoutParams(closeButtonParams);
        closeButton.setBackgroundResource(android.R.color.transparent); // Xóa nền mặc định của nút
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // Icon nút "X"

        // Xử lý khi nhấn vào nút "X" để xóa ảnh
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa ảnh khỏi container và danh sách URI
                imageContainer.removeView(frameLayout);
                selectedImages.remove(imageUri);
                Toast.makeText(EditMarketActivity.this, "Image removed", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm ImageView và nút "X" vào FrameLayout
        frameLayout.addView(imageView);
        frameLayout.addView(closeButton);

        // Thêm FrameLayout vào container
        imageContainer.addView(frameLayout);
    }

    // Method to decode and sample bitmap from URI
    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {
        try {
            // First, get the dimensions of the image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Only get the size
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false; // Get the actual bitmap

            // Decode the bitmap with the inSampleSize
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to calculate the sample size
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void fetchMyMarketPostDetail(String productId) {
        apiService.getMarketPostDetail(productId, new ApiService.DetailCallback() {
            @Override
            public void onSuccess(PostMarketDetail postMarketDetail) {
                PostMarketRequest postMarketData = convertToPostMarketRequest(postMarketDetail);

                runOnUiThread(() -> {
                    try {
                        editTextName.setText(postMarketData.getProductName());
                        editTextPostType.setText(postMarketData.getPostType());
                        editTextColor.setText(postMarketData.getColor());
                        editTextOld.setText(postMarketData.getOld());
                        editTextAddress.setText(postMarketData.getSellerAddress());
                        editTextSize.setText(postMarketData.getSize());
                        editTextTitle.setText(postMarketData.getTitle());
                        editTextProductType.setText(postMarketData.getProductType());
                        editTextType.setText(postMarketData.getType());
                        editTextPhone.setText(postMarketData.getPhoneNumber());
                        editTextPrice.setText(String.valueOf((int) postMarketData.getPrice()));
                        editTextDescription.setText(postMarketData.getDescription());
                        myMarketViewModel.refreshMarketPosts();

                    } catch (Exception e) {
                        Log.e("EditMarketActivity", "Error setting data: " + e.getMessage());
                        Toast.makeText(EditMarketActivity.this, "Error loading post details", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Log.e("EditMarketActivity", "Failed to fetch post details");
                    Toast.makeText(EditMarketActivity.this,
                            "Failed to fetch post details", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private PostMarketRequest convertToPostMarketRequest(PostMarketDetail detail) {
        PostMarketRequest request = new PostMarketRequest();
        request.setId(detail.getId());
        request.setUserId(detail.getUserId());
        request.setPostType(detail.getPostType());
        request.setTitle(detail.getTitle());
        request.setCreatedAt(detail.getCreatedAt());
        request.setProductName(detail.getProductName());
        request.setProductType(detail.getProductType());
        request.setPrice(detail.getPrice());
        request.setSellerAddress(detail.getSellerAddress());
        request.setPhoneNumber(detail.getPhoneNumber());
        request.setDescription(detail.getDescription());
        request.setColor(detail.getColor());
        request.setSize(detail.getSize());
        request.setOld(detail.getOld());
        request.setType(detail.getType());

        // Chuyển đổi danh sách ảnh
        if (detail.getListImage() != null) {
            List<String> imageDataList = new ArrayList<>();
            // Thực hiện chuyển đổi tùy theo cấu trúc của PostMarketDetail
            request.setListImage(imageDataList);
        }

        return request;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Quay lại trang trước đó
        myMarketViewModel.refreshMarketPosts();
        Log.d("tesssssss", "Refreshing market posts on back navigation");
        return true;
    }
}

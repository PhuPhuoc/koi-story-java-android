package com.koistorynew.ui.mymarket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import com.bumptech.glide.Glide;
import com.koistorynew.R;
import com.koistorynew.ui.market.MarketDetailsActivity;
import com.koistorynew.ui.market.MarketViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class AddMarketActivity extends AppCompatActivity {

    private EditText editTextName, editTextPostType, editTextColor, editTextOld, editTextAddress,
            editTextSize, editTextTitle, editTextProductType, editTextType, editTextPhone,
            editTextPrice, editTextDescription;
    private Button uploadImageButton, submitButton;
    private LinearLayout imageContainer; // Container for selected images
    private List<Uri> selectedImages;  // List of selected images
    private MyMarketViewModel myMarketViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_market);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Market Details");

//        myMarketViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
//            @NonNull
//            @Override
//            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//                return (T) new MyMarketViewModel(AddMarketActivity.this);
//            }
//        }).get(MyMarketViewModel.class);

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
        uploadImageButton = findViewById(R.id.button_upload_image);
        submitButton = findViewById(R.id.button_submit);
        imageContainer = findViewById(R.id.image_container); // Initialize image container
        selectedImages = new ArrayList<>();  // Initialize the selected images list

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
                        Toast.makeText(AddMarketActivity.this, "Selected " + selectedImages.size() + " images", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Handle the "Upload Image" button click
        uploadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);  // Allow multiple image selection
            pickImagesLauncher.launch(Intent.createChooser(intent, "Select Pictures"));
        });

        // Handle the "Submit" button click
        submitButton.setOnClickListener(v -> {
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
                Toast.makeText(AddMarketActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert price to integer
            int price;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(AddMarketActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create JSON object
            JSONObject requestData = new JSONObject();
            try {
                requestData.put("product_name", name);
                requestData.put("price", price);
                requestData.put("description", description);

                // Optional fields - using empty string if not provided
                requestData.put("post_type", postType.isEmpty() ? "" : postType);
                requestData.put("color", color.isEmpty() ? "" : color);
                requestData.put("old", old.isEmpty() ? "" : old);
                requestData.put("seller_address", address.isEmpty() ? "" : address);
                requestData.put("size", size.isEmpty() ? "" : size);
                requestData.put("title", title.isEmpty() ? "" : title);
                requestData.put("product_type", productType.isEmpty() ? "" : productType);
                requestData.put("type", type.isEmpty() ? "" : type);
                requestData.put("phone_number", phone.isEmpty() ? "" : phone);

                // System fields
                requestData.put("created_at", String.valueOf(System.currentTimeMillis()));
                requestData.put("user_id", "user123"); // Replace with actual user ID

                // Handle images
                JSONArray imageList = new JSONArray();
                for (Uri imageUri : selectedImages) {
                    imageList.put(imageUri.toString());
                }
                requestData.put("list_image", imageList);

                Log.d("AddMarket", "=== Request Data Details ===");
                Log.d("AddMarket", "Product Name: " + name);
                Log.d("AddMarket", "Post Type: " + postType);
                Log.d("AddMarket", "Color: " + color);
                Log.d("AddMarket", "Old: " + old);
                Log.d("AddMarket", "Address: " + address);
                Log.d("AddMarket", "Size: " + size);
                Log.d("AddMarket", "Title: " + title);
                Log.d("AddMarket", "Product Type: " + productType);
                Log.d("AddMarket", "Type: " + type);
                Log.d("AddMarket", "Phone: " + phone);
                Log.d("AddMarket", "Price: " + price);
                Log.d("AddMarket", "Description: " + description);
                Log.d("AddMarket", "Number of Images: " + selectedImages.size());
                Log.d("AddMarket", "========================");
                Log.d("AddMarket", "Full JSON: " + requestData.toString(2));

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(AddMarketActivity.this, "Error creating JSON", Toast.LENGTH_SHORT).show();
            }

            // Log the entire JSON data
            Log.d("ProductData", requestData.toString());


            // TODO: Call your API here
            // apiService.createMarketPost(requestData, new ApiService.DataCallback<String>() {
            //     @Override
            //     public void onSuccess(String response) {
            //         runOnUiThread(() -> {
            //             Toast.makeText(AddMarketActivity.this, "Product submitted successfully", Toast.LENGTH_SHORT).show();
            //             finish();
            //         });
            //     }
            //
            //     @Override
            //     public void onError() {
            //         runOnUiThread(() -> {
            //             Toast.makeText(AddMarketActivity.this, "Failed to submit product", Toast.LENGTH_SHORT).show();
            //         });
            //     }
            // });

            // Handle submit information here
            Toast.makeText(AddMarketActivity.this, "Product Submitted", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddMarketActivity.this, "Image removed", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Quay lại trang trước đó
//        myMarketViewModel.refreshMarketPosts();
//        Log.d("tesssssss", "Refreshing market posts on back navigation");
        return true;
    }
}

package com.koistorynew.ui.mymarket;

import android.app.ProgressDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.koistorynew.ApiService;
import com.koistorynew.R;
import com.koistorynew.UserSessionManager;
import com.koistorynew.ui.market.MarketDetailsActivity;
import com.koistorynew.ui.market.MarketViewModel;
import com.koistorynew.ui.mymarket.model.PostMarketRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AddMarketActivity extends AppCompatActivity {

    private EditText editTextName, editTextPostType, editTextColor, editTextOld, editTextAddress,
            editTextSize, editTextTitle, editTextProductType, editTextType, editTextPhone,
            editTextPrice, editTextDescription;
    private Button uploadImageButton, submitButton;
    private LinearLayout imageContainer; // Container for selected images
    private List<Uri> selectedImages;  // List of selected images
    private MyMarketViewModel myMarketViewModel;
    private ApiService apiService;
    private RequestQueue requestQueue;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_market);
        String id = UserSessionManager.getInstance().getFbUid();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add market post");
        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        myMarketViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyMarketViewModel(AddMarketActivity.this);
            }
        }).get(MyMarketViewModel.class);

        editTextColor = findViewById(R.id.edit_text_color);
        editTextDescription = findViewById(R.id.edit_text_description);
        selectedImages = new ArrayList<>();
        editTextOld = findViewById(R.id.edit_text_old);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextPostType = findViewById(R.id.edit_text_post_type);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextName = findViewById(R.id.edit_text_name);
        editTextProductType = findViewById(R.id.edit_text_product_type);
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextSize = findViewById(R.id.edit_text_size);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextType = findViewById(R.id.edit_text_type);
        uploadImageButton = findViewById(R.id.button_upload_image);
        submitButton = findViewById(R.id.button_submit);
        imageContainer = findViewById(R.id.image_container);

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

            if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty() || selectedImages.isEmpty()) {
                Toast.makeText(AddMarketActivity.this, "Please fill in all required fields and upload images", Toast.LENGTH_SHORT).show();
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(AddMarketActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImagesAndSubmit(name, postType, color, old, address, size, title, productType, type, phone, price, description, id);

            // Handle submit information here
            Toast.makeText(AddMarketActivity.this, "Product Submitted", Toast.LENGTH_SHORT).show();
        });
    }


    private void uploadImagesAndSubmit(String name, String postType, String color, String old,
                                       String address, String size, String title, String productType, String type,
                                       String phone, int price, String description, String userId) {

        List<String> imageUrls = new ArrayList<>();
        AtomicInteger uploadedCount = new AtomicInteger(0);

        // Hiển thị loading dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading images...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        for (Uri imageUri : selectedImages) {
            // Upload trực tiếp với tên file là thời gian hiện tại
            StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                    .child("images/" + System.currentTimeMillis() + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Lấy download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());

                            // Log the download URL
                            Log.d("ImageUpload", "Image uploaded successfully: " + uri.toString());

                            // Kiểm tra nếu đã upload xong tất cả ảnh
                            if (uploadedCount.incrementAndGet() == selectedImages.size()) {
                                progressDialog.dismiss();
                                submitDataToApi(name, postType, color, old, address, size,
                                        title, productType, type, phone, price, description,
                                        userId, imageUrls);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddMarketActivity.this,
                                "Failed to upload image: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading: " + (int)progress + "%");
                    });
        }
    }


    private void submitDataToApi(String name, String postType, String color, String old,
                                 String address, String size, String title, String productType,
                                 String type, String phone, int price, String description,
                                 String userId, List<String> imageUrls) {

        PostMarketRequest request = new PostMarketRequest();
        request.setProductName(name);
        request.setPrice(price);
        request.setDescription(description);
        request.setPostType(postType.isEmpty() ? "" : postType);
        request.setColor(color.isEmpty() ? "" : color);
        request.setOld(old.isEmpty() ? "" : old);
        request.setSellerAddress(address.isEmpty() ? "" : address);
        request.setSize(size.isEmpty() ? "" : size);
        request.setTitle(title.isEmpty() ? "" : title);
        request.setProductType(productType.isEmpty() ? "" : productType);
        request.setType(type.isEmpty() ? "" : type);
        request.setPhoneNumber(phone.isEmpty() ? "" : phone);
        request.setUserId(userId);
        request.setListImage(imageUrls);

        apiService.createMarketPost(request, new ApiService.DataCallback<String>() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(AddMarketActivity.this,
                            "Product submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Toast.makeText(AddMarketActivity.this,
                            "Failed to submit product", Toast.LENGTH_SHORT).show();
                });
            }
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
        onBackPressed();
        return true;
    }
}

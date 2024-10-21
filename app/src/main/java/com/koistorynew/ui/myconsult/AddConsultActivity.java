package com.koistorynew.ui.myconsult;

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
import com.koistorynew.ApiService;
import com.koistorynew.R;
import com.koistorynew.UserSessionManager;
import com.koistorynew.ui.myconsult.model.AddConsult;
import com.koistorynew.ui.mymarket.AddMarketActivity;
import com.koistorynew.ui.mymarket.MyMarketViewModel;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AddConsultActivity extends AppCompatActivity {
    private EditText addTextContent, addTextTitle, addPostType;
    private Button uploadImageButton, submitButton;
    private LinearLayout imageContainer; // Container for selected images
    private Uri selectedImageUri;
    // List of selected images
    private MyConsultViewModel myConsultViewModel;
    private ApiService apiService;
    private RequestQueue requestQueue;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_consult);
        String id = UserSessionManager.getInstance().getFbUid();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Consult");
        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        myConsultViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyConsultViewModel(AddConsultActivity.this);
            }
        }).get(MyConsultViewModel.class);

        addTextContent = findViewById(R.id.add_content);
        addTextTitle = findViewById(R.id.add_title);
        addPostType = findViewById(R.id.add_post_type);
        uploadImageButton = findViewById(R.id.button_upload_image);
        submitButton = findViewById(R.id.button_submit);
        imageContainer = findViewById(R.id.image_container);

        // Open intent to pick multiple images from the gallery
        ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedImageUri = imageUri;
                            addImageToContainer(imageUri); // Hiển thị ảnh
                            Toast.makeText(AddConsultActivity.this, "Selected an image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        // Handle the "Upload Image" button click
        uploadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(Intent.createChooser(intent, "Select a Picture"));
        });


        submitButton.setOnClickListener(v -> {
            String title = addTextTitle.getText().toString().trim();
            String post_type = addPostType.getText().toString().trim();
            String content = addTextContent.getText().toString().trim();

            if (title.isEmpty() || post_type.isEmpty()  || content.isEmpty()) {
                Toast.makeText(AddConsultActivity.this, "Please fill in all required fields and upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImageAndSubmit(title, post_type, content, id);
        });
    }


    private void uploadImageAndSubmit(String title, String post_type, String content, String userId) {
        if (selectedImageUri == null) {
            Toast.makeText(AddConsultActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference imageRef = FirebaseStorage.getInstance().getReference()
                .child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        progressDialog.dismiss();
                            submitDataToApi(title, post_type, content, userId, selectedImageUri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddConsultActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading: " + (int) progress + "%");
                });
    }


    private void submitDataToApi(String title, String post_type,String content,
                                 String userId, String imageUrls) {

        AddConsult request = new AddConsult();
        request.setTitle(title);
        request.setPost_type(post_type);
        request.setContent(content);
        request.setFile_path(imageUrls);

        apiService.createConsult(request, new ApiService.DataCallback<String>() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(AddConsultActivity.this,
                            "Product submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Toast.makeText(AddConsultActivity.this,
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
                // Remove the image from the container and set the Uri to null
                imageContainer.removeView(frameLayout);
                selectedImageUri = null; // Set the Uri to null
                Toast.makeText(AddConsultActivity.this, "Image removed", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm ImageView và nút "X" vào FrameLayout
        frameLayout.addView(imageView);
        frameLayout.addView(closeButton);

        // Thêm FrameLayout vào container
        imageContainer.addView(frameLayout);
    }

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

package com.koistorynew.ui.myconsult;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.koistorynew.ApiService;
import com.koistorynew.R;
import com.koistorynew.ui.mymarket.model.MarketImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditImagesConsultActivity extends AppCompatActivity {
    private LinearLayout imageContainer;
    private List<MarketImage> imageList = new ArrayList<>();
    private ApiService apiService;
    private RequestQueue requestQueue;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String productId, imageURL, imageID;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private boolean hasImage = false; // Track if there's already an image

    private TextView emptyStateText; // Thêm TextView để hiển thị thông báo

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consult_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Images");

        imageContainer = findViewById(R.id.image_container);
        FloatingActionButton uploadButton = findViewById(R.id.uploadButton);
        emptyStateText = findViewById(R.id.empty_state_text); // Khởi tạo TextView
        Intent intentId = getIntent();
        productId = intentId.getStringExtra("ID");
        imageURL = intentId.getStringExtra("IMAGE");
        imageID = intentId.getStringExtra("IMAGE_ID");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);

        // Fetch market data using imageURL to display existing images
        if (imageURL != null) {
            displayInitialImage(imageURL);
            uploadButton.hide(); // Ẩn nút upload nếu đã có ảnh
        } else {
            emptyStateText.setVisibility(View.VISIBLE); // Hiện hint nếu chưa có ảnh
        }

        // Setup upload button click listener
        uploadButton.setOnClickListener(v -> {
            if (hasImage) {
                Toast.makeText(this, "Please delete the existing image before uploading a new one.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                uploadImageToServer(imageUri);
            }
        }
    }

    private void uploadImageToServer(Uri imageUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create storage reference
        StorageReference imageRef = firebaseStorage.getReference()
                .child("images/" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateImageInDatabase(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditImagesConsultActivity.this,
                            "Failed to upload image: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading: " + (int) progress + "%");
                });
    }

    private void updateImageInDatabase(String imageUrl) {
        String url = "http://api.koistory.site/api/v1/post/" + productId + "/image";

        // Create JSON request body
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("file_path", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditImagesConsultActivity.this,
                            "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("UploadError", "Error: " + error.getMessage());
                    Toast.makeText(EditImagesConsultActivity.this,
                            "Error updating image in database", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void deleteImageFromApi(String productId, String imageId, FrameLayout frameLayout) {
        String url = "http://api.koistory.site/api/v1/post/" + productId + "/image/" + imageId;
        Log.d("url", "deleteImageFromApi: "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(EditImagesConsultActivity.this,
                            "Image deleted successfully", Toast.LENGTH_SHORT).show();
                    imageContainer.removeView(frameLayout);
                    hasImage = false;
                    // Hiện lại nút upload và ẩn hint
                    findViewById(R.id.uploadButton).setVisibility(View.VISIBLE);
                    emptyStateText.setVisibility(View.VISIBLE);
                },
                error -> {
                    Log.e("DeleteError", "Error: " + error.getMessage());
                    Toast.makeText(EditImagesConsultActivity.this,
                            "Error deleting image", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void addImageToContainer(MarketImage marketImage) {
        // Clear existing images first
        imageContainer.removeAllViews();
        hasImage = true; // Set flag indicating there's now an image

        // Hiện lại nút upload và ẩn hint
        findViewById(R.id.uploadButton).setVisibility(View.GONE);
        emptyStateText.setVisibility(View.GONE);

        // Create FrameLayout container
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        frameLayout.setPadding(16, 16, 16, 16);

        // Create ImageView
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(700, 700));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundResource(R.drawable.rounded_corners);

        // Load image using Glide
        Glide.with(this)
                .load(marketImage.getFilePath())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .override(400, 400)
                .into(imageView);

        ImageButton deleteButton = new ImageButton(this);
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(120, 120);
        deleteParams.setMargins(0, 16, 16, 0);
        deleteParams.gravity = android.view.Gravity.END | android.view.Gravity.TOP;
        deleteButton.setLayoutParams(deleteParams);
        deleteButton.setBackgroundResource(android.R.color.transparent);
        deleteButton.setImageResource(R.drawable.ic_close);

        deleteButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(EditImagesConsultActivity.this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteImageFromApi(productId, imageID, frameLayout);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        frameLayout.addView(imageView);
        frameLayout.addView(deleteButton);
        imageContainer.addView(frameLayout);
    }

    private void displayInitialImage(String imageUrl) {
        MarketImage marketImage = new MarketImage(imageUrl, imageID);
        addImageToContainer(marketImage);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

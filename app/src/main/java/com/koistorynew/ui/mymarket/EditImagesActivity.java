package com.koistorynew.ui.mymarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class EditImagesActivity extends AppCompatActivity {

    private LinearLayout imageContainer;
    private List<MarketImage> imageList = new ArrayList<>();
    private MyMarketViewModel myMarketViewModel;
    private ApiService apiService;
    private RequestQueue requestQueue;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String productId;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_images);

        // Initialize views and variables
        imageContainer = findViewById(R.id.image_container);
        FloatingActionButton uploadButton = findViewById(R.id.uploadButton);
        Intent intentId = getIntent();
        productId = intentId.getStringExtra("ID");

        // Setup ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Images");

        // Initialize Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        // Initialize Volley RequestQueue and ApiService
        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);

        // Fetch initial market data
        fetchMarketData(productId);

        // Initialize ViewModel
        myMarketViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyMarketViewModel(EditImagesActivity.this);
            }
        }).get(MyMarketViewModel.class);

        // Setup upload button click listener
        uploadButton.setOnClickListener(v -> {
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
                    // Get download URL after upload success
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateImageInDatabase(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditImagesActivity.this,
                            "Failed to upload image: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading: " + (int)progress + "%");
                });
    }


    private void fetchMarketData(String productId) {
        String url = "http://api.koistory.site/api/v1/markets/" + productId;

        // Clear existing images
        imageContainer.removeAllViews();
        imageList.clear();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject dataObject = response.optJSONObject("data");
                        if (dataObject == null) {
                            Toast.makeText(EditImagesActivity.this,
                                    "No market data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray imagesArray = dataObject.optJSONArray("ListImage");

                        if (imagesArray == null || imagesArray.length() == 0) {
                            Toast.makeText(EditImagesActivity.this,
                                    "No images available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Parse and add images
                        for (int i = 0; i < imagesArray.length(); i++) {
                            JSONObject imageObject = imagesArray.getJSONObject(i);
                            String filePath = imageObject.getString("file_path");
                            String imageId = imageObject.getString("id");

                            imageList.add(new MarketImage(filePath, imageId));
                        }

                        // Display fetched images
                        for (MarketImage image : imageList) {
                            addImageToContainer(image);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(EditImagesActivity.this,
                                "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FetchError", "Error: " + error.getMessage());
                    Toast.makeText(EditImagesActivity.this,
                            "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
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
                    Toast.makeText(EditImagesActivity.this,
                            "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    fetchMarketData(productId); // Refresh image list
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("UploadError", "Error: " + error.getMessage());
                    Toast.makeText(EditImagesActivity.this,
                            "Error updating image in database", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }



    private void deleteImageFromApi(String productId, String imageId, FrameLayout frameLayout) {
        String url = "http://api.koistory.site/api/v1/post/" + productId + "/image/" + imageId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null,
                response -> {
                    Toast.makeText(EditImagesActivity.this,
                            "Image deleted successfully", Toast.LENGTH_SHORT).show();
                    imageContainer.removeView(frameLayout);
                    fetchMarketData(productId); // Refresh image list
                },
                error -> {
                    Log.e("DeleteError", "Error: " + error.getMessage());
                    Toast.makeText(EditImagesActivity.this,
                            "Error deleting image", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void addImageToContainer(MarketImage marketImage) {
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

        // Create delete button
        ImageButton closeButton = new ImageButton(this);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(120, 120);
        closeParams.setMargins(0, 16, 16, 0);
        closeParams.gravity = android.view.Gravity.END | android.view.Gravity.TOP;
        closeButton.setLayoutParams(closeParams);
        closeButton.setBackgroundResource(android.R.color.transparent);
        closeButton.setImageResource(R.drawable.ic_close);
        closeButton.setColorFilter(getResources().getColor(android.R.color.white));

        closeButton.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(EditImagesActivity.this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteImageFromApi(productId, marketImage.getId(), frameLayout);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Add views to container
        frameLayout.addView(imageView);
        frameLayout.addView(closeButton);
        frameLayout.setBackgroundResource(R.drawable.shadow_background);
        imageContainer.addView(frameLayout);
    }

    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
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

            while ((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth) {
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
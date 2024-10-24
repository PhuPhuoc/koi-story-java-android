package com.koistorynew;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegister, passwordRegister, confirmPasswordRegister, userNameRegister;
    private Button registerConfirmButton;
    private RequestQueue requestQueue;
    private ImageView imageView;
    private static final int CAMERA_REQUEST_CODE = 1000;
    private static final int PERMISSION_REQUEST_CODE = 2000;
    private Uri photoUri;
    private File photoFile;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private List<Uri> photoUriList = new ArrayList<>();
    private List<File> photoFileList = new ArrayList<>();
    private static final int MAX_IMAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPasswordRegister = findViewById(R.id.confirmPasswordRegister);
        userNameRegister = findViewById(R.id.userNameRegister);
        registerConfirmButton = findViewById(R.id.registerConfirmButton);
        Button openCameraButton = findViewById(R.id.openCamera);
        imageView = findViewById(R.id.imageView);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        openCameraButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        registerConfirmButton.setOnClickListener(v -> {
            String email = emailRegister.getText().toString();
            String password = passwordRegister.getText().toString();
            String confirmPassword = confirmPasswordRegister.getText().toString();
            String userName = userNameRegister.getText().toString();
            uploadToFirebaseStorageAndRegister(email, password, userName, confirmPassword);
        });
    }

    private void uploadToFirebaseStorageAndRegister(String email, String password, String userName, String confirmPassword) {
        if (photoFileList.isEmpty()) {
            Toast.makeText(this, "Không có ảnh để tải lên", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang tải ảnh lên...");
        progressDialog.show();

        List<String> imageUrls = new ArrayList<>();
        final int[] uploadCount = {0};

        for (int i = 0; i < photoFileList.size(); i++) {
            File file = photoFileList.get(i);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String filename = "image_" + timestamp + "_" + i + ".jpg";
            StorageReference imageRef = storageRef.child("images/" + filename);

            UploadTask uploadTask = imageRef.putFile(Uri.fromFile(file));

            int finalI = i;
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    String imageUrl = "\"" + downloadUri.toString() + "\""; // Wrap the URL in quotes
                    imageUrls.add(imageUrl);
                    uploadCount[0]++;

                    if (uploadCount[0] == photoFileList.size()) {
                        registerUser(email, password, userName, confirmPassword, imageUrls);
                        progressDialog.dismiss();
                    }
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Lỗi tải lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Đã tải lên " + (int) progress + "%");
            });
        }

        progressDialog.dismiss();
    }


    private void registerUser(String email, String password, String userName, String confirmPassword, List face_image) {
        String url = "http://api.koistory.site/api/v1/users/register";
//        String url = "http://10.0.2.2:8080/api/v1/users/register";

        Log.d("RegisterData", "email: " + email + ", password: " + password +
                ", userName: " + userName + ", confirmPassword: " + confirmPassword +
                ", face_image: " + face_image);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("user_name", userName);
            jsonBody.put("confirm_password", confirmPassword);
            JSONArray jsonArray = new JSONArray(face_image);
            jsonBody.put("face_image", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang đăng kí...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assuming the response contains a "data" object
                            String message = response.getString("message");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                            // Redirect to LoginActivity or MainActivity after successful registration
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String errorMessage = jsonResponse.getString("error"); // Get error message
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed" + error, Toast.LENGTH_SHORT).show();
                            Log.e("error", "onErrorResponse: " + error);
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Default retry count
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Default backoff multiplier
        ));

        // Add request to RequestQueue
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private void openCamera() {
        if (photoUriList.size() >= MAX_IMAGES) {
            Toast.makeText(this, "Đã đạt đến giới hạn tối đa 5 ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.koistorynew.fileprovider", photoFile);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                try {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    Intent backupIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(backupIntent, CAMERA_REQUEST_CODE);
                }
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Lỗi khi tạo file: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(getExternalFilesDir(null), "CapturedImages");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (photoUriList.size() >= MAX_IMAGES) {
                Toast.makeText(this, "Đã đạt đến giới hạn tối đa 5 ảnh!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (photoUri != null && photoFile.exists()) {
                    photoUriList.add(photoUri);
                    photoFileList.add(photoFile);
                    imageView.setImageURI(null);
                    imageView.setImageURI(photoUri);

                    Toast.makeText(this, "Ảnh đã được chụp! Bạn có thể chụp thêm.", Toast.LENGTH_SHORT).show();

                    if (photoUriList.size() < MAX_IMAGES) {
                        openCamera(); // Automatically reopen camera until 5 images are captured
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi hiển thị ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterActivityV1 extends AppCompatActivity {

    private EditText emailRegister, passwordRegister, confirmPasswordRegister, userNameRegister;
    private Button registerConfirmButton;
    private static final int CAMERA_REQUEST_CODE = 1000;
    private static final int PERMISSION_REQUEST_CODE = 2000;
    private ImageView imageView;
    private Uri photoUri;
    private File photoFile;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imageView = findViewById(R.id.imageView);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPasswordRegister = findViewById(R.id.confirmPasswordRegister);
        userNameRegister = findViewById(R.id.userNameRegister);
        registerConfirmButton = findViewById(R.id.registerConfirmButton);
        Button openCameraButton = findViewById(R.id.openCamera);
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
            uploadToFirebaseStorage(email, password, userName, confirmPassword);
        });
    }

    private void uploadToFirebaseStorage(String email, String password, String confirm, String userName) {
        if (photoFile == null || !photoFile.exists()) {
            Toast.makeText(this, "Vui lòng chụp ảnh trước", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang tải lên...");
        progressDialog.show();

        // Tạo tên file unique dựa trên timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String filename = "image_" + timestamp + ".jpg";

        // Tạo reference đến file trên Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + filename);

        // Upload file
        UploadTask uploadTask = imageRef.putFile(Uri.fromFile(photoFile));

        // Theo dõi tiến trình upload
        uploadTask.addOnProgressListener(taskSnapshot -> {
            // Tính phần trăm progress
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            progressDialog.setMessage("Đã tải lên " + (int) progress + "%");
        }).addOnSuccessListener(taskSnapshot -> {
            // Upload thành công
            progressDialog.dismiss();

            // Lấy download URL
            imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                String imageUrl = downloadUri.toString();
                Log.d("Firebase", "Upload success. URL: " + imageUrl);
                registerUser(email, password, confirm, userName, imageUrl);
                Toast.makeText(RegisterActivityV1.this,
                        "Tải lên thành công!",
                        Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Upload thất bại
            progressDialog.dismiss();
            Log.e("Firebase", "Upload failed", e);
            Toast.makeText(RegisterActivityV1.this,
                    "Lỗi khi tải lên: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
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
        try {
            photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        "com.koistorynew.fileprovider",
                        photoFile);

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                try {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                } catch (Exception e) {
                    // Nếu không thể mở intent camera thông thường, thử phương án backup
                    Intent backupIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(backupIntent, CAMERA_REQUEST_CODE);
                }
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Lỗi khi tạo file: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Sử dụng getExternalFilesDir() để lưu trong thư mục ứng dụng
        File storageDir = new File(getExternalFilesDir(null), "CapturedImages");

        // Đảm bảo thư mục tồn tại
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Tạo file tạm thời
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",        /* suffix */
                storageDir     /* directory */
        );

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                if (photoUri != null && photoFile.exists()) {
                    // Nếu chụp ảnh full resolution thành công
                    imageView.setImageURI(null);
                    imageView.setImageURI(photoUri);
                } else if (data != null && data.getExtras() != null) {
                    // Fallback cho thumbnail
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(thumbnail);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi hiển thị ảnh: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Cần cấp quyền camera để sử dụng tính năng này",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerUser(String email, String password, String userName, String confirmPassword, String imageUrl) {
//        String url = "http://api.koistory.site/api/v1/users/register";
        String url = "http://10.0.2.2:8080/api/v1/users/register";


        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("user_name", userName);
            jsonBody.put("confirm_password", confirmPassword);
            jsonBody.put("face_image", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivityV1.this, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return; // Exit early on error
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang đăng kí...");
        progressDialog.show();
        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assuming the response contains a "data" object
                            String message = response.getString("message");
                            Toast.makeText(RegisterActivityV1.this, message, Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                            // Redirect to LoginActivity or MainActivity after successful registration
                            Intent intent = new Intent(RegisterActivityV1.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivityV1.this, "Error parsing response", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(RegisterActivityV1.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivityV1.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivityV1.this, "Registration Failed", Toast.LENGTH_SHORT).show();
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


}

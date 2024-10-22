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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CameraActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1000;
    private static final int PERMISSION_REQUEST_CODE = 2000;
    private ImageView imageView;
    private Uri photoUri;
    private File photoFile;
    private FirebaseStorage storage;
    private StorageReference storageRef;

//    private void uploadImage() {
//        if (photoFile == null || !photoFile.exists()) {
//            Toast.makeText(this, "Vui lòng chụp ảnh trước", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        new Thread(() -> {
//            try {
//                // Tạo JSON object cho request body
//                JSONObject jsonBody = new JSONObject();
//                // Thêm các trường cần thiết vào jsonBody
//                // jsonBody.put("username", username);
//                // jsonBody.put("password", password);
//
//                // Tạo RequestBody cho phần JSON
//                RequestBody requestBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("json", jsonBody.toString())
//                        .addFormDataPart("image", photoFile.getName(),
//                                RequestBody.create(MediaType.parse("image/jpeg"), photoFile))
//                        .build();
//
//                // Tạo request
//                Request request = new Request.Builder()
//                        .url("YOUR_API_ENDPOINT")
//                        .post(requestBody)
//                        .build();
//
//                // Tạo OkHttpClient với timeout
//                OkHttpClient client = new OkHttpClient.Builder()
//                        .connectTimeout(30, TimeUnit.SECONDS)
//                        .writeTimeout(30, TimeUnit.SECONDS)
//                        .readTimeout(30, TimeUnit.SECONDS)
//                        .build();
//
//                // Thực hiện request
//                try (Response response = client.newCall(request).execute()) {
//                    final String responseBody = response.body().string();
//                    runOnUiThread(() -> {
//                        if (response.isSuccessful()) {
//                            Toast.makeText(CameraActivity.this,
//                                    "Upload thành công!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(CameraActivity.this,
//                                    "Upload thất bại: " + responseBody, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                runOnUiThread(() -> {
//                    Toast.makeText(CameraActivity.this,
//                            "Lỗi khi upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//            }
//        }).start();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = findViewById(R.id.imageView);
        Button loginButton = findViewById(R.id.buttonCapture);
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

        loginButton.setOnClickListener(v -> {
            if (photoFile != null && photoFile.exists()) {
                uploadToFirebaseStorage();
                Toast.makeText(CameraActivity.this,
                        "Vui lòng chụp ảnh trước",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadToFirebaseStorage() {
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

                Toast.makeText(CameraActivity.this,
                        "Tải lên thành công!",
                        Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Upload thất bại
            progressDialog.dismiss();
            Log.e("Firebase", "Upload failed", e);
            Toast.makeText(CameraActivity.this,
                    "Lỗi khi tải lên: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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
}
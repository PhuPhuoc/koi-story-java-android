package com.koistorynew.ui.myconsult;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.koistorynew.ApiService;
import com.koistorynew.R;
import com.koistorynew.UserSessionManager;
import com.koistorynew.ui.myconsult.model.AddConsult;


public class EditConsultActivity extends AppCompatActivity {
    private EditText editContent, editPostType, editTitle, editFilePath;
    private Button editButton;
    private LinearLayout imageContainer; // Container for selected images
    private Uri selectedImageUri;
    private ApiService apiService;
    private RequestQueue requestQueue;
    private MyConsultViewModel myConsultViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consult);
        String id = UserSessionManager.getInstance().getFbUid();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Consult");

        Intent intentId = getIntent();
        String productId = intentId.getStringExtra("CONSULT_ID");
        String titleConsult = intentId.getStringExtra("CONSULT_TITLE");
        String contentConsult = intentId.getStringExtra("CONSULT_CONTENT");
        String postTypeConsult = intentId.getStringExtra("CONSULT_POST_TYPE");
        String filePathConsult = intentId.getStringExtra("CONSULT_FILE_PATH");

        requestQueue = Volley.newRequestQueue(this);
        apiService = new ApiService(requestQueue);

        myConsultViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MyConsultViewModel(EditConsultActivity.this);
            }
        }).get(MyConsultViewModel.class);

        editContent = findViewById(R.id.add_content);
        editTitle = findViewById(R.id.add_title);
        editPostType = findViewById(R.id.add_post_type);
        editButton = findViewById(R.id.button_submit);

        editContent.setText(contentConsult);
        editTitle.setText(titleConsult);
        editPostType.setText(postTypeConsult);
        editButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String post_type = editPostType.getText().toString().trim();
            String content = editContent.getText().toString().trim();
            if (title.isEmpty() || post_type.isEmpty() || content.isEmpty()) {
                Toast.makeText(EditConsultActivity.this, "Please fill in all required fields and upload an image", Toast.LENGTH_SHORT).show();
                return;
            }

            submitDataToApi(title, post_type, content, productId, filePathConsult);
        });
    }

    private void submitDataToApi(String title, String post_type, String content,
                                 String productId, String imageUrls) {

        AddConsult request = new AddConsult();
        request.setTitle(title);
        request.setPost_type(post_type);
        request.setContent(content);
        request.setFile_path(imageUrls);
        Log.d("request", "submitDataToApi: " + request);

        apiService.editConsult(productId, request, new ApiService.DataCallback<String>() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(EditConsultActivity.this,
                            "Product submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    Toast.makeText(EditConsultActivity.this,
                            "Failed to submit product", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

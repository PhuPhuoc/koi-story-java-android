package com.koistorynew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginButton, registerButton1, registerButton2, faceBtn1, faceBtn2, faceAndEmailBtn1, faceAndEmailBtn2;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton1 = findViewById(R.id.registerButton1);
        registerButton2 = findViewById(R.id.registerButton2);

        faceBtn1 = findViewById(R.id.Face1);
        faceBtn2 = findViewById(R.id.Face2);

        faceAndEmailBtn1 = findViewById(R.id.FaceAndEmail1);
        faceAndEmailBtn2 = findViewById(R.id.FaceAndEmail2);

        loginButton.setOnClickListener(v -> {
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();
            loginUser(emailInput, passwordInput);
        });

        registerButton1.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivityV1.class);
            startActivity(intent);
        });
        registerButton2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        faceBtn1.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
            startActivity(intent);
        });
        faceBtn2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginWithFaceV2Activity.class);
            startActivity(intent);
        });
        faceAndEmailBtn1.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginWithFaceAndEmailV1.class);
            startActivity(intent);
        });
        faceAndEmailBtn2.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginWithFaceAndEmailV2.class);
            startActivity(intent);
        });
    }

    private void loginUser(String emailInput, String passwordInput) {
        String url = "http://api.koistory.site/api/v1/users/login";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", emailInput);
            jsonBody.put("password", passwordInput);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tạo request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log thông tin response
                        Log.d("LoginResponse", response.toString());

                        try {
                            JSONObject data = response.getJSONObject("data");
                            String fbUid = data.getString("fb_uid");
                            String email = data.getString("email");
                            String displayName = data.getString("display_name");
                            String profilePictureUrl = data.getString("profile_picture_url");

                            UserSessionManager.getInstance().setUserData(
                                    fbUid,
                                    email,
                                    displayName,
                                    profilePictureUrl
                            );

                            Toast.makeText(LoginActivity.this, "Welcome, " + displayName, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log lỗi
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String errorMessage = jsonResponse.getString("error"); // Lấy thông báo lỗi
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Nếu không có phản hồi, hiển thị thông báo mặc định
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Thêm request vào RequestQueue
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}

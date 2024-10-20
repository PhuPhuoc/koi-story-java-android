package com.koistorynew;

import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegister, passwordRegister, confirmPasswordRegister, userNameRegister;
    private Button registerConfirmButton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPasswordRegister = findViewById(R.id.confirmPasswordRegister);
        userNameRegister = findViewById(R.id.userNameRegister);
        registerConfirmButton = findViewById(R.id.registerConfirmButton);

        registerConfirmButton.setOnClickListener(v -> {
            String email = emailRegister.getText().toString();
            String password = passwordRegister.getText().toString();
            String confirmPassword = confirmPasswordRegister.getText().toString();
            String userName = userNameRegister.getText().toString();
            registerUser(email, password, userName, confirmPassword);
        });
    }

    private void registerUser(String email, String password, String userName, String confirmPassword) {
        String url = "http://api.koistory.site/api/v1/users/register";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("user_name", userName);
            jsonBody.put("confirm-password", confirmPassword);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return; // Exit early on error
        }

        // Create request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Assuming the response contains a "data" object
                            String message = response.getString("message");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                            // Redirect to LoginActivity or MainActivity after successful registration
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Add request to RequestQueue
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


}

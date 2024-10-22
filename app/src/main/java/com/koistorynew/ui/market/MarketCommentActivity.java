package com.koistorynew.ui.market;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.koistorynew.R;
import com.koistorynew.UserSessionManager;
import com.koistorynew.ui.market.adapter.CommentAdapter;
import com.koistorynew.ui.market.model.PostMarketComment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MarketCommentActivity extends AppCompatActivity {

    private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private List<PostMarketComment> commentList;
    private RequestQueue requestQueue;
    private TextView commentEditText;
    private String productId;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_market_comment);

        initializeViews();
        setupActionBar();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initializeViews() {
        commentEditText = findViewById(R.id.commentEditText);
        ImageButton sendCommentButton = findViewById(R.id.sendCommentButton);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);

        Intent intent = getIntent();
        productId = intent.getStringExtra("POST_ID");
        requestQueue = Volley.newRequestQueue(this);
        userID = UserSessionManager.getInstance().getFbUid();
        commentList = new ArrayList<>();
    }

    private void setupActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Comments");
        }
    }

    private void setupRecyclerView() {
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);
        fetchComments(productId);
    }

    private void setupClickListeners() {
        ImageButton sendCommentButton = findViewById(R.id.sendCommentButton);
        sendCommentButton.setOnClickListener(view -> {
            String newCommentText = commentEditText.getText().toString().trim();
            Log.d("newCommentText", "newCommentText: " + newCommentText);
            if (!newCommentText.isEmpty()) {
                postComment(productId, userID, newCommentText);
                commentEditText.setText("");
            }
            fetchComments(productId);
        });

    }


    private void fetchComments(String postId) {
        commentList.clear();
        commentRecyclerView.removeAllViews();
        String url = "http://api.koistory.site/api/v1/post/" + postId + "/comment";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject commentObject = dataArray.getJSONObject(i);
                            String author = commentObject.getString("username");
                            String date = commentObject.getString("created_at");
                            String comment = commentObject.getString("content");
                            String avatarUrl = commentObject.getString("user_avatar_url");

                            PostMarketComment postMarketComment = new PostMarketComment(author, date, comment, avatarUrl);
                            commentList.add(postMarketComment);
                        }
                        commentAdapter.notifyDataSetChanged();
                        commentRecyclerView.scrollToPosition(commentList.size() - 1);
                    } catch (JSONException e) {
                        Log.e("API_RESPONSE_ERROR", "Error parsing JSON: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("API_RESPONSE_ERROR", "Error fetching data: " + error.getMessage());
                    error.printStackTrace();

                }
        );
        requestQueue.add(jsonObjectRequest);
    }


    private void postComment(String productId, String userID, String content) {
        String url = "http://api.koistory.site/api/v1/post/" + productId + "/comment";
        JSONObject commentData = new JSONObject();
        try {
            commentData.put("content", content);
            commentData.put("user_id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                commentData,
                response -> {
                    Log.d("API_RESPONSE", "Comment posted successfully: " + response.toString());
                    // Fetch comments again after successful posting
                    fetchComments(productId);
                },
                error -> {
                    // Handle 307 redirect
                    if (error.networkResponse != null && error.networkResponse.statusCode == 307) {
                        String redirectUrl = error.networkResponse.headers.get("Location");
                        if (redirectUrl != null) {
                            // Check if redirectUrl is relative, if so prepend the base URL
                            if (!redirectUrl.startsWith("http")) {
                                redirectUrl = "http://api.koistory.site" + redirectUrl;
                            }
                            Log.d("REDIRECT", "Redirecting to: " + redirectUrl);
                            // Re-issue the request to the new location
                            JsonObjectRequest redirectRequest = new JsonObjectRequest(
                                    Request.Method.POST,
                                    redirectUrl,
                                    commentData,
                                    redirectResponse -> {
                                        Log.d("API_RESPONSE", "Comment posted successfully after redirect: " + redirectResponse.toString());
                                        // Fetch comments again
                                        fetchComments(productId);
                                    },
                                    redirectError -> {
                                        Log.e("API_RESPONSE_ERROR", "Error posting comment after redirect: " + redirectError.getMessage());
                                        redirectError.printStackTrace();
                                    }
                            );
                            requestQueue.add(redirectRequest);
                        }
                    } else {
                        Log.e("API_RESPONSE_ERROR", "Error posting comment: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}

package com.koistorynew.ui.market;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.koistorynew.ApiService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_market_comment);

        // Set up back button in ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Comments");
        }
        ImageButton sendCommentButton = findViewById(R.id.sendCommentButton);
        TextView commentEditText = findViewById(R.id.commentEditText);
        Intent intent = getIntent();
        String productId = intent.getStringExtra("PRODUCT_ID");
        requestQueue = Volley.newRequestQueue(this);
        String userID = UserSessionManager.getInstance().getFbUid();

        // Dummy data with avatar URLs
        commentList = new ArrayList<>();

        fetchComments(productId);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        commentRecyclerView.setAdapter(commentAdapter);

        sendCommentButton.setOnClickListener(view -> {
            String newCommentText = commentEditText.getText().toString().trim();
            String userId = userID;

            if (!newCommentText.isEmpty()) {
//                postComment(productId, userId, newCommentText);

                commentEditText.setText("");
            }
        });

    }


    private void fetchComments(String postId) {
        String url = "http://api.koistory.site/api/v1/post/" + postId + "/comment";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject commentObject = dataArray.getJSONObject(i);
                                String author = commentObject.getString("username");
                                String comment = commentObject.getString("content");
                                String avatarUrl = commentObject.getString("user_avatar_url");

                                // Log thông tin bình luận
                                Log.d("API_RESPONSE", "Author: " + author + ", Comment: " + comment + ", Avatar URL: " + avatarUrl);

                                // Add comment to the list
                                PostMarketComment postMarketComment = new PostMarketComment(author, comment, avatarUrl);
                                commentList.add(postMarketComment);
                            }

                            // Notify the adapter that data has changed
                            commentAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("API_RESPONSE_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log any error
                        Log.e("API_RESPONSE_ERROR", "Error fetching data: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
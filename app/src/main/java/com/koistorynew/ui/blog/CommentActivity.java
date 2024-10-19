package com.koistorynew.ui.blog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.koistorynew.R;

public class CommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Thiết lập nút back trên ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Comments"); // Thiết lập tiêu đề ActionBar
        }

        // Get the blog title passed from BlogDetailsActivity
        Intent intent = getIntent();
        String blogTitle = intent.getStringExtra("BLOG_TITLE");

        // Set blog title in the comment activity (for display or processing)
        TextView blogTitleTextView = findViewById(R.id.comment_blog_title);
        blogTitleTextView.setText(blogTitle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go back to the previous page
        return true;
    }
}

package com.koistorynew.ui.blog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.koistorynew.R;
import com.squareup.picasso.Picasso;

public class BlogDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        // Thiết lập nút back trên ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Blog Details");

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("BLOG_TITLE");
        String author = intent.getStringExtra("BLOG_AUTHOR");
        String content = intent.getStringExtra("BLOG_CONTENT");
        String imageUrl = intent.getStringExtra("BLOG_IMAGE");

        // Thiết lập dữ liệu lên UI
        TextView titleTextView = findViewById(R.id.blog_detail_title);
        TextView authorTextView = findViewById(R.id.blog_detail_author);
        TextView contentTextView = findViewById(R.id.blog_detail_content);
        ImageView imageView = findViewById(R.id.blog_detail_image);

        titleTextView.setText(title);
        authorTextView.setText(author);
        contentTextView.setText(content);
        Picasso.get().load(imageUrl).into(imageView);

        // Set up the "View Comments" button
        Button viewCommentsButton = findViewById(R.id.btn_view_comments);
        viewCommentsButton.setOnClickListener(v -> {
            Intent commentIntent = new Intent(BlogDetailsActivity.this, BlogCommentActivity.class);
            commentIntent.putExtra("BLOG_TITLE", title);
            startActivity(commentIntent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Quay lại trang trước đó
        return true;
    }

}

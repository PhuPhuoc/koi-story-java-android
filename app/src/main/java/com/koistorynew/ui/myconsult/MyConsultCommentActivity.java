package com.koistorynew.ui.myconsult;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.R;
import com.koistorynew.ui.myconsult.adapter.MyConsultCommentAdapter;
import com.koistorynew.ui.myconsult.model.MyConsultComment;


import java.util.ArrayList;
import java.util.List;

public class MyConsultCommentActivity extends AppCompatActivity {
    private RecyclerView commentRecyclerView;
    private MyConsultCommentAdapter myConsultCommentAdapter;
    private List<MyConsultComment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_consult_comment);

        // Set up back button in ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Comments consult");
        }
        ImageButton sendCommentButton = findViewById(R.id.sendCommentButton);
        TextView commentEditText = findViewById(R.id.commentEditText);
        Intent intent = getIntent();


        // Dummy data with avatar URLs
        commentList = new ArrayList<>();
        commentList.add(new MyConsultComment("Tèo Phan", "Bởi vì vợ anh 10 là đàn bà nên thích thống kê, đàn bà thường giống nhau.",
                "https://example.com/avatar1.png"));
        commentList.add(new MyConsultComment("Huyền Trang", "Đây là bình luận của Huyền Trang.",
                "https://example.com/avatar2.png"));
        commentList.add(new MyConsultComment("Ngọc Anh", "Ngọc Anh thấy bài viết này rất hay!",
                "https://example.com/avatar3.png"));

        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myConsultCommentAdapter = new MyConsultCommentAdapter(commentList);
        commentRecyclerView.setAdapter(myConsultCommentAdapter);

        sendCommentButton.setOnClickListener(view -> {
            String newCommentText = commentEditText.getText().toString().trim();

            if (!newCommentText.isEmpty()) {
                MyConsultComment newComment = new MyConsultComment("User", newCommentText, "https://example.com/default_avatar.jpg");
                commentList.add(newComment);

                // Notify the adapter about the new comment
                myConsultCommentAdapter.notifyItemInserted(commentList.size() - 1);

                // Clear the input field
                commentEditText.setText("");

                // Scroll to the latest comment
                commentRecyclerView.smoothScrollToPosition(commentList.size() - 1);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}

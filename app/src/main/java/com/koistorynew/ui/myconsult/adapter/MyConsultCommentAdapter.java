package com.koistorynew.ui.myconsult.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.koistorynew.R;
import com.koistorynew.ui.myconsult.model.MyConsultComment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyConsultCommentAdapter extends RecyclerView.Adapter<MyConsultCommentAdapter.CommentViewHolder> {
    private List<MyConsultComment> commentList;

    public MyConsultCommentAdapter(List<MyConsultComment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public MyConsultCommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new MyConsultCommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyConsultCommentAdapter.CommentViewHolder holder, int position) {
        MyConsultComment comment = commentList.get(position);
        holder.userNameTextView.setText(comment.getUserName());
        holder.commentDateTextView.setText(comment.getCommentDate());
        holder.commentTextView.setText(comment.getCommentText());
        Picasso.get().load(comment.getAvatarUrl())
                .error(R.drawable.image_error)
                .into(holder.avatarImageView);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, commentTextView, commentDateTextView;
        ImageView avatarImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.commentUserName);
            commentDateTextView = itemView.findViewById(R.id.commentDate);
            commentTextView = itemView.findViewById(R.id.commentText);
            avatarImageView = itemView.findViewById(R.id.commentAvatar);
        }
    }

}
